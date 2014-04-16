package org.scigap.iucig.filemanager;

import com.jcraft.jsch.Session;
import org.scigap.iucig.filemanager.util.CommandCentral;
import org.scigap.iucig.filemanager.util.Item;
import org.scigap.iucig.filemanager.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;


/*
* SUPPORTED COMMANDS
*
* cd <directory name>
* mkdir <directory>
* rm <file or directory name>
* rename <filename or directory name>
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
    private List<Item> resultItemList;
    private List<String> result;
    private Map<String, String> resultMap;
    //path stack
    private Stack<String> pathStack;
    private String workingDirectory;
    private static final String LS = "ls -ltr ";
    private String remoteUser;

    public CommandExecutor(String user) {
        try {
            remoteUser = user;
            kerberosConnector = new KerberosConnector();
            commandCentral = new CommandCentral();
            stringUtils = new StringUtils();
            resultItemList = new ArrayList<Item>();
            //get the current working directory
            pwd();
        } catch (Exception e) {
            log.error("Error occured..", e.getMessage());
        }

    }

    //execute any command
    public void executeCommand(String command) throws Exception{
        Session session = null;
        try {
            session = kerberosConnector.getSession(remoteUser);
            List<String> commandList = stringUtils.deconstructCommand(command);
            if (commandList.get(0).equals("cd")) {
                if (commandList.get(1).equals("..")) {
                    pathStack.pop();
                }
                else {
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
                ls();
            } else if (commandList.get(0).equals("rm")) {
                command = "rm -r " + workingDirectory + "/" + commandList.get(1);
                log.info("COMMAND: " + command);
                commandCentral.executeCommand(session, command);
                ls();
            } else if (commandList.get(0).equals("ls")) {
                command = LS;
                log.info("COMMAND: " + command);
                //commandCentral.executeCommand(session, command);
                ls();
            } else if (commandList.get(0).equals("rename")) {
                command = "mv " + workingDirectory + "/" + commandList.get(1) + " " + workingDirectory + "/" + commandList.get(2);
                log.info("COMMAND: " + command);
                commandCentral.executeCommand(session, command);
                ls();
            }
        } catch (Exception e){
            log.error("Error occured", e.getMessage());
            throw new Exception(e.getMessage());
        } finally {
            if (session != null){
                session.disconnect();
            }
        }
    }

    //download a file
    public InputStream downloadFile(String filename) throws Exception{
        Session session = null;
        try {
            session = kerberosConnector.getSession(remoteUser);
            log.info("DOWNLOADING FILE: " + filename);
            return commandCentral.scpFrom(session, filename);
        } catch (Exception e){
            log.error("Error occured", e.getMessage());
            throw new Exception(e.getMessage());
        } finally {
            if (session != null){
                session.disconnect();
            }
        }

    }
    //get the home directory
    public void pwd() throws Exception{
        Session session = null;
        try {
            session = kerberosConnector.getSession(remoteUser);
            //getting the home directory
            String path = commandCentral.pwd(session);
            //add it to the stack
            pathStack = stringUtils.getPathStack(path);
            //generate the working directory string using the stack
            workingDirectory = stringUtils.constructPathString(pathStack);
            log.info("CURRENT WORKING DIR: " + workingDirectory);
            log.info("CURRENT PATH: " + path);
        }catch (Exception e){
            log.error("Error occured", e.getMessage());
            throw new Exception(e.getMessage());
        } finally {
            if (session != null){
                session.disconnect();
            }
        }
    }

    public void ls() throws Exception{
        Session session = null;
        try {
            session = kerberosConnector.getSession(remoteUser);
            setResult(commandCentral.executeCommand(session, LS + workingDirectory));
            setResultMap(stringUtils.categorizeResult(getResult()));
            setResultItemList(stringUtils.getResultsList(getResult()));
        }  catch (Exception e){
            log.error("Error occured", e.getMessage());
            throw new Exception(e.getMessage());
        } finally {
            if (session != null){
                session.disconnect();
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
}
