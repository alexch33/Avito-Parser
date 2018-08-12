import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.*;
import java.net.URL;
import java.util.concurrent.TimeUnit;


public class ParserManager {
  private boolean flag = true;
  private static int counter = 0;
  private String mainUrl;
  private Map<Ad, Date> ads = new HashMap<>();
  private File file;
  private ObjectInputStream ois;
  private static final File data;
  private String emailAdress;
  private String searchValue;
  private String emailFrom;
  private String password;

  static {
    data = new File(CurrentDir() + File.separator + "ParsersData");
    System.out.println(data + File.separator + "ParserData" + " data static Manger");
  }

  public static File getData() {
    return data;
  }

  private static String CurrentDir() {
    String path = System.getProperty("user.dir");
    String FileSeparator = System.getProperty("file.separator");
    return path.substring(0, path.lastIndexOf(FileSeparator) + 1);
  }


  public String getSearchValue() {
    return searchValue;
  }

  public void setSearchValue(String searchValue) {
    this.searchValue = searchValue;
  }

  public boolean isFlag() {
    return flag;
  }

  public void setFlag(boolean flag) {
    this.flag = flag;
  }

  public String getMainUrl() {
    return mainUrl;
  }

  public void setMainUrl(String mainUrl) {
    this.mainUrl = mainUrl;
  }

  public String getEmailAdress() {
    return emailAdress;
  }

  public void setEmailAdress(String emailAdress) {
    this.emailAdress = emailAdress;
  }

  static ParserManager getInstance() {
    return Instance.getInstance();

  }

