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
import static org.pwr.algorithm.Encryption.encryptMessage;
import static org.pwr.algorithm.JsonHandler.readJsonAndSendAnswer;

/**
 * Created by mkonczyk on 2016-10-25.
 */
public class Client {
    public static BigInteger p, g, a, A, secret;
    private static EncryptionType encryptionType = EncryptionType.none;
    private static Gson gson = new Gson();

    public static void main(String args[]) {
        try {
            Socket skt = new Socket(HOSTNAME, PORT);
            InputStream input = skt.getInputStream();
            OutputStream output = skt.getOutputStream();
            System.out.println("Connected to server.");
            generateMyAValue();
            System.out.println("Generated A value.");
            sendRequestForKeys(output, input);
            RequestValues step2B = sendMyAValueAndWaitForB(output, input);
            calculateSecret(step2B);

//---------------- INPUT DATA ------------------------
            System.out.println("-------------------------------------------");
            encryptionType = EncryptionType.none;
            System.out.println(
                    writeAndReadMessage(
                            new Message(encryptMessage(encryptionType, "test none", secret.intValue()), "test client"),
                            input,
                            output));
            System.out.println("-------------------------------------------");
            encryptionType = EncryptionType.caesar;
            System.out.println(
                    writeAndReadMessage(
                            new Message(encryptMessage(encryptionType, "test caesar", secret.intValue()), "test client"),
                            input,
                            output));
            System.out.println("-------------------------------------------");
            encryptionType = EncryptionType.xor;
            System.out.println(
                    writeAndReadMessage(
                            new Message(encryptMessage(encryptionType, "test xor", secret.intValue()), "test client"),
                            input,
                            output));
            System.out.println("-------------------------------------------");
//---------------- /INPUT DATA ------------------------

        } catch (Exception e) {
            System.out.print("Something went wrong.!\n");
            e.printStackTrace();
        }
    }

    private static void calculateSecret(RequestValues step2B) {
        secret = step2B.getB().modPow(a, p); // whats wrong here?
        System.out.println("Client secret: " + secret);
    }

    private static void generateMyAValue() {
        Random generator = new Random();
        a = BigInteger.valueOf(generator.nextInt(40));
    }

    public static void sendRequestForKeys(OutputStream outputStream, InputStream inputStream) {
        Request step0 = new Request("keys");
        try {
            String receivedJson = readJsonAndSendAnswer(inputStream, outputStream, new Gson().toJson(step0));
            System.out.println(receivedJson);
            RequestValues step1 = new Gson().fromJson(receivedJson, RequestValues.class);
            p = step1.getP();
            g = step1.getG();
            System.out.println("Received: \nP " + p + "\nG : " + g);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public static RequestValues sendMyAValueAndWaitForB(OutputStream output, InputStream input) throws SocketException {
        RequestValues step2A = new RequestValues(g.modPow(a, p), false);
        A = step2A.getA();

        String msg = readJsonAndSendAnswer(input, output, gson.toJson(step2A));
        RequestValues step2B = gson.fromJson(msg, RequestValues.class);
        System.out.println("Received : " + msg);
        return step2B;
    }


    private static String writeAndReadMessage(Message message, InputStream input, OutputStream output) {
        try {
            String json = readJsonAndSendAnswer(input, output, gson.toJson(message));
            Message receivedMessage = gson.fromJson(json, Message.class);
            receivedMessage.setMsg(decryptMessage(encryptionType, receivedMessage.getMsg(), secret.intValue()));
            if (receivedMessage.getMsg().isEmpty()) {
                return "Empty message";
            }
            return receivedMessage.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Something went wrong!";
        }
    }
}
