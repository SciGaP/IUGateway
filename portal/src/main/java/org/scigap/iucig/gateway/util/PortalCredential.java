package org.scigap.iucig.gateway.util;


public class PortalCredential {
    private byte[] pubkey;
    private byte[] priKey;
    private String userName;
    private String passphrase;

    public byte[] getPubkey() {
        return pubkey;
    }

    public void setPubkey(byte[] pubkey) {
        this.pubkey = pubkey;
    }

    public byte[] getPriKey() {
        return priKey;
    }

    public void setPriKey(byte[] priKey) {
        this.priKey = priKey;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassphrase() {
        return passphrase;
    }

    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }
}
