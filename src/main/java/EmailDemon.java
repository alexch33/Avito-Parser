public class EmailDemon implements Runnable {
  private String title;
  private String text;
  private String addres;

  public EmailDemon(String title, String text, String addres) {
    this.title = title;
    this.text = text;
    this.addres = addres;
  }


  @Override
  public void run() {
    System.out.println("started new Thread Sendler Mail");
    System.out.println("prepearing to sent");

    try {
      EmailSender.send(title, text, addres);
      System.out.println("successful!!!");
    } catch (Exception e) {
      e.printStackTrace();
      run();
      System.out.println("Thread run() restarted....");
    }
    System.out.println("send done from demon");
  }
}
