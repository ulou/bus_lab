package org.pwr.algorithm;

import org.pwr.model.EncryptionType;

import java.util.Base64;

/**
 * Created by mkonczyk on 2016-10-25.
 */
public class Encryption {
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

    private static byte[] xorWithKey(byte[] a, byte[] key) {
        byte[] out = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            out[i] = (byte) (a[i] ^ key[key.length - 1]);
        }
        System.out.println("xor " + new String(out));
        return out;
    }

    public static final byte[] intToByteArray(int value) {
        return new byte[]{
                (byte) (value >>> 24),
                (byte) (value >>> 16),
                (byte) (value >>> 8),
                (byte) value};
    }

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
