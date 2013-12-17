package org.scigap.iucig.controller;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Viknes
 *
 */
@Controller
public class UserController {

	private final Logger logger = Logger.getLogger(getClass());

	@ResponseBody
	@RequestMapping(value = "/submitFeedback", method = RequestMethod.POST)
	public String feedback(@RequestParam(value="name") String name,
			@RequestParam(value="email") String email,
			@RequestParam(value="comment") String comment) {

		String host="smtp.gmail.com";
		String username = "iusciencegateway";
		String password = "sgce-password";

		Properties props = new Properties();
        props.put("mail.smtp.starttls.enable", "true"); // added this line
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.user", username);
        props.put("mail.smtp.password", password);
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");

        Session session = Session.getDefaultInstance(props, null);
        StringBuffer msgbody = new StringBuffer();
        if(name.isEmpty() && email.isEmpty()) {
        	msgbody.append("Anonymous Wrote");
        } else {
	        msgbody.append("From\t");
	        if(!name.isEmpty())
	        	msgbody.append(name+"\t");
	        if(!email.isEmpty())
	        	msgbody.append(email+"\t");
        }
        msgbody.append("\n\n"+comment);

        try {
        	// Emailing feedback from user
            Message feedbackMsg = new MimeMessage(session);
            feedbackMsg.setFrom(new InternetAddress("iusciencegateway@gmail.com", "IU CIG Feedback"));
            feedbackMsg.addRecipient(Message.RecipientType.TO, new InternetAddress("sgg@rtinfo.indiana.edu"));
            feedbackMsg.setSubject("New Comment from IU Gateway");
            feedbackMsg.setText(msgbody.toString());
            
            Transport transport = session.getTransport("smtp");
            transport.connect(host, username, password);
            transport.sendMessage(feedbackMsg, feedbackMsg.getAllRecipients());
            logger.info("Comment emailed");
            
            // Sending feedback receipt confirmation to user
            if(!email.isEmpty()) {
            	
            	String message = "Hello, \n\nWe appericate your time and effort in providing feedback to us." +
            			" You will hear back from us soon incase we need further information. \n\n" +
                        "Cheers and thanks for using our service, \n" +
                        "Science Gateway Group \n" +
                        "University Information Technology Services \n" +
                        "Indiana University" ;
	            Message receiptMsg = new MimeMessage(session);
	            receiptMsg.setFrom(new InternetAddress("iusciencegateway@gmail.com", "IU CIG Feedback"));
	            receiptMsg.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
	            receiptMsg.setSubject("Thanks for your feedback !");
	            receiptMsg.setText(message);
	
	            transport.sendMessage(receiptMsg, receiptMsg.getAllRecipients());
	            transport.close();
	            logger.info("Confirmation emailed");
            }

        } catch (AddressException e) {
        	logger.error("Error sending mail with feedback \n"+msgbody.toString());
        	logger.error(e);
        } catch (MessagingException e) {
        	logger.error("Error sending mail with feedback \n"+msgbody.toString());
        	logger.error(e);
        } catch (UnsupportedEncodingException e) {
        	logger.error("Error sending mail with feedback \n"+msgbody.toString());
        	logger.error(e);
		}
        return "Submitted";
	}
}
