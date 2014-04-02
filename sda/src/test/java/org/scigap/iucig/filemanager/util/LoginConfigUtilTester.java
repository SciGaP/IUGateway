package org.scigap.iucig.filemanager.util;

import org.junit.Before;
import org.junit.Test;

public class LoginConfigUtilTester {
    private LoginConfigUtil loginConfigUtil;

    @Before
    public void setUp() {
        loginConfigUtil = new LoginConfigUtil();
    }

    @Test
    public void testCreateLoginFile() {
        System.out.println(loginConfigUtil.createLoginFile("login2.conf","SimpleEcho"));
    }

    @Test
    public void testSearchTicket() {
        System.out.println(loginConfigUtil.searchTicket("SimpleEcho"));
    }


    @Test
    public void testReadProperty() {
        System.out.println(loginConfigUtil.readProperty("kerb.conffile.location"));
    }


}
