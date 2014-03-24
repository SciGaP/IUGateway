package org.scigap.iucig.filemanager.util;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by swithana on 3/24/14.
 */
public class CommandCentral {
    private static final Logger log = LoggerFactory.getLogger(CommandCentral.class);


    private List<String> result;

    public String pwd(Session session) {
        result = new ArrayList<String>();
        String path = "";

        Channel channel = null;
        InputStream in = null;
        try {
            channel = session.openChannel("exec");
            String command = "pwd";
            ((ChannelExec) channel).setCommand(command);
            channel.setInputStream(null);
            ((ChannelExec) channel).setErrStream(System.err);

            in = channel.getInputStream();

            channel.connect();
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    if(new String(tmp, 0, i).equals("\n")) break;
                     path = (new String(tmp, 0, i-1));
                }
                if (channel.isClosed()) {
                    System.out.println("exit-status: " + channel.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSchException e) {
            e.printStackTrace();
        }finally {
            channel.disconnect();
            session.disconnect();
            return path;
        }

    }
    public List<String> executeCommand(Session session,String command) {
        //FIXME  validate the second part of the command


        result = new ArrayList<String>();
        log.info("COMMAND: "+command);

        Channel channel = null;
        InputStream in = null;
        try {
            channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);
            channel.setInputStream(null);
            ((ChannelExec) channel).setErrStream(System.err);

            in = channel.getInputStream();

            channel.connect();
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    result.add(new String(tmp, 0, i));
                    System.out.println(new String(tmp, 0, i));
                }
                if (channel.isClosed()) {
                    System.out.println("exit-status: " + channel.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSchException e) {
            e.printStackTrace();
        }finally {
            channel.disconnect();
            session.disconnect();
            return result;
        }

    }
}
