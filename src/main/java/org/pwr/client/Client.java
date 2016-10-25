package org.pwr.client;

import com.google.gson.Gson;
import org.pwr.model.EncryptionType;
import org.pwr.model.Message;
import org.pwr.model.Request;
import org.pwr.model.RequestValues;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.net.SocketException;
import java.util.Random;

import static org.pwr.Configuration.HOSTNAME;
import static org.pwr.Configuration.PORT;
import static org.pwr.algorithm.Encryption.decryptMessage;
import static org.pwr.algorithm.JsonHandler.readJsonAndSendOne;

/**
 * Created by mkonczyk on 2016-10-25.
 */
public class Client {
    public static BigInteger p;
    public static BigInteger g;
    public static BigInteger a;
    public static BigInteger A;
    private static BigInteger secret;
    private static EncryptionType encoding = EncryptionType.none;
    private static Gson gson = new Gson();

    public static void main(String args[]) {
        try {
            Socket skt = new Socket(HOSTNAME, PORT);
            InputStream input = skt.getInputStream();
            OutputStream output = skt.getOutputStream();
            System.out.println("Connected");
            generateMyAValue();
            System.out.println("Generated A");
            sendRequestForKeys(output, input);
            RequestValues step2B = sendMyAValueAndWaitForB(output, input);
            calculateSecret(step2B);
        } catch (Exception e) {
            System.out.print("Something went wrong.!\n");
            e.printStackTrace();
        }
    }

    private static void calculateSecret(RequestValues step2B) {
        secret = step2B.getB().modPow(a, p);
        System.out.println("Client secret: " + secret);
    }

    private static void generateMyAValue() {
        Random generator = new Random();
        a = BigInteger.valueOf(generator.nextInt(16));
    }

    public static void sendRequestForKeys(OutputStream outputStream, InputStream inputStream) {
        Request step0 = new Request("keys");
        try {
            String receivedJson = readJsonAndSendOne(inputStream, outputStream, new Gson().toJson(step0));
            System.out.println(receivedJson);
            RequestValues step1 = new Gson().fromJson(receivedJson, RequestValues.class);
            p = step1.getP();
            g = step1.getG();
            System.out.println("Received: P " + p + " G : " + g);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public static RequestValues sendMyAValueAndWaitForB(OutputStream output, InputStream input) throws SocketException {
        RequestValues step2A = new RequestValues(g.modPow(a, p));
        A = step2A.getA();

        String msg = readJsonAndSendOne(input, output, gson.toJson(step2A));
        RequestValues step2B = gson.fromJson(msg, RequestValues.class);
        System.out.println("Received : " + msg);
        return step2B;
    }


    private static String writeAndReadMessage(Message message, InputStream input, OutputStream output) {
        try {
            String json = readJsonAndSendOne(input, output, gson.toJson(message));
            Message receivedMessage = gson.fromJson(json, Message.class);
            receivedMessage.setMsg(decryptMessage(encoding, receivedMessage.getMsg(), secret.intValue()));
            if (receivedMessage.getMsg().isEmpty()) {
                return "Nothing received";
            }
            return receivedMessage.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Something went wrong!";
        }
    }
}
