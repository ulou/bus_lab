package org.pwr.algorithm;

import org.pwr.model.EncryptionType;

import java.util.Base64;

/**
 * Created by mkonczyk on 2016-10-25.
 */

/**
 * Class is responsible for encrypting and decrypting messages using basic algorithms.
 */
public class Encryption {
    /**
     * Method resposible for encrypting message
     *
     * @param encryptionType encryption type, instance of EncryptionType enum
     * @param message        message
     * @param secret         secret code
     * @return encrypted String using Base64 and caesar/xor algorithm
     */
    public static String encryptMessage(EncryptionType encryptionType, String message, Integer secret) {
        message = message.toLowerCase();
        switch (encryptionType) {
            case caesar:
                message = caesar(message, secret % 26);
                break;
            case xor:
                message = new String(xorWithKey(message.getBytes(), intToByteArray(secret)));
                break;
            default:
                // none
        }
        return new String(Base64.getEncoder().encode(message.getBytes()));
    }

    /**
     *  Method resposible for decrypting message
     *
     * @param  encryptionType  encryption type, instance of EncryptionType enum
     * @param  message message
     * @param  secret secret code
     * @return decrypted message of Base64 and caesar/xor algorithm
     */
    public static String decryptMessage(EncryptionType encryptionType, String message, Integer secret) {
        message = new String(Base64.getDecoder().decode(message.getBytes()));
        switch (encryptionType) {
            case caesar:
                message = caesar(message, -(secret % 26));
                break;
            case xor:
                message = new String(xorWithKey(message.getBytes(), intToByteArray(secret)));
                break;
            default:
                // none
        }
        return message;
    }

    /**
     *  Implementation of xor algorithm
     *
     * @param  a array of bytes
     * @param  key algorithm key
     * @return encrypted or decrypted message
     */
    private static byte[] xorWithKey(byte[] a, byte[] key) {
        byte[] out = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            out[i] = (byte) (a[i] ^ key[key.length - 1]);
        }
        System.out.println("xor " + new String(out));
        return out;
    }

    /**
     *  Method that changing integer into byte array
     *
     * @param  value integer value
     * @return bytes array
     */
    public static final byte[] intToByteArray(int value) {
        return new byte[]{
                (byte) (value >>> 24),
                (byte) (value >>> 16),
                (byte) (value >>> 8),
                (byte) value};
    }

    /**
     *  Implementation of caesar cipher algorithm
     *
     * @param  message message
     * @param  shift algorithm shift
     * @return encrypted or decrypted message
     */
    private static String caesar(String message, int shift) {
        char[] buffer = message.toCharArray();
        for (int i = 0; i < buffer.length; i++) {
            char letter = buffer[i];
            if ((letter > 'a' && letter < 'b')) {
                letter = (char) (letter + shift);
                if (letter > 'z') {
                    letter = (char) (letter - 26);
                } else if (letter < 'a') {
                    letter = (char) (letter + 26);
                }
            }
            buffer[i] = letter;
        }
        return new String(buffer);
    }
}
