package com.petarvelikov.taxikooperant.model.reader;

import android.content.Context;
import android.content.SharedPreferences;

import com.petarvelikov.taxikooperant.constants.Constants;
import com.petarvelikov.taxikooperant.model.messages.AbstractMessage;
import com.petarvelikov.taxikooperant.model.messages.ConfirmMessage;
import com.petarvelikov.taxikooperant.model.messages.ErrorMessage;
import com.petarvelikov.taxikooperant.model.messages.PopupMessage;
import com.petarvelikov.taxikooperant.model.messages.RingBellMessage;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MessageParser {

    // Constants
    private static final int HEADER_LENGTH = 9;
    private static final int CHECKSUM_LENGTH = 2;
    private static final int PADDING_LENGTH = 5;

    private SharedPreferences sharedPreferences;

    @Inject
    public MessageParser(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public AbstractMessage parse(byte[] message) {
        // Check if it is 'OK' message or 'Heartbeat' message
        if (message.length > 2) {
            // Check if it is incoming message
            if (message[0] == 'A' && message[1] == 'A') {
                // Check if the message is for this user
                if (!confirmVehicleId(message)) {
                    return new ErrorMessage("Message sent to wrong device!");
                }
                // Type od command
                if (message.length < HEADER_LENGTH) {
                    return new ErrorMessage("Full message not received");
                }
                String command = (char) message[7] + "" + (char) message[8];
                switch (command) {
                    case "50":
                        return parseRingBellMessage(message);
                    case "45":
                        return parsePopupMessage(message);
                    default:
                        return new ErrorMessage("Command not recognized!");
                }
            } else if (message[0] == 'B' && message[1] == 'B') {
                return new ConfirmMessage();
            } else {
                return new ErrorMessage("Message type not recognized!");
            }
        } else {
            return new ErrorMessage("Full message not received");
        }
    }

    private AbstractMessage parseRingBellMessage(byte[] message) {
        if (message.length < HEADER_LENGTH + 2 + CHECKSUM_LENGTH + PADDING_LENGTH) {
            return new ErrorMessage("Full message not received");
        }
        byte[] tmp = new byte[4];
        tmp[0] = message[9];
        tmp[1] = message[10];
        int timeInSeconds = ByteBuffer.wrap(tmp).order(ByteOrder.LITTLE_ENDIAN).getInt();
        return new RingBellMessage(timeInSeconds);
    }

    private AbstractMessage parsePopupMessage(byte[] message) {
        // Calculate length of message text
        if (message.length < HEADER_LENGTH + 3) {
            return new ErrorMessage("Full message not received");
        }
        int textLength = 0;
        textLength += (message[9] - '0') * 100;
        textLength += (message[10] - '0') * 10;
        textLength += (message[11] - '0');
        if (message.length < HEADER_LENGTH + 3 + textLength + CHECKSUM_LENGTH + PADDING_LENGTH) {
            return new ErrorMessage("Full message not received");
        }
        byte[] textMessageBytes = Arrays.copyOfRange(message, 13, 13 + textLength - 2);
        String textMessage = bytesToString(textMessageBytes);
        byte messageSource = message[13 + textLength - 1];
        return new PopupMessage(textMessage, messageSource);
    }

    private boolean confirmVehicleId(byte[] message) {
//        if (message.length < 7) {
//            return false;
//        }
//        byte[] deviceIdBytes = Arrays.copyOfRange(message, 2, 7);
//        String deviceId = sharedPreferences.getString(Constants.USER_ID, null);
//        return deviceId != null && deviceId.equals(bytesToString(deviceIdBytes));
        return true;
    }

    private String bytesToString(byte[] bytes) {
        String ret = "";
        for (byte b : bytes) {
            ret += (char) b;
        }
        return ret;
    }

}
