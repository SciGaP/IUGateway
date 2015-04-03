package org.scigap.iucig.controller;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.scigap.iucig.filemanager.CommandExecutor;
import org.scigap.iucig.filemanager.util.Item;
import org.scigap.iucig.service.UserService;
import org.scigap.iucig.util.ViewNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.List;
import java.util.Properties;

@Controller
@Scope("session")
@RequestMapping(value = "/filemanager")
public class FileManagerController {
    private static final Logger log = LoggerFactory.getLogger(FileManagerController.class);
    public static final String PORTAL_URL = "portal.url";
    public static final String KERB_PROPERTIES = "kerb.properties";
    private Properties properties = new Properties();
    private CommandExecutor commandExecutor;
    private AuthProvider authProvider;

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        log.info("Logging out of SDA Web Interface");
        SecurityContextHolder.getContext().setAuthentication(null);
        if(request.getSession(false)!=null) {
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);
            request.getSession(false).invalidate();
        }
        response.setStatus(HttpStatus.OK.value());
    }

    @ResponseBody
    @RequestMapping(value = "/getRemoteUser", method = RequestMethod.GET)
    public String getRemoteUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String remoteUser = request.getRemoteUser();
        String mail = "@ADS.IU.EDU";
        if (remoteUser != null) {
            remoteUser = remoteUser.substring(0, remoteUser.length() - mail.length());
            UsernamePasswordAuthenticationToken token =
                    new UsernamePasswordAuthenticationToken(remoteUser, remoteUser);
            token.setDetails(new WebAuthenticationDetails(request));
            authProvider = new AuthProvider();
            Authentication auth = authProvider.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        return remoteUser;
    }
    /**
     * Returns the result of a command using a Item list
     */
    @ResponseBody
    @RequestMapping(value = "/command/{command}", method = RequestMethod.GET)
    public List<Item> executeCommand(@PathVariable(value = "command") final String command, HttpServletRequest request) throws Exception {
        String remoteUser = request.getRemoteUser();
        String defaultPath = "sda/filemanager/command/";
        String requestURI = request.getRequestURI();
//      requestURI = URLDecoder.decode(requestURI, "ASCII");
        String commandFinal = requestURI.substring(defaultPath.length() + 1, requestURI.length());
        String decodedCommand = "";
        if (commandFinal.contains("+")){
            String[] strings = commandFinal.split("\\+");
            decodedCommand = URLDecoder.decode(strings[0], "UTF-8");
            for (int i = 1; i < strings.length; i++){
                decodedCommand += "+" + URLDecoder.decode(strings[i], "UTF-8");
            }
        }else {
            decodedCommand = URLDecoder.decode(commandFinal, "UTF-8");
        }
        System.out.println("Command : " + decodedCommand);
        String mail = "@ADS.IU.EDU";
        if (remoteUser != null) {
            remoteUser = remoteUser.substring(0, remoteUser.length() - mail.length());
            System.out.println("Remote User : " + remoteUser);
            if (commandExecutor == null) {
                 commandExecutor = new CommandExecutor(remoteUser);
            }
            commandExecutor.executeCommand(decodedCommand);
            return commandExecutor.getResultItemList();
        }
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "/usedSpace", method = RequestMethod.GET)
    public String getUsedSpace(HttpServletRequest request) throws Exception {
        String remoteUser = request.getRemoteUser();
        String mail = "@ADS.IU.EDU";
        if (remoteUser != null) {
            remoteUser = remoteUser.substring(0, remoteUser.length() - mail.length());
            System.out.println("Remote User : " + remoteUser);
            if (commandExecutor == null) {
                commandExecutor = new CommandExecutor(remoteUser);
            }
            commandExecutor.executeCommand("freedisk");
            List<String> list = commandExecutor.getResult();
            if (list != null && !list.isEmpty()){
                String result = list.get(0);
                String[] strings = result.split("\\t");
                System.out.println("**** result **** " + result);
                return strings[0];
            }
        }
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "/fileCount", method = RequestMethod.GET)
    public String getFileCount(HttpServletRequest request) throws Exception {
        String remoteUser = request.getRemoteUser();
        String mail = "@ADS.IU.EDU";
        if (remoteUser != null) {
            remoteUser = remoteUser.substring(0, remoteUser.length() - mail.length());
            System.out.println("Remote User : " + remoteUser);
            if (commandExecutor == null) {
                commandExecutor = new CommandExecutor(remoteUser);
            }
            commandExecutor.executeCommand("filecount");
            List<String> list = commandExecutor.getResult();
            return list.get(0);
        }
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "/getPwd", method = RequestMethod.GET)
    public String getPWD(HttpServletRequest request) throws Exception {

        String remoteUser = request.getRemoteUser();
        String mail = "@ADS.IU.EDU";
        if (remoteUser != null) {
            remoteUser = remoteUser.substring(0, remoteUser.length() - mail.length());

            if (commandExecutor == null) {
                commandExecutor = new CommandExecutor(remoteUser);
            }
            String workingDirectory = commandExecutor.getWorkingDirectory();
            System.out.println("Working Directory : " + workingDirectory);
            return workingDirectory;

        }
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "/getPortalUrl", method = RequestMethod.GET)
    public String getPortalUrl(HttpServletRequest request) throws Exception {
        return readProperty(PORTAL_URL);
    }

    public String readProperty (String propertyName) throws Exception{
        try {
            URL resource = FileManagerController.class.getClassLoader().getResource(KERB_PROPERTIES);
            if (resource != null){
                properties.load(resource.openStream());
                return properties.getProperty(propertyName);
            }
        } catch (IOException e) {
            log.error("Unable to read properties..", e);
            throw new Exception("Unable to read properties.." , e) ;
        }
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "/getHome", method = RequestMethod.GET)
    public String getHome(HttpServletRequest request) throws Exception {
        String remoteUser = request.getRemoteUser();
        String mail = "@ADS.IU.EDU";
        if (remoteUser != null) {
            remoteUser = remoteUser.substring(0, remoteUser.length() - mail.length());

            if (commandExecutor == null) {
                commandExecutor = new CommandExecutor(remoteUser);
            }
            String homePath = commandExecutor.getHomePath();
            System.out.println("Home dir : " + homePath);
            return homePath;

        }
        return null;
    }

