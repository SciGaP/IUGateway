package org.scigap.iucig.filemanager;

import com.jcraft.jsch.Session;
import org.scigap.iucig.filemanager.util.CommandCentral;
import org.scigap.iucig.filemanager.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Stack;

public class CommandExecutor {
    private static final Logger log = LoggerFactory.getLogger(CommandExecutor.class);

    private KerberosConnector kerberosConnector;
    private CommandCentral commandCentral;
    private StringUtils stringUtils;
    private List<String> result;
    private static List<String> path;
    private static Stack<String> pathStack;
    private List<String> commandList;
    private static String workingDirectory;

    private static final String LS = "ls ";

    public CommandExecutor() {
        kerberosConnector = new KerberosConnector();
        commandCentral = new CommandCentral();
        stringUtils = new StringUtils();

        //get the current working directory
        pwd();
    }

    public void executeCommand(String command) {

        Session session = kerberosConnector.getSession();

        commandList = stringUtils.deconstructCommand(command);

        if (commandList.get(0).equals("cd")) {
            if(commandList.get(1).equals(".."))
                pathStack.pop();
            else
                pathStack.push(commandList.get(1));
            workingDirectory = stringUtils.constructPathFromStack(pathStack);
            command = LS + workingDirectory;
            log.info("COMMAND: " + command);
            setResult(commandCentral.executeCommand(session, command));

        }else if(commandList.get(0).equals("mkdir")) {
            command = "mkdir " + workingDirectory + "/" + commandList.get(1);
            log.info("COMMAND: " + command);
            commandCentral.executeCommand(session, command);
            ls();
        }
    }
    public void pwd() {
        Session session = kerberosConnector.getSession();

        //getting the home directory
        String path = commandCentral.pwd(session);

        //add it to the stack
        pathStack = stringUtils.getPathStack(path);

        //generate the working directory string using the stack
        workingDirectory = stringUtils.constructPathString(pathStack);

        log.info("CURRENT WORKING DIR: "+workingDirectory);
        log.info("CURRENT PATH: "+path.toString());
    }
    public void ls() {
        Session session = kerberosConnector.getSession();
        setResult(commandCentral.executeCommand(session, LS +workingDirectory));

    }

    public List<String> getResult() {
        return result;
    }

    public void setResult(List<String> result) {
        this.result = result;
    }

    public List<String> getPath() {
        return path;
    }

    public void setPath(List<String> path) {
       this.path = path;
    }
}
