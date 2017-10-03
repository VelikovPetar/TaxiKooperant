package com.petarvelikov.taxikooperant.model.writer;

import android.content.SharedPreferences;
import android.location.Location;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MessagesGenerator {

    private static final String USER_ID = "user_id";
    private SharedPreferences sharedPreferences;

    @Inject
    public MessagesGenerator(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public byte[] commonMessage(Location location) {
        byte[] message = new byte[71];

        // Najava na paket
        message[0] = message[1] = (byte) 'A';
        // Broj na vozach
        String userId = sharedPreferences.getString(USER_ID, null);
        userId = padLeft(userId, 5, '0');
        byte[] bytes = userId.getBytes();
        message[2] = bytes[0];
        message[3] = bytes[1];
        message[4] = bytes[2];
        message[5] = bytes[3];
        message[6] = bytes[4];
        // Komanda
        message[7] = (byte) '0';
        message[8] = (byte) '8';
        // PODATOCI
        // Stari podatoci
        float i = 1;
        int bits = Float.floatToIntBits(i);
        message[9] = (byte) (bits);
        message[10] = (byte) (bits >> 8);
        message[11] = (byte) (bits >> 16);
        message[12] = (byte) (bits >> 24);
        // Datum
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(location.getTime()));
        // UTC date
        message[13] = (byte) cal.get(Calendar.DAY_OF_MONTH);
        message[14] = (byte) (cal.get(Calendar.MONTH) + 1);
        message[15] = (byte) (cal.get(Calendar.YEAR) - 2000);
        message[16] = (byte) cal.get(Calendar.HOUR_OF_DAY);
        message[17] = (byte) cal.get(Calendar.MINUTE);
        message[18] = (byte) cal.get(Calendar.SECOND);
        // GPS Data
        // Latitude
        // Latitude degrees
        double latitude = location.getLatitude();
        int degrees = (int) latitude;
        message[19] = (byte) (degrees);
        message[20] = (byte) (degrees >> 8);
        // Latitude minutes
        float minutes = (float) (latitude - degrees) * 60;
        bits = Float.floatToIntBits(minutes);
        message[21] = (byte) (bits);
        message[22] = (byte) (bits >> 8);
        message[23] = (byte) (bits >> 16);
        message[24] = (byte) (bits >> 24);
        // Latitude direction
        message[25] = 'N';
        // Longitude
        // Longitude degrees
        double longitude = location.getLongitude();
        degrees = (int) longitude;
        message[26] = (byte) (degrees);
        message[27] = (byte) (degrees >> 8);
        // Longitude minutes
        minutes = (float) (longitude - degrees) * 60;
        bits = Float.floatToIntBits(minutes);
        message[28] = (byte) (bits);
        message[29] = (byte) (bits >> 8);
        message[30] = (byte) (bits >> 16);
        message[31] = (byte) (bits >> 24);
        // Longitude direction
        message[32] = 'E';
        // Speed in km/h
        float speed = location.getSpeed() * 3.6f;
        bits = Float.floatToIntBits(speed);
        message[33] = (byte) (bits);
        message[34] = (byte) (bits >> 8);
        message[35] = (byte) (bits >> 16);
        message[36] = (byte) (bits >> 24);
        // Number of satellites
        int satellites;
        if (location.getExtras() == null) {
            satellites = 0;
        } else {
            satellites = location.getExtras().getInt("satellites");
        }
        message[37] = (byte) (satellites);
        message[38] = (byte) (satellites >> 8);
        // HDOP
        float hdop = location.getAccuracy();
        bits = Float.floatToIntBits(hdop);
        message[39] = (byte) (bits);
        message[40] = (byte) (bits >> 8);
        message[41] = (byte) (bits >> 16);
        message[42] = (byte) (bits >> 24);
        // Visina
        int altitude = (int) location.getAltitude();
        message[43] = (byte) (altitude);
        message[44] = (byte) (altitude >> 8);
        // Bearing
        float bearing = location.getBearing();
        bits = Float.floatToIntBits(bearing);
        message[45] = (byte) (bits);
        message[46] = (byte) (bits >> 8);
        message[47] = (byte) (bits >> 16);
        message[48] = (byte) (bits >> 24);
        // Binary data 1
        message[49] = binaryData1();
        // Binary data 2
        message[50] = binaryData2();
        // Analog data
        int analogData = 0;
        message[51] = (byte) (analogData);
        message[52] = (byte) (analogData >> 8);
        // KM GPS
        int kmGps = 0;
        message[53] = (byte) (kmGps);
        message[54] = (byte) (kmGps >> 8);
        message[55] = (byte) (kmGps >> 16);
        message[56] = (byte) (kmGps >> 24);
        // KM TAXI
        int kmTaxi = 0;
        message[57] = (byte) (kmTaxi);
        message[58] = (byte) (kmTaxi >> 8);
        message[59] = (byte) (kmTaxi >> 16);
        message[60] = (byte) (kmTaxi >> 24);
        // ID Card
        message[61] = '0';
        message[62] = '0';
        message[63] = '0';
        message[64] = '0';
        message[65] = '0';
        message[66] = '0';
        message[67] = '0';
        message[68] = '0';
        message[69] = '0';
        message[70] = '0';
        // Add checksum
        return addChkSum(message);
    }

    private static byte binaryData1() {
        byte res = 0;
        res |= 1;
        return res;
    }

    private static byte binaryData2() {
        byte res = 0;
        res |= 1 << 1;
        return res;
    }

    private static byte[] addChkSum(byte[] message) {
        byte[] retVal = new byte[message.length + 2];
        for (int i = 0; i < message.length; ++i) {
            retVal[i] = message[i];
        }
        byte tmpByte = (byte) 0;
        for (byte item : message) {
            tmpByte = (byte) (tmpByte ^ item);
        }
        retVal[retVal.length - 1] = (byte) ((tmpByte & 0x0f) | 0x30);
        retVal[retVal.length - 2] = (byte) (((tmpByte & 0xf0) >> 4) | 0x30);
        return retVal;
    }

    private static String padLeft(String text, int length, char paddingChar) {
        if (text == null)
            text = "";
        if (text.length() > length) {
            return text.substring(0, length);
        }
        String ret = text;
        while (ret.length() < length)
            ret = paddingChar + ret;
        return ret;
    }
}
