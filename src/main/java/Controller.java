import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;


/**
 * Email sender has empty values
 */

public class Controller {
  private static final String mainUrl = "https://www.avito.ru/rostovskaya_oblast/noutbuki?s=101&user=1";
  private static final String eMail = "mail@yandex.ru";
  private static final String searchValue = "ns";
  private static final ParserManager parserManager = ParserManager.getInstance();
  private static final Controller controller = new Controller();
  private static final String mail_from = "mailc@gmail.com";
  private static final String password = "password";


  public static void main(String[] args) throws IOException {
    // write your code here      https://www.avito.ru/rostovskaya_oblast/noutbuki?s=101&user=1


    File file = controller.settingsFileInitialize();
    ArrayList<String> settingsFromFile = controller.readSettingsFromFile(file);
    System.out.println("settingsFromFile size : " + settingsFromFile.size());


    String mainUrl = settingsFromFile.get(0);
    String email = settingsFromFile.get(1);
    String searchValue = settingsFromFile.get(2);
    String mail_from = settingsFromFile.get(3);
    String password = settingsFromFile.get(4);

    System.out.printf("Starting with settings: %s %s %s %s %s\n", mainUrl, email, searchValue, mail_from, password);

    try {
      controller.startProgram(mainUrl, email, searchValue, mail_from, password);
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  private File settingsFileInitialize() throws IOException {
    File path = ParserManager.getData();
    System.out.println(path + " settings path in INITIALIZE");
    File file = new File(path.toString() + File.separator + "settings.cfg");
    System.out.println(file);

    Path result;
    if (!file.exists()) {
      result = Files.createFile(file.toPath());
      System.out.println(file + " created!");
      return result.toFile();

    }
    result = file.toPath();
    return result.toFile();
  }

  private ArrayList<String> readSettingsFromFile(File file) {
    ArrayList<String> defaultValues = new ArrayList<>();
    defaultValues.add(0, mainUrl);
    defaultValues.add(1, eMail);
    defaultValues.add(2, searchValue);
    defaultValues.add(3, mail_from);
    defaultValues.add(4, password);

    try {
      BufferedReader reader = new BufferedReader(new FileReader(file));
      String str = reader.readLine();
      ArrayList<String> strings = new ArrayList<>();
      int count = 0;
      while ((str != null)) {
        System.out.println("settings : string was read from file settings : " + str);
        strings.add(count, str);
        count++;
        str = reader.readLine();
      }
      reader.close();
      if (strings.size() == 5) return strings;

    } catch (IOException e) {
      e.printStackTrace();
    }
    return defaultValues;
  }

  private void startProgram(String mainUrl, String email, String searchValue, String email_from, String password) {
    parserManager.setEmailFrom(email_from);
    parserManager.setPassword(password);
    parserManager.startProxySearchThread();
    parserManager.setEmailTo(email);
    parserManager.setMainUrl(mainUrl);
    parserManager.setSearchValue(searchValue);
    parserManager.startPars();
  }
}
