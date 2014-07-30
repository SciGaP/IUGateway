package org.scigap.iucig.filemanager;

import com.jcraft.jsch.Session;
import org.scigap.iucig.filemanager.util.CommandCentral;
import org.scigap.iucig.filemanager.util.Item;
import org.scigap.iucig.filemanager.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.OutputStream;
import java.util.*;


/*
* SUPPORTED COMMANDS
*
* cd <directory name>
* mkdir <directory>
* rm <file or directory name>
*
* renaming
* mv <file/dir>*<secondArgument>
*
* moving a file
* mv <file>*<location>
*
* get free disk space
* freedisk
*
* COMMANDS WITH TWO ARGUMENTS SHOULD BE PROVIDED WITH A "*" BETWEEN THE ARGUMENTS
* mv <firstArgument>*<secondArgument>
*
*
*
* USAGE
*
* executeCommand( command )
* downloadFile(filename)
*
* to get the results --> getResultsMap()
*
*  */
public class CommandExecutor {
    private static final Logger log = LoggerFactory.getLogger(CommandExecutor.class);

    private KerberosConnector kerberosConnector;
    private CommandCentral commandCentral;
    private StringUtils stringUtils;
    private List<Item> resultItemList = new ArrayList<Item>();
    private List<String> result = new ArrayList<String>();
    private Map<String, String> resultMap = new HashMap<String, String>();
    //path stack
    private Stack<String> pathStack;
    private String workingDirectory;
    private String homePath;
    private static final String LS = "ls -ltr ";
    private String remoteUser;

    public CommandExecutor(String user) throws Exception{
        try {
            remoteUser = user;
            kerberosConnector = new KerberosConnector();
            commandCentral = new CommandCentral();
            stringUtils = new StringUtils();
            resultItemList = new ArrayList<Item>();
            //get the current working directory
            pwd();
        } catch (Exception e) {
            log.error("Error occured..", e);
            throw new Exception("Error occured", e);
        }

    }

    //execute any command
    public void executeCommand(String command) throws Exception {
        Session session = null;
        try {
            session = kerberosConnector.getSession(remoteUser);
            List<String> commandList = stringUtils.deconstructCommand(command);
            if (commandList.get(0).equals("cd")) {
                if (commandList.get(1).equals("..")) {
                    pathStack.pop();
                } else {
                    pathStack.push(commandList.get(1));
                }
                workingDirectory = stringUtils.constructPathFromStack(pathStack);
                command = LS + workingDirectory;
                log.info("COMMAND: " + command);
                setResult(commandCentral.executeCommand(session, command));
                setResultMap(stringUtils.categorizeResult(getResult()));
                setResultItemList(stringUtils.getResultsList(getResult()));
            } else if (commandList.get(0).equals("mkdir")) {
                command = "mkdir " + workingDirectory + "/" + commandList.get(1);
                log.info("COMMAND: " + command);
                commandCentral.executeCommand(session, command);
                ls(workingDirectory);
            } else if (commandList.get(0).equals("rm")) {
                command = "rm -r " + workingDirectory + "/" + commandList.get(1);
                log.info("COMMAND: " + command);
                commandCentral.executeCommand(session, command);
                ls(workingDirectory);
            } else if (commandList.get(0).equals("ls")) {
                String path = workingDirectory;
                if (commandList.size() > 1){
                    path = commandList.get(1);
                }
                command = LS + path;
                log.info("COMMAND: " + command);
                ls(path);
            } else if (commandList.get(0).equals("mv")) {
                command = "mv " + getWorkingDirectory() + "/" + commandList.get(1) + " " + "/";
                for (int i=2; i < commandList.size(); i++){
                    command += commandList.get(i) + "/";
                }
                log.info("COMMAND: " + command);
                commandCentral.executeCommand(session, command);
                ls(getWorkingDirectory());
            }
            else if (commandList.get(0).equals("mvr")) {
                command = "mv -r " + getWorkingDirectory() + "/" + commandList.get(1) + " " + "/";
                for (int i=2; i < commandList.size(); i++){
                    command += commandList.get(i) + "/";
                }
                log.info("COMMAND: " + command);
                commandCentral.executeCommand(session, command);
                ls(getWorkingDirectory());
            }
            //doing an ls after copying to get the corner case of copying it to the same folder
            else if (commandList.get(0).equals("cpr")) {
                command = "cp -r " + getWorkingDirectory() + "/" + commandList.get(1) + " " + "/";
                for (int i=2; i < commandList.size(); i++){
                   command += commandList.get(i) + "/";
                }
                log.info("COMMAND: " + command);
                commandCentral.executeCommand(session, command);
                ls(getWorkingDirectory());
            } else if (commandList.get(0).equals("freedisk")) {
                command = "du -sh " + workingDirectory;
                log.info("COMMAND: " + command);
                setResult(commandCentral.executeCommand(session, command));
            }else if (commandList.get(0).equals("filecount")) {
                command = "find " + workingDirectory + " -type f | wc -l";
                log.info("COMMAND: " + command);
                setResult(commandCentral.executeCommand(session, command));
            }
        } catch (Exception e) {
            log.error("Error occured", e);
            throw new Exception(e);
        } finally {
            if (session != null) {
                if (session.isConnected()) {
                    session.disconnect();
                }
            }
        }
    }

