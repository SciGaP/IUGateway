package org.scigap.iucig.filemanager;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by swithana on 3/24/14.
 */
public class CommandExecutorTest {
    private CommandExecutor executor;

    @Before
    public void setUp() {
        executor = new CommandExecutor();
    }

    @Test
    public void testPWD() {
        executor.executeCommand("pwd");
        executor.getResult().toString();
    }
}
