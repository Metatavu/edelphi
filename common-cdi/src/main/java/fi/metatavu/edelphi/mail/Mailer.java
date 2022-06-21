package fi.metatavu.edelphi.mail;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.mail.Session;

import org.simplejavamail.email.Email;
import org.simplejavamail.mailer.MailerBuilder;

/**
 * Mailer service
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class Mailer {

  @Resource (lookup = "java:jboss/mail/Default")
  private Session session;
  
  /**
   * Sends an email
   * 
   * @param email email
   */
  public void sendMail(Email email) {
    MailerBuilder.usingSession(session).buildMailer().sendMail(email);
  }

  /**
   * Sends an email in a new thread and this method returns immediately
   *
   * @param email email
   */
  public void sendMailAsync(Email email) {
    MailerBuilder.usingSession(session).buildMailer().sendMail(email, true);
  }

}
