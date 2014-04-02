package org.scigap.iucig.filemanager.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;


/*
* Creates a Jaas Login configuration file
* @param: filename and the ticketCache
* */
public class LoginConfigUtil {
    private static final Logger log = LoggerFactory.getLogger(LoginConfigUtil.class);
    public static final String KERB_PROPERTIES = "kerb.properties";

    private final String LOGIN_FILE_FIRST_PART = " com.sun.security.jgss.krb5.initiate {\n" +
            "               com.sun.security.auth.module.Krb5LoginModule required\n" +
            "                             debug=\"true\"\n" +
            "                   doNotPrompt=\"true\"\n" +
            "               useTicketCache=\"true\"\n" +
            "              ticketCache=\"";

    private final String LOGIN_FILE_SECOND_PART = "\";\n" +
            "\n" +
            "  };";
    private String filePath;

    public LoginConfigUtil() {
        filePath = readProperty("kerb.conffile.location");
    }

    public String createLoginFile(String filename, String ticketCache) {
        try {
            String filePath = this.filePath + filename;
            File file = new File(filePath);
            BufferedWriter output = new BufferedWriter(new FileWriter(file));
            output.write(LOGIN_FILE_FIRST_PART);
            output.write(ticketCache);
            output.write(LOGIN_FILE_SECOND_PART);
            output.close();

            return filePath;
        } catch (IOException e) {
            log.error("Error Occurred Creating login.conf file ..." + e.getMessage());
        }
        return null;
    }

    public String readProperty(String propertyName) {
        Properties properties = new Properties();
        try {
            URL resource = LoginConfigUtil.class.getClassLoader().getResource(KERB_PROPERTIES);
            if (resource != null) {
                properties.load(resource.openStream());
                return properties.getProperty(propertyName);
            }
        } catch (IOException e) {
            log.error("Error getting the conffile location: " + e.getMessage());
        }
        return null;
    }

}
