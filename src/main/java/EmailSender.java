import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailSender {
  private static final String smtpHost = "smtp.gmail.com";


  public static synchronized void send(MessageHTML message, String addres, final String username, final String password) {
    Properties props = new Properties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.host", smtpHost);
    props.put("mail.smtp.port", "587");
    props.put("mail.smtp.user", username);
    props.put("mail.smtp.pwd", password);

    try {
      Session session = Session.getInstance(props, new javax.mail.Authenticator() {
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password);
        }
    });
      Message msg = new MimeMessage(session);
      msg.setFrom(new InternetAddress(username));
      msg.setSubject(message.getTittle());
      msg.setContent(message.getBody(), "text/html; charset=windows-1251");
//      System.out.println("Sending message......." + message.getBody());
      msg.setRecipient(Message.RecipientType.TO, new InternetAddress(addres));
      Transport transport = session.getTransport("smtp");
      transport.connect(smtpHost, 587, username, password);
      transport.sendMessage(msg, msg.getAllRecipients());
      transport.close();


      System.out.println("Email send... Done");

    } catch (MessagingException e) {
      throw new RuntimeException(e);
    }
  }

}