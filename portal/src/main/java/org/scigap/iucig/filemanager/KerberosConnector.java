package org.scigap.iucig.filemanager;

import com.jcraft.jsch.*;
import org.scigap.iucig.filemanager.util.CommandCentral;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * Created by swithana on 3/23/14.
 */
public class KerberosConnector {
    public Session getSession() {

        String host = "gw110.iu.xsede.org";
        String user = "swithana";
        String  command = "ls -ltr";

        JSch jsch = new JSch();
        jsch.setLogger(new MyLogger());

        System.setProperty("java.security.krb5.conf", "/Users/swithana/git/KerberosConnector/src/main/resources/krb5.conf");
        System.setProperty("java.security.auth.login.config", "/Users/swithana/git/KerberosConnector/src/main/resources/login.conf");
        System.setProperty("javax.security.auth.useSubjectCredsOnly", "false");
        System.setProperty("sun.security.krb5.debug", "true");


        Session session = null;
        try {
            session = jsch.getSession(user, host, 22);
            Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            config.put("PreferredAuthentications",
                    "gssapi-with-mic");

            session.setConfig(config);
            session.connect(20000);

            List<String> result = CommandCentral.pwd(session);
            System.out.println(result.toString());

            System.out.println("DONE");

        } catch (JSchException e) {
            e.printStackTrace();
        }finally {
            return session;
        }
    }
    public static class MyLogger implements com.jcraft.jsch.Logger {
        static java.util.Hashtable name=new java.util.Hashtable();
        static{
            name.put(new Integer(DEBUG), "DEBUG: ");
            name.put(new Integer(INFO), "INFO: ");
            name.put(new Integer(WARN), "WARN: ");
            name.put(new Integer(ERROR), "ERROR: ");
            name.put(new Integer(FATAL), "FATAL: ");
        }
        public boolean isEnabled(int level){
            return true;
        }
        public void log(int level, String message){
            System.err.print(name.get(new Integer(level)));
            System.err.println(message);
        }
    }
}
