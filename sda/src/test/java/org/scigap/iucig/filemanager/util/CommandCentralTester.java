package org.scigap.iucig.filemanager.util;

import com.jcraft.jsch.Session;
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
        commandCentral = new CommandCentral();
        connector = new KerberosConnector();
    }

    @Test
    public void testDownloadFile() throws Exception {
        Session session = connector.getSession(USER);
        InputStream is = commandCentral.scpFrom(session,"/home/swithana/test.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line = null;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    }
}
