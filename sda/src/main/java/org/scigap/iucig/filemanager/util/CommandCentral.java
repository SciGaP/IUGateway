package org.scigap.iucig.filemanager.util;

import com.jcraft.jsch.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.*;

public class CommandCentral {
    private static final Logger log = LoggerFactory.getLogger(CommandCentral.class);
    public static final String KERB_PROPERTIES = "kerb.properties";
    public static final String SDA_FILEDOWNLOAD_LOCATION = "file.download.location";
    private List<String> result;
    private static Properties properties = new Properties();

    public String pwdSFTP(Session session) throws Exception {

        if (!session.isConnected()) {
            throw new Exception("Session is not connected...");
        }
        result = new ArrayList<String>();
        String path = "";
        Channel channel = null;
        ChannelSftp c = null;
        try {
            channel = session.openChannel("sftp");
            channel.connect();
            c = (ChannelSftp) channel;
            path = c.pwd();
        } catch (JSchException e) {
            log.error("Auth failure", e);
            throw new Exception(e);
        } finally {
            if (channel == null) {
                System.out.println("Channel is null ...");
            } else if (c != null && !c.isClosed()){
                c.exit();
                c.disconnect();
            }else if (!channel.isClosed()) {
                channel.disconnect();
            }
            session.disconnect();
        }
        return path;
    }

    public String readProperty (String propertyName) throws Exception{
        try {
            URL resource = CommandCentral.class.getClassLoader().getResource(KERB_PROPERTIES);
            if (resource != null){
                properties.load(resource.openStream());
                return properties.getProperty(propertyName);
            }
        } catch (IOException e) {
            throw new Exception("Error while reading properties..", e);
        }
        return null;
    }

    public List<String> ls (Session session, String path) throws Exception {
        result = new ArrayList<String>();
        log.info("COMMAND: ls " + path);

        if (!session.isConnected()) {
            throw new Exception("Session is not connected...");
        }
        Channel channel = null;
        ChannelSftp c = null;
        try {
            channel = session.openChannel("sftp");
            channel.connect();
            c = (ChannelSftp) channel;
            Vector ls = c.ls(path);
            if (ls != null && ls.size() != 0) {
                for (int i = 0; i < ls.size(); i++) {
                    Object obj = ls.elementAt(i);
                    if (obj instanceof com.jcraft.jsch.ChannelSftp.LsEntry) {
                        ChannelSftp.LsEntry lsEntry = (ChannelSftp.LsEntry) obj;
                        if (!lsEntry.getFilename().startsWith(".")) {
                            String string = lsEntry.getLongname();
                            string = string.replaceAll(",", "");
                            string = string.replaceAll("\t", "\\s");
                            System.out.println(string);
                            result.add(string);
                        }

                    }
                }
            }
        } catch (JSchException e) {
            log.error("Auth failure", e);
            throw new Exception(e);
        } finally {
            if (channel == null) {
                System.out.println("Channel is null ...");
            } else if (c != null && !c.isClosed()){
                c.exit();
                c.disconnect();
            }else if (!channel.isClosed()) {
                channel.disconnect();
            }
            session.disconnect();
        }
        return result;
    }


    public void cp (Session session, String source, String target) throws Exception {
        log.info("COMMAND: cp " + source + " " + target);

        if (!session.isConnected()) {
            log.error("Session is not connected");
            throw new Exception("Session is not connected...");
        }
        Channel upChannel = null;
        Channel downChannel = null;
        ChannelSftp uploadChannel = null;
        ChannelSftp downloadChannel = null;
        try {
            upChannel = session.openChannel("sftp");
            downChannel = session.openChannel("sftp");
            upChannel.connect();
            downChannel.connect();
            uploadChannel = (ChannelSftp) upChannel;
            downloadChannel = (ChannelSftp) downChannel;
            if (isFile(uploadChannel, source)){
                InputStream inputStream = uploadChannel.get(source);
                downloadChannel.put(inputStream, target);
            }else {
                copydDir(uploadChannel,downloadChannel, source, target);
            }

        } catch (JSchException e) {
            log.error("Auth failure", e);
            throw new Exception(e);
        } finally {
            if (upChannel == null || downChannel == null) {
                System.out.println("Channel is null ...");
            }else if (uploadChannel != null && !uploadChannel.isClosed() && !downloadChannel.isClosed()){
                uploadChannel.exit();
                downloadChannel.exit();
                uploadChannel.disconnect();
                downloadChannel.disconnect();
            }else if (!upChannel.isClosed() && !downChannel.isClosed()) {
                upChannel.disconnect();
                downChannel.disconnect();
            }
            session.disconnect();
        }
    }

