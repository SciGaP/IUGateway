package org.scigap.iucig.filemanager.util;

import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;


public class StringUtilsTester {

    private static StringUtils utils;

    @BeforeClass
    public static void setUp() {
        utils = new StringUtils();
    }

    @Test
    public void testDeconstructPath() {
        Assert.assertEquals("[home, swithana]", utils.deconstructPath("/home/swithana").toString());
    }

    @Test
    public void testContructPathString() {
        Assert.assertEquals("/home/swithana",utils.constructPathString(utils.deconstructPath("/home/swithana/")));
    }

    @Test
    public void testDeconstructCommand() {
        Assert.assertEquals("[cd, temp]", utils.deconstructCommand("cd temp").toString());
    }
}
