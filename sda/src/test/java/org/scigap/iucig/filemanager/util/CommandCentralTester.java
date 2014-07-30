package org.scigap.iucig.filemanager.util;

import com.jcraft.jsch.Session;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.scigap.iucig.filemanager.KerberosConnector;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CommandCentralTester {

    private final String USER = "swithana";
    private CommandCentral commandCentral;
    KerberosConnector connector;

    @Before
    public void setUp() {
        try {
            commandCentral = new CommandCentral();
            connector = new KerberosConnector();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Test
    public void testDownloadFile() throws Exception {
        Session session = connector.getSession(USER);
        InputStream is = commandCentral.scpFrom(session,"/home/swithana/test.txt", null);
        /*BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line = null;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }*/

        String myString = IOUtils.toString(is, "UTF-8");
        System.out.println(myString);
    }
}
