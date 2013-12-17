package org.scigap.iucig.gateway.util;

import org.apache.airavata.credential.store.credential.Credential;

import java.io.Serializable;

/**
 * This class is an extension of Credential class which is specific to BR2
 */
public class BR2Credential extends Credential implements Serializable {

    static final long serialVersionUID = 6603675553790734442L;

    private byte[] privatekey;
    private byte[] pubKey;
    private String passphrase;

    public byte[] getPrivatekey() {
        return privatekey;
    }

    public void setPrivatekey(byte[] privatekey) {
        this.privatekey = privatekey;
    }

    public byte[] getPubKey() {
        return pubKey;
    }

    public void setPubKey(byte[] pubKey) {
        this.pubKey = pubKey;
    }

    public String getPassphrase() {
        return passphrase;
    }

    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }
}