    public void copydDir(ChannelSftp channel1,ChannelSftp channel2, String sourcePath, String destPath) throws SftpException {
        try {
            channel1.mkdir(destPath);
            channel1.cd(destPath);
            // Copy remote folders one by one.
            lsFolderCopy(channel1, channel2, sourcePath, destPath);
        } catch (Exception e) {
            log.error("Error occured while copy folder", e);
            throw new SftpException(0, "Error occured while copy folder" + e);
        }

    }

    private void lsFolderCopy(ChannelSftp channel1,ChannelSftp channel2, String sourcePath, String destPath) throws SftpException { // List source (remote, sftp) directory and create a local copy of it - method for every single directory.
        try {
            Vector ls = channel1.ls(sourcePath);
            if (ls != null && ls.size() != 0) {
                for (int i = 0; i < ls.size(); i++) {
                    Object obj = ls.elementAt(i);
                    if (obj instanceof com.jcraft.jsch.ChannelSftp.LsEntry) {
                        ChannelSftp.LsEntry oListItem = (ChannelSftp.LsEntry) obj;
                        String filename = oListItem.getFilename();
                        if (!oListItem.getAttrs().isDir()) {
                            InputStream inputStream = channel1.get(sourcePath + "/" + filename);
                            String filePath = destPath + "/" + filename;
                            channel2.put(inputStream, filePath);
                        } else if (!(".".equals(filename)) && !("..".equals(filename))) {
                            channel1.mkdir(destPath + "/" + filename);
                            lsFolderCopy(channel1, channel2, sourcePath + "/" + filename, destPath + "/" + filename); // Enter found folder on server to read its contents and create locally.
                        }
                    }
                }
            }
        }catch (SftpException e){
            log.error("Error occured while copy folder", e);
            throw new SftpException(0, "Error occured while copy folder", e);
        }

    }

    public void move (Session session, String source, String target) throws Exception {
        log.info("COMMAND: mv " + source + " " + target);

        if (!session.isConnected()) {
            log.error("Session is not connected");
            throw new Exception("Session is not connected...");
        }
        Channel upChannel = null;
        Channel downChannel = null;
        ChannelSftp uploadChannel = null;
        ChannelSftp downloadChannel = null;
        try {
            upChannel = session.openChannel("sftp");
            downChannel = session.openChannel("sftp");
            upChannel.connect();
            downChannel.connect();
            uploadChannel = (ChannelSftp) upChannel;
            downloadChannel = (ChannelSftp) downChannel;
            InputStream inputStream = downloadChannel.get(source);
            uploadChannel.put(inputStream, target);
            remove(session, source);
        } catch (JSchException e) {
            log.error("Auth failure", e);
            throw new Exception(e);
        } finally {
            if (upChannel == null || downChannel == null) {
                System.out.println("Channel is null ...");
            }else if (uploadChannel != null && !uploadChannel.isClosed() && !downloadChannel.isClosed()){
                uploadChannel.exit();
                downloadChannel.exit();
                uploadChannel.disconnect();
                downloadChannel.disconnect();
            }else if (!upChannel.isClosed() && !downChannel.isClosed()) {
                upChannel.disconnect();
                downChannel.disconnect();
            }
            session.disconnect();
        }
    }

    public void rename (Session session, String source, String target) throws Exception {
        log.info("COMMAND: rename " + source + " " + target);

        if (!session.isConnected()) {
            log.error("Session is not connected");
            throw new Exception("Session is not connected...");
        }
        Channel channel = null;
        ChannelSftp c = null;
        try {
            channel = session.openChannel("sftp");
            channel.connect();
            c = (ChannelSftp) channel;
            c.rename(source, target);
        } catch (JSchException e) {
            log.error("Auth failure", e);
            throw new Exception(e);
        } finally {
            if (channel == null) {
                System.out.println("Channel is null ...");
            }else if (c != null && !c.isClosed()){
                c.exit();
                c.disconnect();
            }else if (!channel.isClosed()) {
                channel.disconnect();
            }
            session.disconnect();
        }
    }

