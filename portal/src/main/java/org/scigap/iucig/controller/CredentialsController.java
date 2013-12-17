package org.scigap.iucig.controller;

import org.apache.commons.codec.binary.Base64;
import org.scigap.iucig.gateway.util.ACSUtils;
import org.scigap.iucig.gateway.util.BR2Credential;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/credentials/")
public class CredentialsController {

	@ResponseBody
	@RequestMapping(value="/{machine}/create", method = RequestMethod.POST)
	public String postCredentials(@PathVariable(value="machine")String machine, @RequestParam(value="username") String username, 
			@RequestParam(value="password") String password) {
		// TODO : Credentials should be made machine specific. The machine parameter above should be used for it.
		ACSUtils.writeToCredentialStore(username, password);
		BR2Credential credentials = ACSUtils.readFromCredentialStore(username);
//		Base64 base = new Base64();
		String publicKey = new String(credentials.getPubKey());
		return publicKey;
	}
	
	@ResponseBody
	@RequestMapping(value="/{machine}/exist", method = RequestMethod.GET)
	public boolean isCredentialsExist(@PathVariable(value="machine")String machine, @RequestParam(value="username") String username) {
		// TODO : Credentials should be made machine specific. The machine parameter above should be used for it.
		BR2Credential credentials = ACSUtils.readFromCredentialStore(username);
		if(credentials!=null)
			return true;
		else
			return false;
	}
	
	
}
