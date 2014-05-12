package org.scigap.iucig.filemanager.util;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.scigap.iucig.filemanager.CommandExecutor;

import java.io.InputStream;

public class CommandExecutorTest {
    private CommandExecutor executor;
    private String user = "swithana";
    @Before
    public void setUp() {
        executor = new CommandExecutor(user);
    }

    @Test
    public void testCD() {
        try {
            executor.executeCommand("cd temp");
            executor.getResultItemList().toString();
        /*executor.executeCommand("cd ..");
        executor.getResultItemList().toString();
        executor.executeCommand("cd temp");
        executor.getResultItemList().toString();
        executor.executeCommand("cd test");
        executor.getResultItemList().toString();*/
        } catch (Exception e){
            e.printStackTrace();
        }

    }
    @Test
    public void testPWD() throws Exception {
        try {
            executor.pwd();
//        executor.getResult().toString();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    @Test
    public void testMkdir() {
        try {
            executor.executeCommand("cd temp");
            executor.getResult().toString();
            executor.executeCommand("mkdir ssd");
            executor.getResult().toString();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testRename() {
        try {
            executor.executeCommand("cd temp");
            executor.getResult().toString();
            executor.executeCommand("rename ssh ssd");
            executor.getResult().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //do not use the rm test unless it is absolutely necessary
    @Test
    public void testRM() {
        try {
            executor.executeCommand("cd temp");
            executor.getResult().toString();
            executor.executeCommand("rm ssd");
            executor.getResult().toString();
        } catch (Exception e){
            e.printStackTrace();
        }

    }
    @Test
    public void testHashMapResults() {
        try {
            executor.executeCommand("cd temp");
            System.out.println("PRINTING RESULTS MAP");
            System.out.println(executor.getResult());
            System.out.println(executor.getResultMap());
            System.out.println(executor.getResultMap().size());
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    @Test
    public void testDownloadFile() {
        try {
            InputStream is = executor.downloadFile("test.txt");
            System.out.println("Executing SCP From... file: test.txt");

            String myString = IOUtils.toString(is, "UTF-8");
            System.out.println(myString);
//        System.out.println(executor.getResult());
            //      System.out.println(executor.getResultMap());
            //    System.out.println(executor.getResultMap().size());
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
