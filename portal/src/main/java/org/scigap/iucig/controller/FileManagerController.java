package org.scigap.iucig.controller;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.json.simple.JSONArray;
import org.scigap.iucig.filemanager.CommandExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;

@Controller
@RequestMapping(value="/filemng/")
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

        String result = "";
        String jsonArray = JSONArray.toJSONString(Arrays.asList(commandExecutor.getResultMap()));
        return jsonArray ;
    }

}
