package com.petarvelikov.taxikooperant.model.tcp;

import android.util.Log;

import com.petarvelikov.taxikooperant.model.StatusModel;
import com.petarvelikov.taxikooperant.model.interfaces.MessageObservable;
import com.petarvelikov.taxikooperant.model.interfaces.MessageWriter;
import com.petarvelikov.taxikooperant.model.interfaces.StatusUpdateObservable;
import com.petarvelikov.taxikooperant.model.interfaces.TcpClientController;

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
        StatusUpdateObservable,
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

    private final StatusModel statusModel = new StatusModel(
            StatusModel.NO_LOGGED_DRIVER,
            StatusModel.NOT_CONNECTED,
            StatusModel.NO_LOCATION_SERVICE,
            StatusModel.NOT_CONNECTED
    );
    private BehaviorSubject<StatusModel> statusSubject;
    private PublishSubject<byte[]> dataSubject;

    @Inject
    public TcpClient(NetworkMonitor networkMonitor) {
        this.networkMonitor = networkMonitor;
        isWaitingData = false;
        shouldAutomaticallyReconnect = true;
        serverReconnectAttempts = 0;
        statusSubject = BehaviorSubject.createDefault(statusModel);
        dataSubject = PublishSubject.create();
    }

    @Override
    public void run() {
        shouldAutomaticallyReconnect = true;
        while (shouldAutomaticallyReconnect) {
            // TODO Update network state(CONNECTING)
            // Wait for internet connection
            int attempts = 0;
            while (!networkMonitor.hasInternetConnection()) {
                waitMillis(1000);
                if (attempts++ == 10) {
                    // TODO Update network state(NOT_CONNECTED)
                    waitMillis(5000);
                    attempts = 0;
                }
                if (!shouldAutomaticallyReconnect) {
                    return;
                }
            }
            // Has internet connection
            // TODO Update network state(CONNECTED)
            isWaitingData = true;

            // Connecting to server
            // TODO Update server state(CONNECTING)
            if (serverReconnectAttempts == 3) {
                serverReconnectAttempts = 0;
                // TODO Update server status(NOT_CONNECTED)
                waitMillis(60000);
            } else if (serverReconnectAttempts > 0 && serverReconnectAttempts < 3) {
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
                // TODO Update server status (CONNECTING)
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
        Log.d("Injection", "Successful");
        return dataSubject;
    }

    @Override
    public Observable<StatusModel> getStatusUpdatesObservable() {
        return statusSubject;
    }

    private void readData() throws IOException {
        byte[] buffer = new byte[1024];
        int readBytes = inputStream.read(buffer);
        ArrayList<Byte> bytes = new ArrayList<>();
        for (int i = 0; i < readBytes; ++i) {
            if (i < readBytes - 1) {
                if ((buffer[i] == 'A' && buffer[i + 1] == 'A') || (buffer[i] == 'B' && buffer[i + 1] == 'B')) {
                    if (bytes.size() > 0) {
                        // TODO Parse msg
                        bytes = new ArrayList<>();
                    }
                }
            }
            bytes.add(buffer[i]);
        }
        // TODO parse message
    }

    private void setupSocket() throws IOException {
        socket = new Socket(SERVER_IP, SERVER_PORT);
        socket.setSoTimeout(TIMEOUT);
        inputStream = socket.getInputStream();
        outputStream = new DataOutputStream(socket.getOutputStream());
    }

    private void waitMillis(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
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
        if (isWaitingData) {
            isWaitingData = false;
            shouldAutomaticallyReconnect = false;
        }
        closeSocket();
        closeInputStream();
        closeOutputStream();
        inputStream = null;
        outputStream = null;
        socket = null;
    }
}
