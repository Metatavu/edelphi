package fi.metatavu.edelphi.utils;

import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.lang3.StringUtils;

public class MailUtils {

  private static final Logger logger = Logger.getLogger(MailUtils.class.getName());
  public static final String PLAIN = "text/plain; charset=UTF-8";
  public static final String HTML = "text/html; charset=UTF-8";

  private MailUtils() {
  }
  
  public static boolean sendMail(String fromAddress, String toAddress, String subject, String content) {
    InternetAddress from = null;
    try {
      if (StringUtils.isNotBlank(fromAddress)) {
        from = new InternetAddress(fromAddress);
      }
    } catch (AddressException e) {
      logger.log(Level.SEVERE, "Malformed from address", e);
    }
    
    return sendMail(new String[] { toAddress }, from, subject, content, PLAIN);
  }
  
  public static boolean sendMail(String toAddress, String subject, String content) {
    return sendMail(new String[] { toAddress }, null, subject, content, PLAIN);
  }

  public static boolean sendMail(String[] toAddresses, String subject, String content) {
    return sendMail(toAddresses, null, subject, content, PLAIN);
  }

  public static boolean sendMail(String[] toAddresses, InternetAddress from, String subject, String content, String mimetype) {
    try {
      Properties props = new Properties();

      InitialContext ictx = new InitialContext(props);
      Session mailSession = (Session) ictx.lookup("java:jboss/mail/Default");

      MimeMessage m = new MimeMessage(mailSession);
      Address[] to = new InternetAddress[toAddresses.length];
      for (int i = 0; i < toAddresses.length; i++) {
        to[i] = new InternetAddress(toAddresses[i]);
      }

      m.setRecipients(Message.RecipientType.TO, to);
      m.setSubject(subject);
      m.setSentDate(new Date());
      m.setContent(content, mimetype);

      if (from != null) {
        m.setFrom(from);
        m.setReplyTo(new InternetAddress[] { from });
      }

      Transport.send(m);
      
      return true;
    } catch (NamingException e) {
      logger.log(Level.SEVERE, "Could not send mail services are configured incorrectly", e);
    } catch (MessagingException e) {
      logger.log(Level.WARNING, String.format("Could not deliver mail to %s", StringUtils.join(toAddresses, ',')), e);
    }
    
    return false;
  }

}
