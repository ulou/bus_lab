package org.pwr.model;

/**
 * Created by mkonczyk on 2016-10-25.
 */
public class EncryptionHandler {
    private EncryptionType encryptionType;

    public EncryptionHandler(EncryptionType encryptionType) {
        this.encryptionType = encryptionType;
    }

    public EncryptionType getEncryptionType() {
        return encryptionType;
    }

    public void setEncryptionType(EncryptionType encryptionType) {
        this.encryptionType = encryptionType;
    }
}
