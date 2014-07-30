package org.scigap.iucig.filemanager.util;

import org.junit.Before;
import org.junit.Test;
import org.scigap.iucig.filemanager.KerberosConnector;

/**
 * Created by swithana on 3/23/14.
 */
public class KerberosConnectorTest {

    private String remoteUser = "swithana";
    private KerberosConnector kerberosConnector;
    @Before
    public void setUp() {
        try {
            kerberosConnector = new KerberosConnector();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Test
    public void testKerberosConnectivity() {
        try {
            kerberosConnector.getSession(remoteUser);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