    public void mkdir (Session session, String path) throws Exception {
        log.info("COMMAND: mkdir " + path);

        if (!session.isConnected()) {
            log.error("Session is not connected");
            throw new Exception("Session is not connected");
        }
        Channel channel = null;
        ChannelSftp c = null;
        try {
            channel = session.openChannel("sftp");
            channel.connect();
            c = (ChannelSftp) channel;
            c.mkdir(path);
        } catch (JSchException e) {
            log.error("Auth failure", e);
            throw new Exception(e);
        } finally {
            if (channel == null) {
                System.out.println("Channel is null ...");
            } else if (c != null && !c.isClosed()){
                c.exit();
                c.disconnect();
            }else if (!channel.isClosed()) {
                channel.disconnect();
            }
            session.disconnect();
        }
    }

    public void remove (Session session, String path) throws Exception {
        log.info("COMMAND: rm -rf " + path);

        if (!session.isConnected()) {
            log.error("Session is not connected");
            throw new Exception("Session is not connected");
        }
        Channel channel = null;
        ChannelSftp c = null;
        try {
            channel = session.openChannel("sftp");
            channel.connect();
            c = (ChannelSftp) channel;
            if (isFile(c, path)){
                c.rm(path);
            }else {
                folderDelete(c, path);
            }
        } catch (JSchException e) {
            log.error("Auth failure", e);
            throw new Exception(e);
        } finally {
            if (channel == null) {
                System.out.println("Channel is null ...");
            } else if (c != null && !c.isClosed()){
                c.exit();
                c.disconnect();
            }else if (!channel.isClosed()) {
                channel.disconnect();
            }
            session.disconnect();
        }
    }

    private void folderDelete(ChannelSftp channel, String path) throws SftpException {
        try {
            Vector ls = channel.ls(path);
            if (ls != null && ls.size() == 2){
                channel.rmdir(path);
            }
            if (ls != null && ls.size() > 2) {
                for (int i = 0; i < ls.size(); i++) {
                    Object obj = ls.elementAt(i);
                    if (obj instanceof com.jcraft.jsch.ChannelSftp.LsEntry) {
                        ChannelSftp.LsEntry oListItem = (ChannelSftp.LsEntry) obj;
                        String filename = oListItem.getFilename();
                        if (!oListItem.getAttrs().isDir()) {
                            String filePath = path + "/" + filename;
                            channel.rm(filePath);
                            if (isFolderEmpty(channel,path)){
                                channel.rmdir(path);
                            }
                        } else if (!(".".equals(filename)) && !("..".equals(filename))) {
                            folderDelete(channel, path + "/" + filename);
                            if (isFolderEmpty(channel,path)){
                                channel.rmdir(path);
                            }
                        }
                    }
                }
            }
        }catch (SftpException e){
            log.error("Error occured while copy folder", e);
            throw new SftpException(0, "Error occured while copy folder", e);
        }

    }

