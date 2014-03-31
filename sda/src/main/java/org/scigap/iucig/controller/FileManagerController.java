package org.scigap.iucig.controller;

import org.scigap.iucig.filemanager.CommandExecutor;
import org.scigap.iucig.filemanager.util.Item;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping(value="/filemanager/")
public class FileManagerController {

    private static CommandExecutor commandExecutor;

    /** Returns the result of a command using a hash map */
    @ResponseBody
    @RequestMapping(value="/command/{command}", method = RequestMethod.GET)
    public List<Item> executeCommand(@PathVariable(value="command") final String command) {
        if(commandExecutor == null) {
            commandExecutor = new CommandExecutor();        
        }
        commandExecutor.executeCommand(command);
/*
        String result = "";
        String jsonArray = JSONArray.toJSONString(Arrays.asList(commandExecutor.getResultMap()));*/
        return commandExecutor.getResultItemList();
    }

}
