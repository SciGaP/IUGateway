package org.scigap.iucig.controller;

import org.scigap.iucig.filemanager.CommandExecutor;
import org.scigap.iucig.util.FileUploadBean;
import org.scigap.iucig.util.ViewNames;
import org.springframework.validation.BindException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

public class FileUploadController extends SimpleFormController {
    private CommandExecutor commandExecutor;

    public FileUploadController() {
        setCommandClass(FileUploadBean.class);
        setCommandName("/upload");
    }

    @Override
    protected ModelAndView onSubmit(HttpServletRequest request,
                                    HttpServletResponse response,
                                    Object command, BindException errors) throws Exception {
        String remoteUser = request.getRemoteUser();
        String mail = "@ADS.IU.EDU";
        if (remoteUser != null) {
            remoteUser = remoteUser.substring(0, remoteUser.length() - mail.length());
            System.out.println("Remote User : " + remoteUser);
            if (commandExecutor == null) {
                commandExecutor = new CommandExecutor(remoteUser);
            }
        }
        FileUploadBean file = (FileUploadBean) command;
        MultipartFile multipartFile = file.getFile();
        String fileName = null;
        if (multipartFile != null) {
            fileName = multipartFile.getOriginalFilename();
            File createdFile = new File(fileName);
            multipartFile.transferTo(createdFile);

            if (fileName == null) {
                fileName = multipartFile.getName();
            }
            try {
                commandExecutor.uploadFile(fileName, createdFile);
            } catch (Throwable e) {
                System.out.println("Error uploading file ....!!");
                e.printStackTrace();
            }
        }
//        return new ModelAndView(ViewNames.SDA_PAGE);
        return new ModelAndView("FileUploadSuccess","fileName",fileName);
    }
}
