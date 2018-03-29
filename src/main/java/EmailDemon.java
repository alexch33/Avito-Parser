public class EmailDemon implements Runnable {
  private String title;
  private String text;
  private String addres;
  private String mail_from;
  private String password;

  public EmailDemon(String title, String text, String addres, String mail_from, String password) {
    this.title = title;
    this.text = text;
    this.addres = addres;
    this.mail_from = mail_from;
    this.password = password;
  }


  @Override
  public void run() {
    System.out.println("started new Thread Sendler Mail");
    System.out.println("prepearing to sent");

    try {
      EmailSender.send(title, text, addres, mail_from, password);
      System.out.println("successful!!!");
    } catch (Exception e) {
      e.printStackTrace();
      run();
      System.out.println("Thread run() restarted....");
    }
    System.out.println("send done from demon");
  }
}
