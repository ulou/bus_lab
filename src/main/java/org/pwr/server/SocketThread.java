package org.pwr.server;

import com.google.gson.Gson;
import org.pwr.model.*;

import javax.crypto.spec.DHParameterSpec;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.net.SocketException;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidParameterSpecException;
import java.util.Random;

import static org.pwr.algorithm.Encryption.decryptMessage;
import static org.pwr.algorithm.Encryption.encryptMessage;
import static org.pwr.algorithm.JsonHandler.readJsonAndSendAnswer;
import static org.pwr.algorithm.JsonHandler.writeJson;

/**
 * Created by mkonczyk on 2016-10-25.
 */
public class SocketThread implements Runnable {

    protected Socket clientSocket = null;
    protected String serverText = null;
    private RequestValues step1;
    private BigInteger b, secret;
    private boolean running = true;
    private EncryptionType encryptionType = EncryptionType.none;
    private Gson gson;

    public SocketThread(Socket clientSocket, String serverText) {
        System.out.println("Connection successful.");
        this.clientSocket = clientSocket;
        this.serverText = serverText;
        gson = new Gson();
        generateBNumber();
        generateKeys();
    }

    public void run() {
        try {
            InputStream input = clientSocket.getInputStream();
            OutputStream output = clientSocket.getOutputStream();
            waitForKeys(input);
            sendKeys(output, step1);
            RequestValues step2A = sendBAndWaitForA(input, output);
            calculateSecret(step2A);
            waitForMessagesOrControl(input, output);

        } catch (SocketException exception) {
            System.out.println("Socket stopped, client has been disconnected.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void calculateSecret(RequestValues step2A) {
        secret = step2A.getA().modPow(b, step1.getP());
        System.out.println("Server side secret: " + secret);
    }

    private RequestValues sendBAndWaitForA(InputStream input, OutputStream output) throws SocketException {
        RequestValues step2A = null;
        RequestValues step2B = new RequestValues(step1.getG().modPow(b, step1.getP()), true);
        while (step2A == null) {
            step2A = step2(input, output, gson.toJson(step2B));
        }
        return step2A;
    }

    private void generateBNumber() {
        Random generator = new Random();
        b = BigInteger.valueOf(generator.nextInt(40));
    }

    private void generateKeys() {
        AlgorithmParameterGenerator paramGen = null;
        try {
            paramGen = AlgorithmParameterGenerator.getInstance("DH");
            paramGen.init(1024);
            AlgorithmParameters params = paramGen.generateParameters();
            DHParameterSpec dhSpec = (DHParameterSpec) params.getParameterSpec(DHParameterSpec.class);
            step1 = new RequestValues(dhSpec.getP(), dhSpec.getG());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidParameterSpecException e) {
            e.printStackTrace();
        }

    }

    public void waitForKeys(InputStream input) {
        while (true) {
            try {
                if ((gson.fromJson(readJsonAndSendAnswer(input, null, null), Request.class)).getRequest().equals("keys")) {
                    System.out.println("Sending request for keys...");
                    break;
                }
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendKeys(OutputStream output, RequestValues step1) {
        writeJson(output, gson.toJson(step1));
    }

    public RequestValues step2(InputStream input, OutputStream outputStream, String msg) throws SocketException {
        String json = readJsonAndSendAnswer(input, outputStream, msg);
        if (json.isEmpty())
            return null;
        System.out.println("Received : " + json.toString());
        RequestValues step2A = gson.fromJson(json.toString(), RequestValues.class);
        if (step2A != null)
            return step2A;

        return null;

    }

    private void waitForMessagesOrControl(InputStream input, OutputStream output) throws SocketException {
        while (running) {
            String msg = readJsonAndSendAnswer(input, null, null);
            System.out.println("Received : " + msg);
            Message message = gson.fromJson(msg, Message.class);
            if (message == null || message.getMsg() == null) {
                handleControlMessage(msg);

            } else {
                handleMessage(output, message);

            }
        }
    }

    private void handleMessage(OutputStream output, Message message) {
        message.setMsg(decryptMessage(encryptionType, message.getMsg(), secret.intValue()));
        System.out.println("------------------------------------");
        System.out.println("Received: \n" + message);
        message.setMsg(encryptMessage(encryptionType, message.getMsg(), secret.intValue()));
        System.out.println("Encrypted: \n" + message);
        System.out.println("------------------------------------");

        writeJson(output, gson.toJson(message));
    }

    private void handleControlMessage(String msg) {
        EncryptionHandler encryptionHandler = gson.fromJson(msg, EncryptionHandler.class);
        if (encryptionHandler != null && encryptionHandler.getEncryptionType() != null) {
            encryptionType = encryptionHandler.getEncryptionType();
            System.out.println("EncyptionType changed to  " + encryptionType);
        } else {
            System.out.println("Something went wrong!");
            this.running = false;
        }
    }


}
