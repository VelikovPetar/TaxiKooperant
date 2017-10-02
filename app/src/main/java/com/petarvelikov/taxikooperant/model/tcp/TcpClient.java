package com.petarvelikov.taxikooperant.model.tcp;

import android.util.Log;

import com.petarvelikov.taxikooperant.model.interfaces.ConnectionStatusObservable;
import com.petarvelikov.taxikooperant.model.interfaces.MessageObservable;
import com.petarvelikov.taxikooperant.model.interfaces.MessageWriter;
import com.petarvelikov.taxikooperant.model.status.StatusModel;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;

@Singleton
public class TcpClient implements
        Runnable,
        MessageObservable,
        ConnectionStatusObservable,
        MessageWriter {

    private static final String SERVER_IP = "some_ip";
    private static final int SERVER_PORT = 1000;
    private static final int TIMEOUT = 50000;

    private Socket socket;
    private InputStream inputStream;
    private DataOutputStream outputStream;
    private NetworkMonitor networkMonitor;

    private volatile boolean isWaitingData;
    private volatile boolean shouldAutomaticallyReconnect;
    private int serverReconnectAttempts;

    private BehaviorSubject<Integer> networkStatusSubject;
    private BehaviorSubject<Integer> serverStatusSubject;
    private PublishSubject<byte[]> dataSubject;

    @Inject
    public TcpClient(NetworkMonitor networkMonitor) {
        this.networkMonitor = networkMonitor;
        isWaitingData = false;
        shouldAutomaticallyReconnect = true;
        serverReconnectAttempts = 0;
        networkStatusSubject = BehaviorSubject.createDefault(StatusModel.NOT_CONNECTED);
        serverStatusSubject = BehaviorSubject.createDefault(StatusModel.NOT_CONNECTED);
        dataSubject = PublishSubject.create();
    }

    @Override
    public void run() {
        shouldAutomaticallyReconnect = true;
        while (shouldAutomaticallyReconnect) {
            // Wait for internet connection
            int attempts = 0;
            while (!networkMonitor.hasInternetConnection() && shouldAutomaticallyReconnect) {
                networkStatusSubject.onNext(StatusModel.CONNECTING);
                Log.d("TCP", "Connecting...");
                waitMillis(1000);
                if (attempts++ == 10 && shouldAutomaticallyReconnect) {
                    networkStatusSubject.onNext(StatusModel.NOT_CONNECTED);
                    Log.d("TCP", "Not connected");
                    waitMillis(5000);
                    attempts = 0;
                }
                if (!shouldAutomaticallyReconnect) {
                    return;
                }
            }
            // Has internet connection
            networkStatusSubject.onNext(StatusModel.CONNECTED);
            isWaitingData = true;

            // Connecting to server
            serverStatusSubject.onNext(StatusModel.CONNECTING);
            if (serverReconnectAttempts == 3 && shouldAutomaticallyReconnect) {
                serverReconnectAttempts = 0;
                serverStatusSubject.onNext(StatusModel.NOT_CONNECTED);
                waitMillis(60000);
            } else if (serverReconnectAttempts > 0 && serverReconnectAttempts < 3 && shouldAutomaticallyReconnect) {
                waitMillis(3000);
            }
            if (!shouldAutomaticallyReconnect) {
                return;
            }

            // Open socket and wait for data
            try {
                setupSocket();
                serverReconnectAttempts = 0;
                while (isWaitingData) {
                    readData();
                }
            } catch (SocketTimeoutException e) {
                serverStatusSubject.onNext(StatusModel.CONNECTING);
                e.printStackTrace();
                serverReconnectAttempts++;
            } catch (IOException e) {
                waitMillis(300);
                if (networkMonitor.hasInternetConnection()) {
                    serverReconnectAttempts++;
                }
            } finally {
                closeSocket();
                if (!shouldAutomaticallyReconnect) {
                    serverReconnectAttempts = 0;
                }
            }
        }
    }

    @Override
    public boolean writeMessage(byte[] message) {
        return false;
    }

    @Override
    public Observable<byte[]> getMessageObservable() {
        return dataSubject;
    }

    @Override
    public Observable<Integer> getNetworkStatusObservable() {
        return networkStatusSubject;
    }

    @Override
    public Observable<Integer> getServerStatusObservable() {
        return serverStatusSubject;
    }

    private void readData() throws IOException {
        byte[] buffer = new byte[1024];
        int readBytes = inputStream.read(buffer);
        ArrayList<Byte> bytes = new ArrayList<>();
        for (int i = 0; i < readBytes; ++i) {
            if (i < readBytes - 1) {
                if ((buffer[i] == 'A' && buffer[i + 1] == 'A') || (buffer[i] == 'B' && buffer[i + 1] == 'B')) {
                    if (bytes.size() > 0) {
                        dataSubject.onNext(toByteArray(bytes));
                        bytes = new ArrayList<>();
                    }
                }
            }
            bytes.add(buffer[i]);
        }
        dataSubject.onNext(toByteArray(bytes));
    }

    private void setupSocket() throws IOException {
        socket = new Socket(SERVER_IP, SERVER_PORT);
        socket.setSoTimeout(TIMEOUT);
        serverStatusSubject.onNext(StatusModel.CONNECTED);
        inputStream = socket.getInputStream();
        outputStream = new DataOutputStream(socket.getOutputStream());
    }

    private void waitMillis(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d("TCP", "Interrupted exception");
        }
    }

    private void closeSocket() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        closeInputStream();
        closeOutputStream();
    }

    private void closeInputStream() {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void closeOutputStream() {
        if (outputStream != null) {
            try {
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        isWaitingData = false;
        shouldAutomaticallyReconnect = false;
        closeSocket();
        closeInputStream();
        closeOutputStream();
        inputStream = null;
        outputStream = null;
        socket = null;
    }

    private byte[] toByteArray(ArrayList<Byte> list) {
        int size = list.size();
        byte[] bytes = new byte[size];
        for (int i = 0; i < size; ++i) {
            bytes[i] = list.get(i);
        }
        return bytes;
    }
}