//    @ResponseBody
//    @RequestMapping(value = "/getRemoteUser", method = RequestMethod.GET)
//    public String getRemoteUser(HttpServletRequest request) throws Exception {
//        String remoteUser = request.getRemoteUser();
//        String mail = "@ADS.IU.EDU";
//        if (remoteUser != null) {
//            remoteUser = remoteUser.substring(0, remoteUser.length() - mail.length());
//            System.out.println("Remote User : " + remoteUser);
//        }
//        return remoteUser;
//    }

    /**
     * Download a file
     */
    @ResponseBody
    @RequestMapping(value = "/download/{filename}", method = RequestMethod.GET)
    public void downloadFile(@PathVariable(value = "filename") final String filename,
                             HttpServletResponse response,
                             HttpServletRequest request) throws Exception {
        String fileName = null;
        String decodedFN = "";
        try {
            String remoteUser = request.getRemoteUser();
            String defaultPath = "sda/filemanager/download/";
            String requestURI = request.getRequestURI();
//            requestURI = URLDecoder.decode(requestURI, "ASCII");
            fileName = requestURI.substring(defaultPath.length() + 4, requestURI.length());
            if (fileName.contains("+")){
                String[] strings = fileName.split("\\+");
                decodedFN = URLDecoder.decode(strings[0], "UTF-8");
                for (int i = 1; i < strings.length; i++){
                    decodedFN += "+" + URLDecoder.decode(strings[i], "UTF-8");
                }
            }else {
                decodedFN = URLDecoder.decode(fileName, "UTF-8");
            }

            System.out.println("filename : " + decodedFN);
            String mail = "@ADS.IU.EDU";
            if (remoteUser != null) {
                remoteUser = remoteUser.substring(0, remoteUser.length() - mail.length());
                System.out.println("Remote User : " + remoteUser);
                if (commandExecutor == null) {
                    commandExecutor = new CommandExecutor(remoteUser);
                }
            }
            response.setContentType("application/force-download");
            response.setHeader( "Content-Disposition", "attachment;filename=" + decodedFN.trim() );
            // get your file as InputStream
            commandExecutor.downloadFile(decodedFN.trim(), response.getOutputStream());

            response.flushBuffer();
        } catch (IOException ex) {
            log.error("Error writing file to output stream. Filename :'" + decodedFN);
            throw new Exception("IOError writing file to output stream", ex);
        }
    }

//    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
//    public String uploadFileHandler(@RequestParam("file") MultipartFile file,
//                             HttpServletRequest request) throws Exception {
//
//        System.out.println("**********upload file*********");
//        String remoteUser = request.getRemoteUser();
//        String mail = "@ADS.IU.EDU";
//        if (remoteUser != null) {
//            remoteUser = remoteUser.substring(0, remoteUser.length() - mail.length());
//            System.out.println("Remote User : " + remoteUser);
//            if (commandExecutor == null) {
//                commandExecutor = new CommandExecutor(remoteUser);
//            }
//        }
//        String fileName;
//        if (!file.isEmpty()) {
//            fileName = file.getOriginalFilename();
//            InputStream stream = file.getInputStream();
//
//            try {
//                commandExecutor.uploadFile(fileName, stream);
//            } catch (Throwable e) {
//                System.out.println("Error uploading file ....!!");
//                e.printStackTrace();
//            }
//            //return "You successfully uploaded file=" + fileName;
//            return "redirect:/";
//        } else {
//            return ViewNames.SDA_PAGE;
//        }
//    }


    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    public String uploadFile(HttpServletRequest request) throws Exception {
        try {
            System.out.println("**********upload file*********");
            String remoteUser = request.getRemoteUser();
            String mail = "@ADS.IU.EDU";
            if (remoteUser != null) {
                remoteUser = remoteUser.substring(0, remoteUser.length() - mail.length());
                System.out.println("Remote User : " + remoteUser);
                if (commandExecutor == null) {
                    commandExecutor = new CommandExecutor(remoteUser);
                }
            }
            boolean multipartContent = ServletFileUpload.isMultipartContent(request);
            if (multipartContent) {
                ServletFileUpload upload = new ServletFileUpload();
                FileItemIterator iter = upload.getItemIterator(request);
                while (iter.hasNext()) {
                    FileItemStream item = iter.next();
                    String fileName = item.getName();
                    System.out.println("********** file name : " + fileName);
                    String name = item.getFieldName();
                    InputStream stream = item.openStream();
                    if (item.isFormField()) {
                        return ViewNames.SDA_PAGE;
                    } else {
                        log.info("File field " + name + " with file name "
                                + item.getName() + " detected.");

                        commandExecutor.uploadFile(fileName, stream);
                        return "redirect:/";
                    }
                }
            }else {
                return ViewNames.SDA_PAGE;
            }
        }catch (Exception e){
            log.error("Error occured while uploading file ", e);
            throw new Exception(e);
        }

        return ViewNames.SDA_PAGE;
    }
}
