public class EmailDemon implements Runnable {
  private int trais_counter;
  private MessageHTML message;
  private String mail_to;
  private String mail_from;
  private String password;

  public EmailDemon(MessageHTML message, String mail_to, String mail_from, String password) {
    this.message = message;
    this.mail_to = mail_to;
    this.mail_from = mail_from;
    this.password = password;
    this.trais_counter = 5;
  }


  @Override
  public void run() {
    System.out.println("started new Thread Sendler Mail");
    System.out.println("prepearing to sent");

    --this.trais_counter;
    try {
      EmailSender.send(message, mail_to, mail_from, password);
      System.out.println("successful!!!");
    } catch (Exception e) {
      e.printStackTrace();
      if (trais_counter != 0) {
        System.out.println("Error Sending email, start new try...");
        run();
      } else {
        System.out.println("Email sending Failed!!!");
      }
    }
    System.out.println("send done from demon");
  }
}