    public List<String> executeCommand(Session session, String command) throws Exception {
        //FIXME  validate the second part of the command
        result = new ArrayList<String>();
        log.info("COMMAND: " + command);

        if (!session.isConnected()) {
            throw new Exception("Session is not connected...");
        }
        ChannelExec channel = null;
        InputStream in = null;
        try {
            channel = (ChannelExec)session.openChannel("exec");
            channel.setCommand(command);
            channel.setInputStream(null);
            channel.setErrStream(System.err);
            in = channel.getInputStream();
            channel.connect();

            try {
                Thread.sleep(1000);
            } catch (Exception ee) {
                log.error("Error occured while channel connect", ee.getMessage());
            }
            byte[] tmp = new byte[4096];
//            if (channel.getExitStatus() != 0) {
//                while (true) {
//                    while (in.available() > 0) {
//                        int i = in.read(tmp, 0, 4096);
//                        if (i < 0) break;
//                        result.add(new String(tmp, 0, i));
//                    }
//                    throw new Exception(result.toString());
//                }
//            }else {
//                while (true) {
//                    while (in.available() > 0) {
//                        int i = in.read(tmp, 0, 4096);
//                        if (i < 0) break;
//                        result.add(new String(tmp, 0, i));
//                    }
//                    if (channel.isClosed()) {
//                        System.out.println("exit-status: " + channel.getExitStatus());
//                        break;
//                    }
//                }
//            }

            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 4096);
                    if (i < 0) break;
                    result.add(new String(tmp, 0, i));
                    System.out.println(new String(tmp, 0, i));
                }
                if (channel.isClosed()) {
                    System.out.println("exit-status: " + channel.getExitStatus());
                    if (channel.getExitStatus() !=  0){
                        throw new Exception(result.toString());
                    }
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                    log.error("Error occured while channel connect", ee.getMessage());
                }
            }
        } catch (IOException e) {
            log.error("Error occured while opening channel", e.getMessage());
            throw new Exception(e.getMessage());
        } catch (JSchException e) {
            log.error("Auth failure", e.getMessage());
            throw new Exception(e.getMessage());
        } finally {
            if (channel == null) {
                System.out.println("Channel is null ...");
            } else if (!channel.isClosed()) {
                channel.disconnect();
            }
            session.disconnect();
            in.close();
        }
        return result;
    }

    public void scpFrom(Session session, String filePath, OutputStream outStream) throws Exception {
        Channel channel = null;
        ChannelSftp c = null;
        InputStream inputStream = null;
        try {
            channel = session.openChannel("sftp");
            channel.connect();
            try {
                Thread.sleep(10000);
            } catch (Exception ee) {
                log.error("Error occured while channel connect", ee.getMessage());
            }
            c = (ChannelSftp) channel;
            if (isFile(c, filePath)){
                inputStream = c.get(filePath);
                IOUtils.copy(inputStream, outStream);
                inputStream.close();
                outStream.close();
            }
        } catch (FileNotFoundException e1) {
            log.error("Unable to find the file", e1.getMessage());
//            throw new Exception(e1.getMessage());
        } catch (JSchException e1) {
            log.error("Auth failure", e1.getMessage());
//            throw new Exception(e1.getMessage());
        } catch (IOException e1) {
            log.error("Error occured", e1.getMessage());
            if (channel.isClosed()){
                log.info("***** channel closed *****");
                channel.connect();
                try {
                    Thread.sleep(10000);
                } catch (Exception ee) {
                    log.error("Error occured while channel connect", ee.getMessage());
                }
            }

            c = (ChannelSftp) channel;
            if (isFile(c, filePath)){
                inputStream = c.get(filePath);
                IOUtils.copy(inputStream, outStream);
                inputStream.close();
                outStream.close();
            }
//            throw new Exception(e1.getMessage());
        } finally {
            if (channel == null) {
                System.out.println("Channel is null ...");
            } else if (c != null && !c.isClosed()){
                c.exit();
                c.disconnect();
            }else if (!channel.isClosed()) {
                channel.disconnect();
            }else if (inputStream != null){
                inputStream.close();
            }else if (outStream != null){
                outStream.close();
            }
            session.disconnect();
        }
    }

    public void scpToSFTP(Session session,String filePath, InputStream fileInputStream) throws Exception{
        Channel channel = null;
        ChannelSftp c = null;
        try {
            channel = session.openChannel("sftp");
            channel.connect();
            c = (ChannelSftp) channel;
            c.put(fileInputStream, filePath);
            fileInputStream.close();
        }catch (JSchException e) {
            log.error("Auth failure", e);
            throw new Exception(e);
        } finally {
            if (channel == null) {
                System.out.println("Channel is null ...");
            } else if (c != null && !c.isClosed()){
                c.exit();
                c.disconnect();
            }else if (!channel.isClosed()) {
                channel.disconnect();
            }else if (fileInputStream != null){
                fileInputStream.close();
            }
            session.disconnect();
        }
    }

    public boolean isFile (ChannelSftp channelSftp, String path) throws Exception{
        boolean isFile = false;
        log.info("COMMAND: ls " + path);
        try {
            Vector ls = channelSftp.ls(path);
            if (ls != null){
                System.out.println("file count : " + ls.size());
                if (ls.size() == 1){
                    isFile = true;
                }
            }
        } catch (Exception e) {
            log.error("Auth failure", e);
//            throw new Exception(e);
        }
        return isFile;
    }

    public boolean isFolderEmpty (ChannelSftp channelSftp, String path) throws SftpException{
        boolean isEmpty = false;
        log.info("COMMAND: ls " + path);
        try {
            Vector ls = channelSftp.ls(path);
            if (ls != null){
                System.out.println("file count : " + ls.size());
                if (ls.size() == 2){
                    isEmpty = true;
                }
            }
        } catch (Exception e) {
            log.error("Auth failure", e);
            throw new SftpException(0, "Error..", e);
        }
        return isEmpty;
    }
}
