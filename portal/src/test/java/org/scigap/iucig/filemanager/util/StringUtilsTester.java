package org.scigap.iucig.filemanager.util;

import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


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
        System.out.println(utils.constructPathString(utils.deconstructPath("/home/swithana/")));
    }
}
