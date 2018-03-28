import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailSender {
  private static final String username = "chernov.alexe2017@yandex.ru";
  private static final String password = "";
  private static final String smtpHost = "smtp.yandex.ru";


  public static synchronized void send(String title, String text, String addres) {
    Properties props = new Properties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.host", smtpHost);
    props.put("mail.smtp.port", "587");
    props.put("mail.smtp.user", username);
    props.put("mail.smtp.pwd", password);

    try {
      Session session = Session.getInstance(props, null);
      Message msg = new MimeMessage(session);
      msg.setFrom(new InternetAddress(username));
      msg.setSubject(title);
      msg.setText(text);
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