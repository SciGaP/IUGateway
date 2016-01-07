package org.scigap.iucig.filemanager;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import org.scigap.iucig.filemanager.util.CommandCentral;
import org.scigap.iucig.filemanager.util.Constants;
import org.scigap.iucig.filemanager.util.Item;
import org.scigap.iucig.filemanager.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

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
    private static final String LS = "ls -l ";
    private String remoteUser;

    public CommandExecutor(String user) throws JSchException, SftpException, IOException {
        try {
            remoteUser = user;
            kerberosConnector = new KerberosConnector();
            commandCentral = new CommandCentral();
            stringUtils = new StringUtils();
            resultItemList = new ArrayList<Item>();
            //get the current working directory
            pwd();
        }
        catch (IOException e) {
            log.error("Error occurred while getting working directory", e);
            throw new IOException("Error occurred while getting working directory", e);
        } catch (SftpException e) {
            log.error("Error occurred while getting working directory", e);
            throw new SftpException(0, "Error occurred while getting working directory", e);
        } catch (JSchException e) {
            log.error(Constants.ErrorMessages.AUTH_ERROR, e);
            throw new JSchException(Constants.ErrorMessages.AUTH_ERROR);
        }
    }

    public String getRemoteUser() {
        return remoteUser;
    }

    //execute any command
    public void executeCommand(String command) throws JSchException, SftpException, IOException {
        Session session = null;
        String name = "";
        try {
            session = kerberosConnector.getSession(remoteUser);
            List<String> commandList = stringUtils.deconstructCommand(command);
            if (commandList.size() >1){
                if (commandList.get(1) != null){
                    name =  commandList.get(1);
                }
            }

            if (commandList.get(0).equals("cd")) {
                if (commandList.get(1).equals("..")) {
                    pathStack.pop();
                    workingDirectory = stringUtils.constructPathFromStack(pathStack);
                } else if (commandList.get(1).equals("~")){
                    workingDirectory = getHomePath();
                    pathStack = stringUtils.getPathStack(workingDirectory);
                }else {
                    pathStack.push(name);
                    workingDirectory = stringUtils.constructPathFromStack(pathStack);
                }
                command = "ls" + workingDirectory;
                log.info("COMMAND: " + command);
                ls(workingDirectory, session);
                setResultItemList(commandCentral.getItemList());
            } else if (commandList.get(0).equals("mkdir")) {
                String path = workingDirectory + "/" + name;
                command = "mkdir " + path;
                log.info("COMMAND: " + command);
                mkdir(path, session);
                ls(workingDirectory, session);
                setResultItemList(commandCentral.getItemList());
            } else if (commandList.get(0).equals("rm")) {
                String path = workingDirectory + "/" + name;
                command = "rm -r " + path;
                log.info("COMMAND: " + command);
                remove(path, session);
                ls(workingDirectory, session);
                setResultItemList(commandCentral.getItemList());
            } else if (commandList.get(0).equals("ls")) {
                String path = workingDirectory;
                if (commandList.size() > 1){
                    if (commandList.get(1).equals("~")) {
                      path = homePath;
                    }else {
                        path = name;
                    }
                }
                command = LS + path;
                log.info("Command " + command);
                ls(path, session);
                setResultItemList(commandCentral.getItemList());
            } else if (commandList.get(0).equals("mv")) {
                command = "mv " + getWorkingDirectory() + "/" + name + " ";
                String source =  getWorkingDirectory() + "/" + name;
                String target =  "";
                for (int i=2; i < commandList.size(); i++){
                    String fname = commandList.get(i).replaceAll("\\s", "\\\\ ");
                    command +=  "/" + fname ;
                    target +=  "/" + fname ;
                }

                target += "/" + name;
                log.info("COMMAND: " + "mv " + source + " " + target);
                move(source, target, session);
                ls(getWorkingDirectory(), session);
                setResultItemList(commandCentral.getItemList());
            }else if (commandList.get(0).equals("rename")) {
                command = "rename " + getWorkingDirectory() + "/" + name + " ";
                String source =  getWorkingDirectory() + "/" + name;
                String target =  "";
                for (int i=2; i < commandList.size(); i++){
                    String fname = commandList.get(i).replaceAll("\\s", "\\\\ ");
                    command +=  "/" + fname ;
                    target +=  "/" + fname ;
                }
                log.info("COMMAND: " + "rename " + source + " " + target);
                rename(source, target, session);
                ls(getWorkingDirectory(), session);
                setResultItemList(commandCentral.getItemList());
            }
            else if (commandList.get(0).equals("mvr")) {
                String source =  getWorkingDirectory() + "/" + name;
                String target =  "";
                command = "mv -r " + getWorkingDirectory() + "/" + name + " ";
                for (int i=2; i < commandList.size(); i++){
                    String fname = commandList.get(i).replaceAll("\\s", "\\\\ ");
                    command +=  "/" + fname ;
                    target +=  "/" + fname ;
                }
                target += "/" + name;
                log.info("COMMAND: " + "mv " + source + " " + target);
                move(source, target, session);
                ls(getWorkingDirectory(), session);
                setResultItemList(commandCentral.getItemList());
            }
            else if (commandList.get(0).equals("cpr")) {
                command = "cp -r " + getWorkingDirectory() + "/" + name + " ";
                String source =  getWorkingDirectory() + "/" + name;
                String target =  "";
                for (int i=2; i < commandList.size(); i++){
                    String fname;
                    if (commandList.get(i).equals(name)){
                        fname = name;
                    }else {
                        fname= commandList.get(i).replaceAll("\\s", "\\\\ ");
                    }
                    command +=  "/" + fname ;
                    target +=  "/" + fname ;
                }
                log.info("COMMAND: " + "cp " + source + " " + target);
                cp(source, target, session);
                ls(getWorkingDirectory(), session);
                setResultItemList(commandCentral.getItemList());
            } else if (commandList.get(0).equals("freedisk")) {
                command = "du -sh " + workingDirectory;
                log.info("COMMAND: " + command);
                setResult(commandCentral.executeCommand(session, command));
            }else if (commandList.get(0).equals("filecount")) {
                command = "find " + workingDirectory + " -type f | wc -l";
                log.info("COMMAND: " + command);
                setResult(commandCentral.executeCommand(session, command));
            }
        } catch (JSchException e) {
            log.error(Constants.ErrorMessages.AUTH_ERROR,e);
            throw new JSchException(Constants.ErrorMessages.AUTH_ERROR);
        } catch (SftpException e) {
            log.error("Error occurred while executing command : " + command, e);
            throw new SftpException(0,"Error occurred while executing command : " + command, e);
        } catch (IOException e) {
            log.error("Error occurred while executing command : " + command, e);
            throw new IOException("Error occurred while executing command : " + command, e);
        } finally {
            if (session != null) {
                if (session.isConnected()) {
                    session.disconnect();
                }
            }
        }
    }

    //download a file
    public void downloadFile(String filename, OutputStream outputStream) throws JSchException, SftpException, IOException {
        Session session = null;
        try {
            session = kerberosConnector.getSession(remoteUser);
            log.info("DOWNLOADING FILE: " + filename);
            String filepath = workingDirectory + "/" + filename;
            commandCentral.scpFrom(session, filepath, outputStream);
        } catch (JSchException e) {
            log.error(Constants.ErrorMessages.AUTH_ERROR, e);
            throw new JSchException(Constants.ErrorMessages.AUTH_ERROR);
        } catch (SftpException e) {
            log.error("Error while downloading file " + filename, e);
            throw new SftpException(0, "Error while downloading file " + filename, e);
        } catch (IOException e) {
            log.error("Error while downloading file " + filename, e);
            throw new IOException("Error while downloading file " + filename, e);
        } finally {
            if (session != null) {
                if (session.isConnected()) {
                    session.disconnect();
                }
            }
        }

    }

    //upload a file
    public void uploadFile(String filename,InputStream uploadedFile) throws SftpException, JSchException, IOException {
        Session session = null;
        filename = filename.replaceAll("\\s", "\\\\ ");
        String filepath = "";
        try {
            session = kerberosConnector.getSession(remoteUser);
            log.info("UPLOADING FILE: " + filename);
            filepath = workingDirectory + "/" + filename;
            commandCentral.scpToSFTP(session, filepath, uploadedFile);
//            ls();
        } catch (JSchException e) {
            // remove partial uploads
            remove(filepath, session);
            log.error(Constants.ErrorMessages.AUTH_ERROR, e);
            throw new JSchException(Constants.ErrorMessages.AUTH_ERROR);
        } catch (SftpException e) {
            log.error("Error while uploading file " + filename, e);
            throw new SftpException(0,"Error while uploading file " + filename, e);
        } catch (IOException e) {
            log.error("Error while downloading file " + filename, e);
            throw new IOException("Error while uploading file " + filename, e);
        } finally {
            if (session != null) {
                if (session.isConnected()) {
                    session.disconnect();
                }
            }
        }
    }
    //get the home directory
    public String pwd() throws JSchException, SftpException {
        Session session = null;
        try {
            session = kerberosConnector.getSession(remoteUser);
            //getting the home directory
            String path = commandCentral.pwdSFTP(session);
            homePath = path;
            //add it to the stack
            pathStack = stringUtils.getPathStack(path);
            //generate the working directory string using the stack
            workingDirectory = stringUtils.constructPathString(pathStack);
            log.info("CURRENT WORKING DIR: " + workingDirectory);
            log.info("CURRENT PATH: " + path);
        } catch (SftpException e) {
            log.error("Error occurred while getting working directory", e);
            throw new SftpException(0, "Error occurred while getting working directory", e);
        } catch (JSchException e) {
            log.error(Constants.ErrorMessages.AUTH_ERROR, e);
            throw new JSchException(Constants.ErrorMessages.AUTH_ERROR);
        }finally {
            if (session != null) {
                if (session.isConnected()) {
                    session.disconnect();
                }
            }
        }
        return workingDirectory;
    }

    public void ls(String path, Session session) throws SftpException, JSchException {
        try {
            if (!session.isConnected()){
                session = kerberosConnector.getSession(remoteUser);
            }
            List<Item> ls = commandCentral.ls(session, path);
//            if (ls.isEmpty()){
//                ls = commandCentral.ls(session, path);
//            }
            setResultItemList(ls);
        } catch (JSchException e) {
            // construct path stack again
            if (path.contains("/")){
                String[] splitPaths = path.split("/");
                String correctedPath = "/";
                if (splitPaths.length != 0){
                    for (int i=0; i < splitPaths.length -1; i++){
                        correctedPath += splitPaths[i];
                    }
                }
                pathStack = stringUtils.getPathStack(correctedPath);
                workingDirectory = stringUtils.constructPathFromStack(pathStack);
            }else if (path.equals("")){
                pathStack = stringUtils.getPathStack(homePath);
                workingDirectory = stringUtils.constructPathFromStack(pathStack);
            }else {
                pathStack = stringUtils.getPathStack(homePath);
                workingDirectory = stringUtils.constructPathFromStack(pathStack);
            }
            log.error(Constants.ErrorMessages.AUTH_ERROR, e);
            throw new JSchException(Constants.ErrorMessages.AUTH_ERROR);
        } catch (SftpException e) {
            // construct path stack again
            if (path.contains("/")){
                String[] splitPaths = path.split("/");
                String correctedPath = "/";
                if (splitPaths.length != 0){
                    for (int i=0; i < splitPaths.length -1; i++){
                        correctedPath += splitPaths[i];
                    }
                }
                pathStack = stringUtils.getPathStack(correctedPath);
                workingDirectory = stringUtils.constructPathFromStack(pathStack);
            }else if (path.equals("")){
                pathStack = stringUtils.getPathStack(homePath);
                workingDirectory = stringUtils.constructPathFromStack(pathStack);
            }else {
                pathStack = stringUtils.getPathStack(homePath);
                workingDirectory = stringUtils.constructPathFromStack(pathStack);
            }
            log.error("Error occurred while listing files in " + path + "....", e);
            throw new SftpException(0, "Error occurred while listing files in " + path + "....", e);
        } finally {
            if (session != null) {
                if (session.isConnected()) {
                    session.disconnect();
                }
            }
        }
    }

    public void cp(String source, String target, Session session) throws JSchException, SftpException {
        try {
            if (!session.isConnected()){
                session = kerberosConnector.getSession(remoteUser);
            }
            commandCentral.cp(session, source, target);
        } catch (JSchException e) {
            log.error(Constants.ErrorMessages.AUTH_ERROR, e);
            throw new JSchException("Error occurred while copy from " + source + " to destination " + target + "....");
        } catch (SftpException e) {
            throw new SftpException(0,"Error occurred while copy from " + source + " to destination " + target + "....", e);
        } finally {
            if (session != null) {
                if (session.isConnected()) {
                    session.disconnect();
                }
            }
        }
    }

    public void move(String source, String target, Session session) throws SftpException, JSchException {
        try {
            if (!session.isConnected()){
                session = kerberosConnector.getSession(remoteUser);
            }
            commandCentral.move(session, source, target);
        } catch (JSchException e) {
            log.error(Constants.ErrorMessages.AUTH_ERROR, e);
            throw new JSchException(Constants.ErrorMessages.AUTH_ERROR);
        } catch (SftpException e) {
            log.error("Error occured while move from " + source + " to destination " + target + "....", e);
            throw new SftpException(0, "Error occured while move from " + source + " to destination " + target + "....", e);
        } finally {
            if (session != null) {
                if (session.isConnected()) {
                    session.disconnect();
                }
            }
        }
    }

    public void rename(String source, String target, Session session) throws SftpException, JSchException {
        try {
            if (!session.isConnected()){
                session = kerberosConnector.getSession(remoteUser);
            }
            commandCentral.rename(session, source, target);
        } catch (JSchException e) {
            log.error(Constants.ErrorMessages.AUTH_ERROR, e);
            throw new JSchException(Constants.ErrorMessages.AUTH_ERROR);
        } catch (SftpException e) {
            log.error("Error occured while rename " + source + " to " + target + "....", e);
            throw new SftpException(0, "Error occured while rename " + source + " to " + target + "....", e);
        } finally {
            if (session != null) {
                if (session.isConnected()) {
                    session.disconnect();
                }
            }
        }
    }

    public void mkdir(String path, Session session) throws JSchException {
        try {
            if (!session.isConnected()){
                session = kerberosConnector.getSession(remoteUser);
            }
           commandCentral.mkdir(session, path);
        } catch (JSchException e) {
            log.error(Constants.ErrorMessages.AUTH_ERROR, e);
            throw new JSchException(Constants.ErrorMessages.AUTH_ERROR);
        } finally {
            if (session != null) {
                if (session.isConnected()) {
                    session.disconnect();
                }
            }
        }
    }

    public void remove(String path, Session session) throws JSchException, SftpException{
        try {
            if (!session.isConnected()){
                session = kerberosConnector.getSession(remoteUser);
            }
            commandCentral.remove(session, path);
        } catch (JSchException e) {
            log.error(Constants.ErrorMessages.AUTH_ERROR, e);
            throw new JSchException(Constants.ErrorMessages.AUTH_ERROR);
        } catch (SftpException e) {
            log.error("Error occured while remove files " + path + "....", e);
            throw new SftpException(0, "Error occured while remove files " + path + "....", e);
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

    public String getWorkingDirectory() throws SftpException, JSchException {
        if (workingDirectory == null ){
            return pwd();
        }
        return workingDirectory;
    }

    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    public String getHomePath() throws SftpException, JSchException {
        if (homePath == null ){
            return pwd();
        }
        return homePath;
    }

    public void setHomePath(String homePath) {
        this.homePath = homePath;
    }
}
