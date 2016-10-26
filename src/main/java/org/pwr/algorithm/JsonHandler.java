package org.pwr.algorithm;

import java.io.*;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mkonczyk on 2016-10-25.
 */
public class JsonHandler {
    /**
     * Method resposible for reading JSON
     *
     * @param input   Instance of InputStream
     * @param output  Instance of OutputStream
     * @param sendMsg send message
     * @return String (JSON)
     */
    public static String readJsonAndSendAnswer(InputStream input, OutputStream output, String sendMsg) throws SocketException {
        List<Byte> bytes = new ArrayList<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(input));
        String msg;
        StringBuilder json = new StringBuilder();
        try {
            boolean received = false;
            while (!(received && input.available() == 0)) {
                if (sendMsg != null) {
                    writeJson(output, sendMsg);
                    sendMsg = null;
                }
                int oneByte = input.read();
                if (oneByte > 0) {
                    received = true;
                    bytes.add((byte) oneByte);
                } else {
                    break;
                }
                if (oneByte == 125)
                    break;
            }
        } catch (SocketException e) {
            throw new SocketException();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String received = decodeListOfBytes(bytes.toArray());
        System.out.println("Received : " + received);
        return received;
    }

    /**
     *  Method resposible for writing JSON
     *
     * @param  output  Instance of OutputStream
     * @param  msg  message
     * @return false if JSON wont be written
     */
    public static boolean writeJson(OutputStream output, String msg) {
        System.out.println("Sending" + msg);
        byte[] message = msg.getBytes();
        try {
            output.write(message);
            output.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     *  Method resposible for decoding list of bytes
     *
     * @param  bytes  byte list
     * @return decoded String
     */
    private static String decodeListOfBytes(Object[] bytes) {
        byte[] list = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            list[i] = (Byte) bytes[i];
        }
        return new String(list);
    }

}
