package org.scigap.iucig.controller;

import org.apache.commons.io.IOUtils;
import org.scigap.iucig.filemanager.CommandExecutor;
import org.scigap.iucig.filemanager.util.Item;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/filemanager/")
public class FileManagerController {

    private static CommandExecutor commandExecutor;
    private static Map<String, CommandExecutor> commandExecutorMap = new HashMap<String, CommandExecutor>();

    /**
     * Returns the result of a command using a Item list
     */
    @ResponseBody
    @RequestMapping(value = "/command/{command}", method = RequestMethod.GET)
    public List<Item> executeCommand(@PathVariable(value = "command") final String command, HttpServletRequest request) {
        String remoteUser = request.getRemoteUser();
        String mail = "@ADS.IU.EDU";
        if (remoteUser != null){
            remoteUser = remoteUser.substring(0, remoteUser.length() - mail.length());
            System.out.println("Remote User : " + remoteUser);
            if (!commandExecutorMap.isEmpty()){
                commandExecutor = commandExecutorMap.get(remoteUser);
                if (commandExecutor == null){
                    commandExecutor = new CommandExecutor(remoteUser);
                    commandExecutorMap.put(remoteUser, commandExecutor);
                }
            }else {
                commandExecutor = new CommandExecutor(remoteUser);
                commandExecutorMap.put(remoteUser, commandExecutor);
            }
            commandExecutor.executeCommand(command);
            return commandExecutor.getResultItemList();
        }
        return null;
    }

    /**
     * Download a file
     */
    @ResponseBody
    @RequestMapping(value = "/download/{user}/{filename}", method = RequestMethod.GET)
    public void downloadFile(@PathVariable(value = "user") final String user, @PathVariable(value = "filename") final String filename, HttpServletResponse response) {
        if (commandExecutor == null) {
            commandExecutor = new CommandExecutor(user);
        }
        InputStream in = commandExecutor.downloadFile(filename);
        try {
            IOUtils.copy(in, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
