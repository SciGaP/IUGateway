package org.scigap.iucig.service;

import org.scigap.iucig.filemanager.CommandExecutor;
import org.scigap.iucig.filemanager.util.Item;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.List;

@Path("/filemanager")
public class FileManagerService {
    private static CommandExecutor commandExecutor;

    @GET
    @Path("/command/{command}")
    public List<Item> executeCommand(@PathParam("command") final String command) {
        if(commandExecutor == null) {
            commandExecutor = new CommandExecutor();
        }
        commandExecutor.executeCommand(command);
        return commandExecutor.getResultItemList();
    }
}
