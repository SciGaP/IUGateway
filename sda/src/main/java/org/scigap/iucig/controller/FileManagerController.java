package org.scigap.iucig.controller;

import org.scigap.iucig.filemanager.CommandExecutor;
import org.scigap.iucig.filemanager.util.Item;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    @RequestMapping(value = "/getRemoteUser", method = RequestMethod.GET)
    public String getRemoteUser(HttpServletRequest request) throws Exception {
        return request.getRemoteUser();
    }

    /**
     * Download a file
     */
    @ResponseBody
    @RequestMapping(value = "/download/{user}/{filename}", method = RequestMethod.GET)
    public void downloadFile(@PathVariable(value = "user") final String user, @PathVariable(value = "filename") final String filename, HttpServletResponse response) throws Exception {
//        if (commandExecutor == null) {
//            commandExecutor = new CommandExecutor(user);
//        }
//        InputStream in = commandExecutor.downloadFile(filename);
//        try {
//            IOUtils.copy(in, response.getOutputStream());
//            response.flushBuffer();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            in.close();
//        }
    }

    /**
     * Upload a file
     */
    @ResponseBody
    @RequestMapping(value = "/download/{user}/{filename}", method = RequestMethod.GET)
    public void uploadFile(@PathVariable(value = "user") final String user, @PathVariable(value = "filename") final String filename, HttpServletResponse response) throws Exception {
//        if (commandExecutor == null) {
//            commandExecutor = new CommandExecutor(user);
//        }
//        InputStream in = commandExecutor.downloadFile(filename);
//        try {
//            IOUtils.copy(in, response.getOutputStream());
//            response.flushBuffer();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            in.close();
//        }
    }

}
