package org.scigap.iucig.controller;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.scigap.iucig.filemanager.CommandExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping(value="/filemanager/")
public class FileManagerController {

    private static CommandExecutor commandExecutor;

    /** Returns the result of a command using a hash map */
    @ResponseBody
    @RequestMapping(value="/command/{command}", method = RequestMethod.GET)
    public String executeCommand(@PathVariable(value="command") final String command) {
        if(commandExecutor == null) {
            commandExecutor = new CommandExecutor();        
        }
        commandExecutor.executeCommand(command);
        return  commandExecutor.getResultMap().toString();
    }

}
