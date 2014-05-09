package org.scigap.iucig.controller;

import org.apache.commons.io.IOUtils;
import org.scigap.iucig.filemanager.CommandExecutor;
import org.scigap.iucig.filemanager.util.Item;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.util.List;

@Controller
@Scope("session")
@RequestMapping(value = "/filemanager/")
public class FileManagerController {

    private CommandExecutor commandExecutor;
    /**
     * Returns the result of a command using a Item list
     */
    @ResponseBody
    @RequestMapping(value = "/command/{command}", method = RequestMethod.GET)
    public List<Item> executeCommand(@PathVariable(value = "command") final String command, HttpServletRequest request) throws Exception {
        String remoteUser = request.getRemoteUser();
        String defaultPath = "sda/filemanager/command/";
        String requestURI = request.getRequestURI();
        requestURI = URLDecoder.decode(requestURI, "UTF-8");
        String commandFinal = requestURI.substring(defaultPath.length() + 1, requestURI.length());
        System.out.println("Command : " + commandFinal);
        String mail = "@ADS.IU.EDU";
        if (remoteUser != null) {
            remoteUser = remoteUser.substring(0, remoteUser.length() - mail.length());
            System.out.println("Remote User : " + remoteUser);
            if (commandExecutor == null) {
                 commandExecutor = new CommandExecutor(remoteUser);
            }
            commandExecutor.executeCommand(commandFinal);
            return commandExecutor.getResultItemList();
        }
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "/getPwd", method = RequestMethod.GET)
    public String getPWD(HttpServletRequest request) throws Exception {
        String remoteUser = request.getRemoteUser();
        String mail = "@ADS.IU.EDU";
        if (remoteUser != null) {
            remoteUser = remoteUser.substring(0, remoteUser.length() - mail.length());
            System.out.println("Remote User : " + remoteUser);
            if (commandExecutor == null) {
                commandExecutor = new CommandExecutor(remoteUser);
            }
            return commandExecutor.pwd();
        }
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "/getRemoteUser", method = RequestMethod.GET)
    public String getRemoteUser(HttpServletRequest request) throws Exception {
        return request.getRemoteUser();
    }

    /**
     * Download a file
     */
    @ResponseBody
    @RequestMapping(value = "/download/{user}/{filename}", method = RequestMethod.GET)
    public void downloadFile(@PathVariable(value = "user") final String user, @PathVariable(value = "filename") final String filename, HttpServletResponse response
    ,HttpServletRequest request) throws Exception {
        try {
            String remoteUser = request.getRemoteUser();
            String defaultPath = "sda/filemanager/command/";
            String requestURI = request.getRequestURI();
            requestURI = URLDecoder.decode(requestURI, "UTF-8");
            String commandFinal = requestURI.substring(defaultPath.length() + 1, requestURI.length());
            System.out.println("Command : " + commandFinal);
            String mail = "@ADS.IU.EDU";
            if (remoteUser != null) {
                remoteUser = remoteUser.substring(0, remoteUser.length() - mail.length());
                System.out.println("Remote User : " + remoteUser);
                if (commandExecutor == null) {
                    commandExecutor = new CommandExecutor(remoteUser);
                }
            }
            // get your file as InputStream
            InputStream is = commandExecutor.downloadFile(filename);
            // copy it to response's OutputStream
            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException ex) {
            System.out.println("Error writing file to output stream. Filename :'" + filename);
            throw new RuntimeException("IOError writing file to output stream");
        }
    }

    /**
     * Upload a file
     */
    @ResponseBody
    @RequestMapping(value = "/upload/{user}/", method = RequestMethod.POST)
    public void uploadFile(@PathVariable(value = "user") final String user,@RequestParam("filename") String filename,
                           @RequestParam("file") MultipartFile file,HttpServletRequest request) throws IOException {

        File createdFile = new File(filename);
        file.transferTo(createdFile);

        if (filename.equals(null)) {
            filename = file.getName();
        }

        String remoteUser = request.getRemoteUser();
        String defaultPath = "sda/filemanager/command/";
        String requestURI = request.getRequestURI();
        requestURI = URLDecoder.decode(requestURI, "UTF-8");
        String commandFinal = requestURI.substring(defaultPath.length() + 1, requestURI.length());
        System.out.println("Command : " + commandFinal);
        String mail = "@ADS.IU.EDU";
        if (remoteUser != null) {
            remoteUser = remoteUser.substring(0, remoteUser.length() - mail.length());
            System.out.println("Remote User : " + remoteUser);
            if (commandExecutor == null) {
                commandExecutor = new CommandExecutor(remoteUser);
            }
        }

        try {
            commandExecutor.uploadFile(filename, createdFile);

        } catch (Exception e) {
            System.out.println("Error uploading file ....!!");
            e.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    public String uploadFileHandler(@RequestParam("file") MultipartFile file,
                             HttpServletRequest request) throws Exception {

        System.out.println("**********upload file*********");
        String remoteUser = request.getRemoteUser();
        String mail = "@ADS.IU.EDU";
        if (remoteUser != null) {
            remoteUser = remoteUser.substring(0, remoteUser.length() - mail.length());
            System.out.println("Remote User : " + remoteUser);
            if (commandExecutor == null) {
                commandExecutor = new CommandExecutor(remoteUser);
            }
        }
        String fileName = null;
        if (!file.isEmpty()) {
            fileName = file.getOriginalFilename();
            File createdFile = new File(fileName);
            file.transferTo(createdFile);

            if (fileName == null) {
                fileName = file.getName();
            }
            try {
                commandExecutor.uploadFile(fileName, createdFile);
            } catch (Throwable e) {
                System.out.println("Error uploading file ....!!");
                e.printStackTrace();
            }
            return "You successfully uploaded file=" + fileName;
        } else {
            return "You failed to upload " + fileName
                    + " because the file was empty.";
        }
    }

}
