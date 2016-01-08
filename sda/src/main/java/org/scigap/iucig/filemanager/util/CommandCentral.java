package org.scigap.iucig.filemanager.util;

import com.jcraft.jsch.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class CommandCentral {
    private static final Logger log = LoggerFactory.getLogger(CommandCentral.class);
    public static final String KERB_PROPERTIES = "kerb.properties";
    private List<String> result;
    private List<Item> itemList;
    private static Properties properties = new Properties();

    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }

    public String pwdSFTP(Session session) throws JSchException, SftpException {

        if (!session.isConnected()) {
            log.error(Constants.ErrorMessages.AUTH_ERROR);
            throw new JSchException(Constants.ErrorMessages.AUTH_ERROR);
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
            log.error(Constants.ErrorMessages.AUTH_ERROR, e);
            throw new JSchException(Constants.ErrorMessages.AUTH_ERROR, e);
        } catch (SftpException e) {
            log.error("Error occurred while getting working directory", e);
            throw new SftpException(1, "Error occurred while getting working directory", e);
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

    public List<Item> ls (Session session, String path) throws JSchException, SftpException {
        log.info("COMMAND: ls " + path);
        itemList = new ArrayList<Item>();
        if (!session.isConnected()) {
            log.error(Constants.ErrorMessages.AUTH_ERROR);
            throw new JSchException(Constants.ErrorMessages.AUTH_ERROR);
        }
        Channel channel = null;
        ChannelSftp c = null;
        StringUtils stringUtils = new StringUtils();
        try {
            channel = session.openChannel("sftp");
            channel.connect();
            c = (ChannelSftp) channel;
            Vector ls = c.ls(path);
            if (ls != null && ls.size() != 0) {
                for (int i = 0; i < ls.size(); i++) {
                    Object obj = ls.elementAt(i);
                    if (obj instanceof ChannelSftp.LsEntry) {
                        ChannelSftp.LsEntry lsEntry = (ChannelSftp.LsEntry) obj;
                        if (!lsEntry.getFilename().startsWith(".")) {
                            SftpATTRS attrs = lsEntry.getAttrs();
                            String fileType = "";
                            if (attrs.isDir()) {
                                fileType = "dir";
                            } else if (attrs.isLink()) {
                                fileType = "symlink";
                            } else {
                                fileType = "file";
                            }
                            long size =  attrs.getSize();
                            long mTime = attrs.getMTime() * 1000L;
                            Date date = new Date(mTime);
                            SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
                            String dateText = df2.format(date);
                            Item item = new Item(lsEntry.getFilename(), dateText, fileType, size);
                            item.setSize(size);
                            item.setPermission(attrs.getPermissionsString());
                            item = stringUtils.updateUserAndGroup(lsEntry.toString(), item);
                            itemList.add(item);
                        }
                    }
                }
            }
        } catch (JSchException e) {
            log.error(Constants.ErrorMessages.AUTH_ERROR, e);
            throw new JSchException(Constants.ErrorMessages.AUTH_ERROR, e);
        } catch (SftpException e) {
            log.error("Error occurred while folder content for path : " + path, e);
            throw new SftpException(1, "Error occurred while folder content for path : " + path, e);
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
        setItemList(itemList);
        return itemList;
    }


    public void cp (Session session, String source, String target) throws JSchException, SftpException {
        log.info("COMMAND: cp " + source + " " + target);

        if (!session.isConnected()) {
            log.error(Constants.ErrorMessages.AUTH_ERROR);
            throw new JSchException(Constants.ErrorMessages.AUTH_ERROR);
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
            throw new JSchException("Auth failure");
        } catch (SftpException e) {
            log.error("Error occurred while copying data from " + source + " to " + target, e);
            throw new SftpException(1, "Error occurred while copying data from " + source + " to " + target , e);
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
        } catch (SftpException e) {
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
            throw new SftpException(0, "Error occurred while copy folder", e);
        }

    }

    public void move (Session session, String source, String target) throws JSchException, SftpException {
        log.info("COMMAND: mv " + source + " " + target);

        if (!session.isConnected()) {
            log.error(Constants.ErrorMessages.AUTH_ERROR);
            throw new JSchException(Constants.ErrorMessages.AUTH_ERROR);
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
            log.error(Constants.ErrorMessages.AUTH_ERROR, e);
            throw new JSchException(Constants.ErrorMessages.AUTH_ERROR, e);
        } catch (SftpException e) {
            log.error("Error occurred while moving data from " + source + " to " + target);
            throw new SftpException(0, "Error occurred while moving data from " + source + " to " + target, e);
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

    public void rename (Session session, String source, String target) throws JSchException, SftpException {
        log.info("COMMAND: rename " + source + " " + target);

        if (!session.isConnected()) {
            log.error(Constants.ErrorMessages.AUTH_ERROR);
            throw new JSchException(Constants.ErrorMessages.AUTH_ERROR);
        }
        Channel channel = null;
        ChannelSftp c = null;
        try {
            channel = session.openChannel("sftp");
            channel.connect();
            c = (ChannelSftp) channel;
            c.rename(source, target);
        } catch (JSchException e) {
            log.error(Constants.ErrorMessages.AUTH_ERROR, e);
            throw new JSchException(Constants.ErrorMessages.AUTH_ERROR, e);
        } catch (SftpException e) {
            log.error("Error occurred while renaming " + source + " to " + target, e);
            throw new SftpException(0, "Error occurred while renaming " + source + " to " + target, e);
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

    public void mkdir (Session session, String path) throws JSchException {
        log.info("COMMAND: mkdir " + path);

        if (!session.isConnected()) {
            log.error(Constants.ErrorMessages.AUTH_ERROR);
            throw new JSchException(Constants.ErrorMessages.AUTH_ERROR);
        }
        Channel channel = null;
        ChannelSftp c = null;
        try {
            channel = session.openChannel("sftp");
            channel.connect();
            c = (ChannelSftp) channel;
            c.mkdir(path);
        } catch (JSchException e) {
            log.error(Constants.ErrorMessages.AUTH_ERROR);
            throw new JSchException(Constants.ErrorMessages.AUTH_ERROR, e);
        } catch (SftpException e) {
            e.printStackTrace();
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

    public void remove (Session session, String path) throws JSchException, SftpException {
        log.info("COMMAND: rm -rf " + path);

        if (!session.isConnected()) {
            log.error(Constants.ErrorMessages.AUTH_ERROR);
            throw new JSchException(Constants.ErrorMessages.AUTH_ERROR);
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
            log.error(Constants.ErrorMessages.AUTH_ERROR);
            throw new JSchException(Constants.ErrorMessages.AUTH_ERROR, e);
        } catch (SftpException e) {
            log.error("Error occurred while deleting " + path, e);
            throw new SftpException(0, "Error occurred while deleting " + path, e);
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
            log.error("Error occurred while copy folder", e);
            throw new SftpException(0, "Error occurred while copy folder", e);
        }

    }

    public List<String> executeCommand(Session session, String command) throws JSchException, IOException {
        //FIXME  validate the second part of the command
        result = new ArrayList<String>();
        log.info("COMMAND: " + command);

        if (!session.isConnected()) {
            log.error(Constants.ErrorMessages.AUTH_ERROR);
            throw new JSchException(Constants.ErrorMessages.AUTH_ERROR);
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
                log.error("Error occurred while channel connect", ee.getMessage());
            }
            byte[] tmp = new byte[4096];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 4096);
                    if (i < 0) break;
                    result.add(new String(tmp, 0, i));
//                    System.out.println(new String(tmp, 0, i));
                }
                if (channel.isClosed()) {
                    System.out.println("exit-status: " + channel.getExitStatus());
                    if (channel.getExitStatus() !=  0){
                        throw new JSchException(result.toString());
                    }
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                    log.error("Error occurred while channel connect", ee.getMessage());
                }
            }
        } catch (IOException e) {
            log.error("Error occurred while opening channel", e.getMessage());
            throw new IOException(e.getMessage(),e);
        } catch (JSchException e) {
            log.error(Constants.ErrorMessages.AUTH_ERROR);
            throw new JSchException(Constants.ErrorMessages.AUTH_ERROR, e);
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

    public void scpFrom(Session session, String filePath, OutputStream outStream) throws JSchException, SftpException, IOException {
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
        } catch (JSchException e1) {
            log.error(Constants.ErrorMessages.AUTH_ERROR, e1);
            throw new JSchException(Constants.ErrorMessages.AUTH_ERROR, e1);
        } catch (IOException e1) {
            log.error("Error occured", e1.getMessage());
            if (channel.isClosed()){
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
        } catch (SftpException e) {
            e.printStackTrace();
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

    public void scpToSFTP(Session session,String filePath, InputStream fileInputStream) throws JSchException, IOException, SftpException {
        Channel channel = null;
        ChannelSftp c = null;
        try {
            channel = session.openChannel("sftp");
            channel.connect();
            c = (ChannelSftp) channel;
            c.put(fileInputStream, filePath);
            fileInputStream.close();
        }catch (JSchException e) {
            log.error(Constants.ErrorMessages.AUTH_ERROR, e);
            throw new JSchException(Constants.ErrorMessages.AUTH_ERROR, e);
        } catch (SftpException e) {
            log.error("Error occurred while copy file to " + filePath, e);
            throw new SftpException(0, "Error occurred while copy file to " + filePath, e);
        } catch (IOException e) {
            log.error("Could not find file", e);
            throw new IOException("Could not find file", e);
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

    public boolean isFile (ChannelSftp channelSftp, String path) throws SftpException{
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
        } catch (SftpException e) {
            log.error("Error occurred while executing command ls " + path, e);
            throw new SftpException(0,"Error occurred while executing command ls " + path, e );
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
        } catch (SftpException e) {
            log.error("Error occurred while executing command ls " + path, e);
            throw new SftpException(0,"Error occurred while executing command ls " + path, e );
        }
        return isEmpty;
    }
}
