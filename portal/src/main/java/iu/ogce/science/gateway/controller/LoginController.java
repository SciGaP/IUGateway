package iu.ogce.science.gateway.controller;

import iu.ogce.science.gateway.service.UserService;
import iu.ogce.science.gateway.util.Constants;
import iu.ogce.science.gateway.util.ViewNames;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.uiuc.ncsa.security.util.pkcs.CertUtil;

/**
 * @author Viknes
 *
 */

@Controller
public class LoginController {
	
	@Value("${portal.url}")
	private String PORTAL_URL;
	
	@Value("${caslogin.url}")
	private String CAS_LOGIN_URL;
	
	@Value("${casvalidate.url}")
	private String CAS_VALIDATE_URL;
	
	private final Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private UserService userService;
	
	@RequestMapping(value = "/caslogin", method = RequestMethod.GET)
	public String login(HttpServletRequest request) throws IOException {
		String loginUrl = CAS_LOGIN_URL +	"?cassvc=IU&casurl=" +
				PORTAL_URL + "iugateway/getTicket";
		logger.debug("Redirecting to CAS Login URL" + loginUrl);
        return "redirect:"+loginUrl;
	}
	
	@RequestMapping(value = "/getTicket", method = RequestMethod.GET)
	public String getTicket(HttpServletRequest request, @RequestParam(required = true, value="casticket") String casTicket) throws IOException {
		logger.info("CAS ticket is "+casTicket);
		logger.debug("Redirecting to URL " + CAS_VALIDATE_URL + 
				"?cassvc=IU&" +
        		"casticket=" + casTicket +
        		"&casurl="+PORTAL_URL+" to validate the CAS ticket");
        URL casValidatorURL = new URL(CAS_VALIDATE_URL + 
        		"?cassvc=IU&" +
        		"casticket=" + casTicket +
        		"&casurl="+PORTAL_URL);
        
        HttpsURLConnection casConnection = (HttpsURLConnection) casValidatorURL.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(casConnection.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            if(inputLine.equalsIgnoreCase(Constants.YES)) {
            	String username = in.readLine().trim();
            	userService.setAuthenticatedUser(username, null);
            	logger.info("Logged in as "+username);
            } else {
            	logger.debug("CAS Ticket validation failure ! Proceeding to Failure Page");
            	return ViewNames.LOGIN_FAILURE_PAGE;
            }
        }
        logger.debug("Login success ! Proceeding to Home Page");
        in.close();
        return ViewNames.INDEX_PAGE;
	}
	
	@ResponseBody
	@RequestMapping(value = "/getUserinfo", method = RequestMethod.GET)
	public String getUser(HttpServletRequest request, HttpServletResponse response) {
		logger.debug("get user info is called");
		response.setHeader("Cache-Control", "no-cache, no-store");
		response.setHeader("Pragma", "no-cache");
		if(userService.isUserAuthenticated())
			return userService.getAuthenticatedUser().getUsername();
		else
			return null;
	}
	
	@RequestMapping(value = "/logout", method = RequestMethod.POST)
	public void logout(HttpServletRequest request, HttpServletResponse response) {
		logger.info("Logging out of Cybergateway");
		userService.clearAuthenticatedUser();
		if(request.getSession(false)!=null) {
			response.setHeader("Cache-Control", "no-cache, no-store");
			response.setHeader("Pragma", "no-cache");
			request.getSession(false).invalidate();
		}
		response.setStatus(HttpStatus.OK.value());
		return;
	}
		
	/** Certificate is received from the CILogon2 client. The CILogon2 client talks to the CILogon service 
	 * to get the certificate and posts it to the portal
	 * @param request
	 * @return redirects to Homepage
	 */
	@RequestMapping(value = "/receiveCert", method = RequestMethod.POST)
	public String getCILogonCert(HttpServletRequest request, 
			@RequestParam(value="cert") String certificate,
			@RequestParam(value="certSubject") String certSubject,
			@RequestParam(value="username") String username) {
        logger.info("Got the certificate from CILogon !");
        logger.info("Certificate Subject is "+certSubject);
		logger.info("Certificate is \n"+certificate);
		logger.info("Username is "+username);
		try {
			String networkId = "";
			X509Certificate cert = CertUtil.fromX509PEM(certificate)[0];
			Collection<List<?>> alternativeNames = cert.getSubjectAlternativeNames();
			for(List<?> item : alternativeNames) {
				logger.debug("Alternative Subject Names from Certificate are");
				logger.debug(item.get(0).toString()+"\n"+item.get(1).toString());
				logger.debug("Using 2nd parameter to get network id");
				String email = item.get(1).toString();
				networkId = email.substring(0, email.indexOf("@"));
			}
			userService.setAuthenticatedUser(networkId, certificate);
        	logger.info("Logged in as "+networkId);
		} catch (Exception e) {
			logger.error(e);
			logger.error("Error Decrypting certificate to get username. Getting the username from CAS !");
			return "redirect:caslogin";
		}
		
        return "redirect:"+ViewNames.INDEX_PAGE;
	}
	
}