  public void startProxySearchThread() {
    final Thread proxyThread = new Thread(new ProxyController());
    proxyThread.setDaemon(true);
    proxyThread.start();
    proxyThread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
      @Override
      public void uncaughtException(Thread thread, Throwable throwable) {
        System.out.println("error in proxy thread, relaunching....");
        if (!proxyThread.isAlive() || proxyThread.isInterrupted()) {
          startProxySearchThread();
        }
      }
    });
  }

  private ParserManager() {
    if (!data.exists()) try {
      Files.createDirectories(data.toPath());
    } catch (IOException e) {
      e.printStackTrace();
    }
    file = new File(data + File.separator + "admap.ser");
    if (Files.notExists(file.toPath())) try {
      System.out.println("file creating " + file);
      Files.createFile(file.toPath());
    } catch (IOException e) {
      e.printStackTrace();
    }

    try {
      FileInputStream fis = new FileInputStream(file);
      ois = new ObjectInputStream(fis);

      ads = (Map<Ad, Date>) ois.readObject();
      fis.close();
      ois.close();
      System.out.println("Object history READING...Done");

    } catch (EOFException e) {
      ads = new HashMap<>();
      System.out.println("Object history READING...not Done, new file object history created");
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }


    if (ads == null) {
      ads = new HashMap<Ad, Date>();
      System.out.println("new ads");
    }

    System.out.println("constructor ads(object history HashMap) size: " + ads.size());
  }

  public void startPars() {

    while (flag) {
      try {
        Date before = new Date();
        System.out.printf("Start Processing URL: %s\n", mainUrl);
        start(mainUrl);
        int rand = (int) rnd(6, 17);
        TimeUnit.SECONDS.sleep(rand);
        Date after = new Date();
        long time = after.getTime() - before.getTime();
        System.out.println("parsing page done, time reamaning ms: " + time + "   seconds: " + new Date(time).getSeconds());


      } catch (Exception e) {
        e.printStackTrace();
      }
      if (!flag) break;
    }
  }


  private boolean containsURL(URL key) {
    boolean result = false;
    for (Map.Entry<Ad, Date> pair : ads.entrySet()) {
      if (pair.getKey().getUrl().equals(key)) result = true;
    }
    // System.out.println(result + " url contains : true");
    return result;
  }

  //important method
  private void start(String mainUrl) throws MalformedURLException {
    Searcher searcher = new Searcher(mainUrl);
    Map<URL, Date> urlDateMap = null;
    try {
      urlDateMap = searcher.getUrlsAndDatesMapFromFile();
    } catch (IOException e) {
      e.printStackTrace();
    }

    if (urlDateMap == null || urlDateMap.isEmpty()) {
      System.out.println("urlDateMap is empty ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
      Searcher.deleteLastProxy();
    }

    for (Map.Entry<URL, Date> urlStringEntry : urlDateMap.entrySet()) {
      URL url = urlStringEntry.getKey();
      Date dateFromBoard = urlStringEntry.getValue();
      System.out.println("Url seen: " + url + " date: " + (dateFromBoard == null ? "Not Today" : dateFromBoard));
      if (!AvitoDateParser.isToday(dateFromBoard)) continue;


      if (!containsURL(url) ||
              (containsURL(url) && !datesEquals(url, dateFromBoard))) {


        Ad ad = AdFactoryFromAdUrl.createNewAd(urlStringEntry.getKey());
        String title = ad.getTitle();
        int price = ad.getPrice();
        String description = ad.getDescription();
        Date date = ad.getDate();
//        if (descriptionContainsSearchVal(description, title, searchValue))
//          System.out.println(title + "\n" +
//                  price + "\n" +
//                  description + "!Contained searchValue!!! " + searchValue + "\n" + ad.getUrl());


        if ((descriptionContainsSearchVal(description, title, searchValue)) && !ads.containsKey(ad) && !ads.containsValue(date)) {
          new Thread(new EmailDemon(title, price + " \n" + description + " время: \n" +
                  "" + date + "\n" + ad.getUrl(), emailAdress, emailFrom, password)).start();

        }
        System.out.println("##########################################################################");
        System.out.println(ad);
        System.out.println(ads.size() + " ads.size()");
        System.out.println("##########################################################################");


        if (!ads.containsKey(ad)) {
          ads.put(ad, date);
          setSerialize();
          if (ads.size() > 2000) {
            try {
              Path bakFile = saveSetAndCreateNewFile();
              System.out.println("BAK FILE AND NEW HashSet CREATED!!! " + bakFile);
            } catch (IOException e) {
              e.printStackTrace();
            }
          }

        }

      }

    }
  }

//  private boolean priceInRange(int price) {
//    return priceRange[0] == 0 && priceRange[1] == 0 || price >= priceRange[0] && price <= priceRange[1];
//
//  }

  private boolean descriptionContainsSearchVal(String description, String title, String searchValue) {
    return searchValue.equals("ns") || description.toLowerCase().contains(searchValue) || title.toLowerCase().contains(searchValue);
  }

  private boolean datesEquals(URL url, Date dateFromBoard) {
    boolean result = false;
    for (Ad ad : ads.keySet()) {
      if (ad.getUrl().equals(url)) {

        Calendar calendar1 = new GregorianCalendar();
        Calendar calendar2 = new GregorianCalendar();

        calendar1.setTime(ad.getDate());
        calendar1.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.MILLISECOND, 0);

        calendar2.setTime(dateFromBoard);
        calendar2.set(Calendar.SECOND, 0);
        calendar2.set(Calendar.MILLISECOND, 0);

        System.out.println("ad's date:" + ad.getDate() + "\ndate from board:  " + dateFromBoard +
                "\nequals: " + calendar1.getTime().equals(calendar2.getTime()));
        if (calendar1.getTime().equals(calendar2.getTime())) result = true;//!!!!!!!!!!!!!!!!!!
      }

    }


    return result;
  }

  private Path saveSetAndCreateNewFile() throws IOException {
    String stringPath = data + File.separator + file.getName().trim() + ".bak";
    Path path = Paths.get(stringPath);
    Path target = Files.createFile(path);
    Files.copy(file.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
    ads = new HashMap<>();
    return target;
  }


  private void setSerialize() {
    try {
      ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
      oos.writeObject(ads);
      oos.flush();
      oos.close();
      System.out.println("ads written");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Метод получения псевдослучайного целого числа от min до max (включая max);
   */
  static long rnd(long min, long max) {
    max -= min;
    final double random = Math.random();
    return Math.round((random * max) + min);
  }

//  public int[] getPriceRange() {
//    return priceRange;
//  }
//
//  public void setPriceRange(int[] priceRange) {
//    this.priceRange = priceRange;
//  }

  public void setEmailFrom(String emailFrom) {
    this.emailFrom = emailFrom;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  private static class Instance {
    private static final ParserManager parsmanager = new ParserManager();

    static ParserManager getInstance() {
      return parsmanager;
    }
  }
}