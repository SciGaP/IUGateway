package org.scigap.iucig.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.HashMap;
import java.util.Map;

public class KerberosService {
    public static final String REMOTE_USER = "REMOTE_USER";
    public static final String KRB5CCNAME = "KRB5CCNAME";
    public Map<String, String> systemPropertyMap;

    @Path("/getProperties")
    @GET
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
