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

    private String filePath;
    private String ticketLocation;


    public LoginConfigUtil() {
        filePath = readProperty("kerb.conffile.location");
        ticketLocation = readProperty("kerb.ticket.location");
    }

    public String createLoginFile(String filename, String username) {
        try {
            String filePath = this.filePath + filename;
            String ticketCache = searchTicket(username);
            if (ticketCache == null) {
                return null;
            }

            File file = new File(filePath);
            BufferedWriter output = new BufferedWriter(new FileWriter(file));
            String LOGIN_FILE_FIRST_PART = " com.sun.security.jgss.krb5.initiate {\n" +
                    "               com.sun.security.auth.module.Krb5LoginModule required\n" +
                    "                             debug=\"true\"\n" +
                    "                   doNotPrompt=\"true\"\n" +
                    "               useTicketCache=\"true\"\n" +
                    "              ticketCache=\"";
            output.write(LOGIN_FILE_FIRST_PART);
            output.write(ticketCache);
            String LOGIN_FILE_SECOND_PART = "\";\n" +
                    "\n" +
                    "  };";
            output.write(LOGIN_FILE_SECOND_PART);
            output.close();
            return filePath;
        } catch (IOException e) {
            log.error("Error Occurred Creating login.conf file ..." + e.getMessage());
        }
        return null;
    }

    public String searchTicket(final String username) {
        File folder = new File(ticketLocation);
        for (final File fileEntry : folder.listFiles()) {
            String TICKET_PREPHRASE = "krb5cc_apache_";
            if(fileEntry.getName().contains(TICKET_PREPHRASE +username))
                return ticketLocation+"/"+fileEntry.getName();
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
