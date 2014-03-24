package org.scigap.iucig.filemanager;

import com.jcraft.jsch.Session;
import org.scigap.iucig.filemanager.util.CommandCentral;

import java.util.List;

public class CommandExecutor {
    private KerberosConnector kerberosConnector;
    private CommandCentral commandCentral;
    private List<String> result;
    private static List<String> path;

    public CommandExecutor() {
        this.kerberosConnector = new KerberosConnector();
        commandCentral = new CommandCentral();
    }

    public void executeCommand(String command) {
        Session session = kerberosConnector.getSession();

        if (command.equals("pwd")) {
            setResult(commandCentral.pwd(session));
        }

    }

    public List<String> getResult() {
        return result;
    }

    public void setResult(List<String> result) {
        this.result = result;
    }

    public static List<String> getPath() {
        return path;
    }

    public static void setPath(List<String> path) {
        CommandExecutor.path = path;
    }
}
