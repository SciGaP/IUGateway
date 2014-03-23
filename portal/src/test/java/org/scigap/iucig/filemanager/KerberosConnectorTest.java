package org.scigap.iucig.filemanager;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by swithana on 3/23/14.
 */
public class KerberosConnectorTest {

    private KerberosConnector kerberosConnector;
    @Before
    public void setUp() {
        kerberosConnector = new KerberosConnector();
    }

    @Test
    public void testKerberosConnectivity() {
        kerberosConnector.connect();
    }
}