    //download a file
    public void downloadFile(String filename, OutputStream outputStream) throws Exception {
        Session session = null;
        try {
            session = kerberosConnector.getSession(remoteUser);
            log.info("DOWNLOADING FILE: " + filename);
            String filepath = workingDirectory + "/" + filename;
            commandCentral.scpFrom(session, filepath, outputStream);
        } catch (Exception e) {
            log.error("Error occured", e);
            throw new Exception(e);
        } finally {
            if (session != null) {
                if (session.isConnected()) {
                    session.disconnect();
                }
            }
        }

    }

    //upload a file
    public void uploadFile(String filename,File uploadedFile) throws Exception {
        Session session = null;
        try {
            session = kerberosConnector.getSession(remoteUser);
            log.info("DOWNLOADING FILE: " + filename);
            String filepath = workingDirectory + "/" + filename;
            commandCentral.scpTo(session, filepath, uploadedFile);
//            ls();
        } catch (Exception e) {
            log.error("Error occured", e);
            throw new Exception(e);
        } finally {
            if (session != null) {
                if (session.isConnected()) {
                    session.disconnect();
                }
            }
        }
    }
    //get the home directory
    public String pwd() throws Exception {
        Session session = null;
        try {
            session = kerberosConnector.getSession(remoteUser);
            //getting the home directory
            String path = commandCentral.pwd(session);
            homePath = path;
            //add it to the stack
            pathStack = stringUtils.getPathStack(path);
            //generate the working directory string using the stack
            workingDirectory = stringUtils.constructPathString(pathStack);
            log.info("CURRENT WORKING DIR: " + workingDirectory);
            log.info("CURRENT PATH: " + path);
        } catch (Exception e) {
            log.error("Error occured", e);
            throw new Exception(e);
        } finally {
            if (session != null) {
                if (session.isConnected()) {
                    session.disconnect();
                }
            }
        }
        return workingDirectory;
    }

    public void ls(String path) throws Exception {
        Session session = null;
        try {
            session = kerberosConnector.getSession(remoteUser);
            setResult(commandCentral.executeCommand(session, LS + path));
            setResultMap(stringUtils.categorizeResult(getResult()));
            setResultItemList(stringUtils.getResultsList(getResult()));
        } catch (Exception e) {
            log.error("Error occured", e);
            throw new Exception(e);
        } finally {
            if (session != null) {
                if (session.isConnected()) {
                    session.disconnect();
                }
            }
        }
    }

    public Map<String, String> getResultMap() {
        return resultMap;
    }

    public void setResultMap(Map<String, String> resultMap) {
        this.resultMap = resultMap;
    }

    public List<String> getResult() {
        return result;
    }

    public void setResult(List<String> result) {
        this.result = result;
    }

    public List<Item> getResultItemList() {
        return resultItemList;
    }

    public void setResultItemList(List<Item> resultItemList) {
        this.resultItemList = resultItemList;
    }

    public String getWorkingDirectory() throws Exception {
        if (workingDirectory == null ){
            return pwd();
        }
        return workingDirectory;
    }

    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    public String getHomePath() throws Exception {
        if (homePath == null ){
            String pwd = pwd();
            System.out.println("***** Home Dir ***** : " + pwd);
            return pwd;
        }
        return homePath;
    }

    public void setHomePath(String homePath) {
        this.homePath = homePath;
    }
}
