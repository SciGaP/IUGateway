package org.scigap.iucig.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class KerberosController {
    public static final String REMOTE_USER = "REMOTE_USER";
    public static final String KRB5CCNAME = "KRB5CCNAME";
    public Map<String, String> systemPropertyMap;

    @ResponseBody
    @RequestMapping(value = "/getProperties", method = RequestMethod.GET)
    public Map<String, String> readSystemProperty(){
        systemPropertyMap = new HashMap<String, String>();
        String remoteUser = System.getProperty(REMOTE_USER);
        String kerbName = System.getProperty(KRB5CCNAME);
        if (remoteUser != null){
            systemPropertyMap.put(REMOTE_USER, remoteUser);
        }
        if (kerbName != null){
            systemPropertyMap.put(KRB5CCNAME, kerbName);
        }
        return systemPropertyMap;
    }
}
