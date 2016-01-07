package org.scigap.iucig.controller;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.scigap.iucig.filemanager.CommandExecutor;
import org.scigap.iucig.filemanager.util.Constants;
import org.scigap.iucig.filemanager.util.Item;
import org.scigap.iucig.filemanager.util.LoginConfigUtil;
import org.scigap.iucig.filemanager.util.SDAUtils;
import org.scigap.iucig.util.ViewNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Controller
@RequestMapping(value = "/filemanager")
public class FileManagerController {
    private static final Logger log = LoggerFactory.getLogger(FileManagerController.class);
    private CommandExecutor commandExecutor;
    private static Map<String, Boolean> userLogoutStatus = new HashMap<String, Boolean>();

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        log.info("Logging out of SDA Web Interface");
        SecurityContextHolder.getContext().setAuthentication(null);
        LoginConfigUtil configUtil = new LoginConfigUtil();
        String remoteUser = request.getRemoteUser();
        String mail = "@ADS.IU.EDU";
        if (remoteUser != null) {
            remoteUser = remoteUser.substring(0, remoteUser.length() - mail.length());
            if (configUtil.isTicketAvailable(remoteUser)){
                userLogoutStatus.put(remoteUser, true);
            }
        }
    }

    private void popLogout(HttpServletRequest request, HttpServletResponse response){
        HttpSession session = request.getSession();
        if(request.getSession(false)!=null) {
            response.setStatus(401);
            response.setHeader("WWW-Authenticate", "basic realm=\"A valid Scholarly Data Archive account is required to access this system\"");
//                    response.setHeader("WWW-Authenticate", "None");
            session.setAttribute("auth", Boolean.TRUE);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/getRemoteUser", method = RequestMethod.GET)
    public String getRemoteUser(HttpServletRequest request, HttpServletResponse response) {
        String remoteUser = request.getRemoteUser();
        String mail = "@ADS.IU.EDU";
        if (remoteUser != null) {
            remoteUser = remoteUser.substring(0, remoteUser.length() - mail.length());
            if (userLogoutStatus.containsKey(remoteUser)){
                Boolean status = userLogoutStatus.get(remoteUser);
                if (status){
                    popLogout(request, response);
                    userLogoutStatus.put(remoteUser, false);
                }
            }
        }
        return remoteUser;
    }
    /**
     * Returns the result of a command using a Item list
     */
    @ResponseBody
    @RequestMapping(value = "/command/{command}", method = RequestMethod.GET)
    public List<Item> executeCommand(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String decodedCommand = "";
        try {
            String remoteUser = request.getRemoteUser();
            String mail = "@ADS.IU.EDU";
            if (remoteUser != null) {
                remoteUser = remoteUser.substring(0, remoteUser.length() - mail.length());
                if (userLogoutStatus.containsKey(remoteUser)) {
                    Boolean status = userLogoutStatus.get(remoteUser);
                    if (status) {
                        popLogout(request, response);
                    }
                    userLogoutStatus.put(remoteUser, false);
                }
                String defaultPath = "sda/filemanager/command/";
                String requestURI = request.getRequestURI();
                String commandFinal = requestURI.substring(defaultPath.length() + 1, requestURI.length());

                if (commandFinal.contains("+")) {
                    String[] strings = commandFinal.split("\\+");
                    decodedCommand = URLDecoder.decode(strings[0], "UTF-8");
                    for (int i = 1; i < strings.length; i++) {
                        decodedCommand += "+" + URLDecoder.decode(strings[i], "UTF-8");
                    }
                } else {
                    decodedCommand = URLDecoder.decode(commandFinal, "UTF-8");
                }
                System.out.println("Command : " + decodedCommand);
                if (commandExecutor == null) {
                    commandExecutor = new CommandExecutor(remoteUser);
                } else {
                    if (!commandExecutor.getRemoteUser().equals(remoteUser)) {
                        commandExecutor = new CommandExecutor(remoteUser);
                    }
                }
                commandExecutor.executeCommand(decodedCommand);
                return commandExecutor.getResultItemList();
            }
        } catch (UnsupportedEncodingException e) {
            handleRuntimeException(e, response, "Unsupported encoding type");
        } catch (IOException e) {
            handleRuntimeException(e, response, "Error while executing command : " + decodedCommand);
        } catch (JSchException e) {
            handleRuntimeException(e, response, e.getLocalizedMessage());
        } catch (SftpException e) {
            handleRuntimeException(e, response, "Error while executing command : " + decodedCommand);
        }

        return null;
    }

    public static void handleRuntimeException(Exception ex, HttpServletResponse response,String message) throws IOException {
        log.error(message, ex);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.getWriter().write(message);
        response.flushBuffer();
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
            }else {
                if (!commandExecutor.getRemoteUser().equals(remoteUser)){
                    commandExecutor = new CommandExecutor(remoteUser);
                }
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
    public String getFileCount(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String remoteUser = request.getRemoteUser();
        String mail = "@ADS.IU.EDU";
        if (remoteUser != null) {
            remoteUser = remoteUser.substring(0, remoteUser.length() - mail.length());
            if (userLogoutStatus.containsKey(remoteUser)){
                Boolean status = userLogoutStatus.get(remoteUser);
                if (status){
                    popLogout(request, response);
                }
                userLogoutStatus.put(remoteUser, false);
            }
            System.out.println("Remote User : " + remoteUser);
            if (commandExecutor == null) {
                commandExecutor = new CommandExecutor(remoteUser);
            }else {
                if (!commandExecutor.getRemoteUser().equals(remoteUser)){
                    commandExecutor = new CommandExecutor(remoteUser);
                }
            }
            commandExecutor.executeCommand("filecount");
            List<String> list = commandExecutor.getResult();
            return list.get(0);
        }
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "/getPwd", method = RequestMethod.GET)
    public String getPWD(HttpServletRequest request, HttpServletResponse response) throws IOException {

        try {
            String remoteUser = request.getRemoteUser();
            String mail = "@ADS.IU.EDU";
            if (remoteUser != null) {
                remoteUser = remoteUser.substring(0, remoteUser.length() - mail.length());
                if (userLogoutStatus.containsKey(remoteUser)){
                    Boolean status = userLogoutStatus.get(remoteUser);
                    if (status){
                        popLogout(request, response);
                    }
                    userLogoutStatus.put(remoteUser, false);
                }
                if (commandExecutor == null) {
                    commandExecutor = new CommandExecutor(remoteUser);
                }else {
                    if (!commandExecutor.getRemoteUser().equals(remoteUser)){
                        commandExecutor = new CommandExecutor(remoteUser);
                    }
                }
                String workingDirectory = commandExecutor.getWorkingDirectory();
                System.out.println("Working Directory : " + workingDirectory);
                return workingDirectory;

            }
        } catch (IOException e) {
            handleRuntimeException(e, response, "Error while getting current working directory... ");
        } catch (JSchException e) {
            handleRuntimeException(e, response, e.getLocalizedMessage());
        } catch (SftpException e) {
            handleRuntimeException(e, response, "Error while getting current working directory... ");
        }

        return null;
    }

    @ResponseBody
    @RequestMapping(value = "/getPortalUrl", method = RequestMethod.GET)
    public String getPortalUrl(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            return SDAUtils.getPortalURL();
        } catch (IOException e) {
            handleRuntimeException(e, response, "Unable to read properties..");
        }
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "/getHome", method = RequestMethod.GET)
    public String getHome(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String remoteUser = request.getRemoteUser();
            String mail = "@ADS.IU.EDU";
            if (remoteUser != null) {
                remoteUser = remoteUser.substring(0, remoteUser.length() - mail.length());

                if (userLogoutStatus.containsKey(remoteUser)) {
                    Boolean status = userLogoutStatus.get(remoteUser);
                    if (status) {
                        popLogout(request, response);
                    }
                    userLogoutStatus.put(remoteUser, false);
                }
                if (commandExecutor == null) {
                    commandExecutor = new CommandExecutor(remoteUser);
                } else {
                    if (!commandExecutor.getRemoteUser().equals(remoteUser)) {
                        commandExecutor = new CommandExecutor(remoteUser);
                    }
                }
                String homePath = commandExecutor.getHomePath();
                System.out.println("Home dir : " + homePath);
                return homePath;

            }
        } catch (IOException e) {
            handleRuntimeException(e, response, "Error while getting current home directory... ");
        } catch (JSchException e) {
            handleRuntimeException(e, response, e.getLocalizedMessage());
        } catch (SftpException e) {
            handleRuntimeException(e, response, "Error while getting current home directory... ");
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
                             HttpServletRequest request) throws IOException {
        String fileName = null;
        String decodedFN = "";
        try {
            String remoteUser = request.getRemoteUser();
            if (userLogoutStatus.containsKey(remoteUser)){
                Boolean status = userLogoutStatus.get(remoteUser);
                if (status){
                    popLogout(request, response);
                }
                userLogoutStatus.put(remoteUser, false);
            }
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
                }else {
                    if (!commandExecutor.getRemoteUser().equals(remoteUser)){
                        commandExecutor = new CommandExecutor(remoteUser);
                    }
                }
            }
            response.setContentType("application/force-download");
            response.setHeader( "Content-Disposition", "attachment;filename=" + decodedFN.trim() );
            // get your file as InputStream
            commandExecutor.downloadFile(decodedFN.trim(), response.getOutputStream());

            response.flushBuffer();
        } catch (IOException ex) {
            handleRuntimeException(ex, response, "Error writing file to output stream. Filename :'" + decodedFN);
        } catch (JSchException e) {
            handleRuntimeException(e, response, Constants.ErrorMessages.AUTH_ERROR);
        } catch (SftpException e) {
            handleRuntimeException(e, response, "Error writing file to output stream. Filename :'" + decodedFN);
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
    public String uploadFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            System.out.println("**********upload file*********");
            String remoteUser = request.getRemoteUser();
            String mail = "@ADS.IU.EDU";
            if (remoteUser != null) {
                remoteUser = remoteUser.substring(0, remoteUser.length() - mail.length());
                if (userLogoutStatus.containsKey(remoteUser)){
                    Boolean status = userLogoutStatus.get(remoteUser);
                    if (status){
                        popLogout(request, response);
                    }
                    userLogoutStatus.put(remoteUser, false);
                }
                System.out.println("Remote User : " + remoteUser);
                if (commandExecutor == null) {
                    commandExecutor = new CommandExecutor(remoteUser);
                }else {
                    if (!commandExecutor.getRemoteUser().equals(remoteUser)){
                        commandExecutor = new CommandExecutor(remoteUser);
                    }
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
        } catch (IOException ex) {
            handleRuntimeException(ex, response, "Error writing uploading file...");
        } catch (JSchException e) {
            handleRuntimeException(e, response, Constants.ErrorMessages.AUTH_ERROR);
        } catch (SftpException e) {
            handleRuntimeException(e, response, "Error writing uploading file...");
        } catch (FileUploadException e) {
            e.printStackTrace();
        }

        return ViewNames.SDA_PAGE;
    }
}
