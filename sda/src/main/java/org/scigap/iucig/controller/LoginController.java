package org.scigap.iucig.controller;

import org.apache.log4j.Logger;
import org.scigap.iucig.service.UserService;
import org.scigap.iucig.util.ViewNames;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class LoginController {
	
	private final Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private UserService userService;
    @Autowired
    private AuthenticationSuccessHandler successHandler;

	@RequestMapping(value = "/logout1", method = RequestMethod.POST)
	public String logout(HttpServletRequest request, HttpServletResponse response) {
		logger.info("Logging out of SDA Web Interface");
		userService.clearAuthenticatedUser();
        if(request.getSession(false)!=null) {
            response.setHeader("Cache-Control", "no-cache, no-store");
            response.setHeader("Pragma", "no-cache");
            request.getSession(false).invalidate();
        }
        response.setStatus(HttpStatus.OK.value());
        return "redirect:"+ViewNames.SDA_PAGE;

	}

    @ResponseBody
    @RequestMapping(value = "/getRemoteUser", method = RequestMethod.GET)
    public String getRemoteUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String remoteUser = request.getRemoteUser();
        String mail = "@ADS.IU.EDU";
        if (remoteUser != null) {
            remoteUser = remoteUser.substring(0, remoteUser.length() - mail.length());
            System.out.println("Remote User : " + remoteUser);
            Authentication authentication = userService.setAuthenticatedUser(remoteUser);
            successHandler.onAuthenticationSuccess(request, response, authentication);
        }
        return remoteUser;
    }
}
