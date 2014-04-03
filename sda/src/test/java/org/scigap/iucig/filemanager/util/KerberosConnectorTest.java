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
        kerberosConnector = new KerberosConnector();
    }

    @Test
    public void testKerberosConnectivity() {
        kerberosConnector.getSession(remoteUser);
    }
}
