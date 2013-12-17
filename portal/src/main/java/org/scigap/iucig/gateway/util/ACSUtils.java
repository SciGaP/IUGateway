package org.scigap.iucig.gateway.util;


import com.jcraft.jsch.JSch;
import com.jcraft.jsch.KeyPair;
import org.apache.airavata.credential.store.store.CredentialStoreException;
import org.apache.airavata.credential.store.store.impl.CredentialReaderImpl;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

public class ACSUtils {
    public static final String DEFAULT_GATEWAY_NAME = "default";

    public static void main(String[] arg){
         writeToCredentialStore("cpelikan", "koikoi@1");
    }

    public static PortalCredential generateKeyPair(String userName, String passphrase){
        JSch jsch=new JSch();
        try{
            KeyPair kpair=KeyPair.genKeyPair(jsch, KeyPair.RSA);
            File file = File.createTempFile("id_rsa", "");
            String fileName = file.getAbsolutePath();

            kpair.writePrivateKey(fileName,passphrase.getBytes());
            kpair.writePublicKey(fileName + ".pub"  , "");
            kpair.dispose();
            byte[] priKey = FileUtils.readFileToByteArray(new File(fileName));

            byte[] pubKey = FileUtils.readFileToByteArray(new File(fileName + ".pub"));
            PortalCredential portalCredential = new PortalCredential();
            portalCredential.setPriKey(priKey);
            portalCredential.setPubkey(pubKey);
            portalCredential.setUserName(userName);
            portalCredential.setPassphrase(passphrase);
            return portalCredential;
        }
        catch(Exception e){
            System.out.println(e);
        }
        return null;
    }

    public static void writeToCredentialStore(String username, String passPhrase){
        try {
            PortalCredential credential = generateKeyPair(username, passPhrase);
            BR2Credential br2Credential = new BR2Credential();
            br2Credential.setPrivatekey(credential.getPriKey());
            br2Credential.setPubKey(credential.getPubkey());
            br2Credential.setToken(credential.getUserName());
            br2Credential.setPortalUserName(credential.getUserName());
            br2Credential.setPassphrase(credential.getPassphrase());
            br2Credential.setCertificateRequestedTime(getCurrentDate());
            PortalCredentialWriter portalCredentialWriter = new PortalCredentialWriter(dbInfo());
            portalCredentialWriter.writeCredentials(br2Credential);
        } catch (CredentialStoreException e) {
            e.printStackTrace();
        }
    }

    public static BR2Credential readFromCredentialStore(String portalUser){
        try {
            CredentialReaderImpl credentialReader = new CredentialReaderImpl(dbInfo());
            org.apache.airavata.credential.store.credential.Credential credential = credentialReader.getCredential(DEFAULT_GATEWAY_NAME, portalUser);
            return (BR2Credential)credential;
        } catch (CredentialStoreException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static DBConnector dbInfo(){
        try {
            DBConnector dbConnector = new DBConnector("jdbc:mysql://localhost:3306/persistent_data",
                    "iugateway", "iugateway", "com.mysql.jdbc.Driver");
            return dbConnector;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date getCurrentDate (){
        Calendar calender = Calendar.getInstance();
        return calender.getTime();
    }
}
