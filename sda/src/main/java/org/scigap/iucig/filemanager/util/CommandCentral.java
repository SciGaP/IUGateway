package org.scigap.iucig.filemanager.util;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by swithana on 3/24/14.
 */
public class CommandCentral {
    private static final Logger log = LoggerFactory.getLogger(CommandCentral.class);
    public static final String KERB_PROPERTIES = "kerb.properties";
    public static final String SDA_FILEDOWNLOAD_LOCATION = "file.download.location";
    private List<String> result;
    private static Properties properties = new Properties();

    public String pwd(Session session) throws Exception {

        if (!session.isConnected()) {
            return null;
        }
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
                    if (new String(tmp, 0, i).equals("\n")) break;
                    path = (new String(tmp, 0, i - 1));
                }
                if (channel.isClosed()) {
                    System.out.println("exit-status: " + channel.getExitStatus());
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
            e.printStackTrace();
        } catch (JSchException e) {
            log.error("Auth failure", e.getMessage());
            e.printStackTrace();
        } finally {
            if (channel == null) {
                System.out.println("Channel is null ...");
            } else if (!channel.isClosed()) {
                channel.disconnect();
            }
            in.close();
            session.disconnect();
        }
        return path;
    }

    public String readProperty (String propertyName){
        try {
            URL resource = CommandCentral.class.getClassLoader().getResource(KERB_PROPERTIES);
            if (resource != null){
                properties.load(resource.openStream());
                return properties.getProperty(propertyName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> executeCommand(Session session, String command) throws Exception {
        //FIXME  validate the second part of the command
        result = new ArrayList<String>();
        log.info("COMMAND: " + command);

        if (!session.isConnected()) {
            return null;
        }

        Channel channel = null;
        InputStream in = null;
        try {
            channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);
            channel.setInputStream(null);
            ((ChannelExec) channel).setErrStream(System.err);
            in = channel.getInputStream();
            channel.connect();
            byte[] tmp = new byte[4096];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 4096);
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

    public InputStream scpFrom(Session session, String filename, OutputStream outStream) throws Exception {
        Channel channel = null;
        InputStream in = null;
        String command = "scp -f " + filename;
        String prefix = null;
        if (new File(filename).isDirectory()) {
            prefix = filename + File.separator;
        }
        try {
            channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);
            channel.setInputStream(null);
            ((ChannelExec) channel).setErrStream(System.err);
            // get I/O streams for remote scp
            OutputStream out = channel.getOutputStream();
            in = channel.getInputStream();
            channel.connect();
            byte[] buf = new byte[1024];
            // send '\0'
            buf[0] = 0;
            out.write(buf, 0, 1);
            out.flush();

            while (true) {
                int c = checkAck(in);
                if (c != 'C') {
                    break;
                }
                // read '0644 '
                int read = in.read(buf, 0, 5);
                long filesize = 0L;
                while (true) {
                    if (in.read(buf, 0, 1) < 0) {
                        break;
                    }
                    if (buf[0] == ' ') break;
                    filesize = filesize * 10L + (long) (buf[0] - '0');
                }
                String file = null;
                for (int i = 0; ; i++) {
                    int read1 = in.read(buf, i, 1);
                    if (buf[i] == (byte) 0x0a) {
                        file = new String(buf, 0, i);
                        break;
                    }
                }
                log.info("Downloding file: "+file+" filesize= "+filesize);

                // send '\0'
                buf[0] = 0;
                out.write(buf, 0, 1);
                out.flush();


                String fileDownloadLocation = readProperty(SDA_FILEDOWNLOAD_LOCATION);
                FileOutputStream fos=new FileOutputStream(fileDownloadLocation);

                int foo;
                while(true){
                    if(buf.length<filesize) foo=buf.length;
                    else foo=(int)filesize;
                    foo=in.read(buf, 0, foo);
                    if(foo<0){
                        // error
                        break;
                    }
                    outStream.write(buf, 0, foo);
                    fos.write(buf, 0, foo);
                    filesize-=foo;
                    if(filesize==0L) break;
                }
                fos.close();
                // outStream.close();

                if(checkAck(in)!=0){
                    throw new Exception("Error occurred");
                }

                // send '\0'
                buf[0]=0; out.write(buf, 0, 1); out.flush();

            }

        } catch (FileNotFoundException e1) {
            log.error("Unable to find the file", e1.getMessage());
            throw new Exception(e1.getMessage());
        } catch (JSchException e1) {
            log.error("Auth failure", e1.getMessage());
            throw new Exception(e1.getMessage());
        } catch (IOException e1) {
            log.error("Error occured", e1.getMessage());
            throw new Exception(e1.getMessage());
        } finally {
            if (channel == null) {
                log.error("Channel is null ...");
            } else if (!channel.isClosed()) {
                channel.disconnect();
            }
            // session.disconnect();
        }
        return in;
    }

    public void scpTo(Session session, String filepath,File uploadedFile) throws Exception{
        FileInputStream fis = null;
        try {
            String lfile = "";
            // exec 'scp -t rfile' remotely
            String command = "scp " + " -t " + filepath;
            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);

            // get I/O streams for remote scp
            OutputStream out = channel.getOutputStream();
            InputStream in = channel.getInputStream();

            channel.connect();
            if (checkAck(in) != 0) {
                throw new Exception("Error occured...");
            }

            // send "C0644 filesize filename", where filename should not include '/'
            long filesize = uploadedFile.length();
            command = "C0644 " + filesize + " ";
            if (lfile.lastIndexOf('/') > 0) {
                command += lfile.substring(lfile.lastIndexOf('/') + 1);
            } else {
                command += lfile;
            }
            command += "\n";
            out.write(command.getBytes());
            out.flush();
            if (checkAck(in) != 0) {
                throw new Exception("Error occured...");
//                System.exit(0);
            }

            // send a content of lfile
            fis = new FileInputStream(uploadedFile);
            byte[] buf = new byte[1024];
            while (true) {
                int len = fis.read(buf, 0, buf.length);
                if (len <= 0) break;
                out.write(buf, 0, len); //out.flush();
            }
            fis.close();
            fis = null;
            // send '\0'
            buf[0] = 0;
            out.write(buf, 0, 1);
            out.flush();
            if (checkAck(in) != 0) {
                throw new Exception("Error occured...");
            }
            out.close();

            channel.disconnect();
            session.disconnect();
        } catch (Exception e) {
            log.error("Error occured"+e.getMessage());
            try {
                if (fis != null) fis.close();
            } catch (Exception ee) {
                log.error("File inputstream cannot be closed", ee);
            }
        }
    }

    private int checkAck(InputStream in) throws IOException {
        int b = in.read();
        // b may be 0 for success,
        //          1 for error,
        //          2 for fatal error,
        //          -1
        if (b == 0) return b;
        if (b == -1) return b;

        if (b == 1 || b == 2) {
            StringBuffer sb = new StringBuffer();
            int c;
            do {
                c = in.read();
                sb.append((char) c);
            }
            while (c != '\n');
            if (b == 1) { // error
                log.error(sb.toString());
            }
            if (b == 2) { // fatal error
                log.error(sb.toString());
            }
        }
        return b;
    }
}
