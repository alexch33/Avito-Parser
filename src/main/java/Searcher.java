import io.github.bonigarcia.wdm.WebDriverManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


class Searcher {
  private static List<Proxy> proxyList = new ArrayList<>();
  private static WebDriver webDriver = getConfiguredWebDriver();
  private File file;
  private final String mainUrl;
  private File data = ParserManager.getData();
  private Document mainDoc = null;
  private static final String userAgent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36";
  private static ProxySetter proxySetter;
  private static final Logger logger = Logger.getLogger("Searcher parse()");
  private static boolean isWebDriverProxy;

  public static WebDriver getConfiguredWebDriver() {
    if (webDriver == null) {
      if (!proxyList.isEmpty()) {
        webDriver = initializePhantomJSDriver(true);
        isWebDriverProxy = true;
      } else {
        isWebDriverProxy = false;
        webDriver = initializePhantomJSDriver(false);
      }
    } else {
      try {
        if (!isWebDriverProxy && !proxyList.isEmpty()) {
          webDriver.quit();
          webDriver = null;
          System.out.println("reconfiguring webdriver with proxy..........");
          getConfiguredWebDriver();
        }
        System.out.println("Checking driver connection...with ya.ru");
        webDriver.get("https://ya.ru");
        System.out.println("Driver working correctly............");
      } catch (Exception e) {
        e.printStackTrace();
        webDriver = null;
        deleteLastProxy();
        System.out.println("Driver reinitializing webdriver.........");
        getConfiguredWebDriver();
      }
    }

    return webDriver;
  }

  private static PhantomJSDriver initializePhantomJSDriver(boolean withProxy) {
    if (WebDriverManager.phantomjs().getBinaryPath() == null) {
      System.out.println("Setup phantom...");
      WebDriverManager.phantomjs().setup();
    }
    System.out.println("Phantom binary path: " + WebDriverManager.phantomjs().getBinaryPath());

    DesiredCapabilities caps = new DesiredCapabilities();
    caps.setJavascriptEnabled(true);
    caps.setCapability(CapabilityType.TAKES_SCREENSHOT, false);
    caps.setCapability(CapabilityType.SUPPORTS_NETWORK_CONNECTION, true);
    caps.setCapability("phantomjs.page.settings.loadImages", false);
    caps.setCapability("--disk-cache",  true);

    caps.setCapability(
            PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
            WebDriverManager.phantomjs().getBinaryPath()
    );

    if (withProxy) {
      System.out.println("setting proxy to driver................" + proxyList.get(0).address());
      ArrayList<String> cliArgsCap = new ArrayList<>();
      cliArgsCap.add(String.format("--proxy=%s", proxyList.get(0).address()));
//      cliArgsCap.add("--proxy-auth=username:password");
      cliArgsCap.add("--proxy-type=http");

      caps.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, cliArgsCap);
    }

    PhantomJSDriver driver = new PhantomJSDriver(caps);
    driver.manage().timeouts().pageLoadTimeout(60000, TimeUnit.MILLISECONDS);
    driver.manage().timeouts().implicitlyWait(5000, TimeUnit.MILLISECONDS);

    System.out.println("Phantom initialized successfully, with proxy: " + (withProxy ? proxyList.get(0).address() : false));

    return driver;
  }

  public static void setProxyList(List<Proxy> proxyList) {
    Searcher.proxyList = proxyList;
  }

  public static List<Proxy> getProxyList() {
    return proxyList;
  }

  public static void setProxySetter(ProxySetter proxySetter) {
    Searcher.proxySetter = proxySetter;
  }

  public Document getMainDoc() {
    return mainDoc;
  }

  public Searcher(String mainUrl) {
    System.out.println(data + "search constructor");
    this.mainUrl = mainUrl;
    int begin = mainUrl.lastIndexOf('/') + 1;
    int end = begin + 5;
    if (!data.exists()) try {
      Files.createDirectories(data.toPath());
    } catch (IOException e) {
      e.printStackTrace();
    }
    file = new File(data + File.separator + mainUrl.substring(begin, end).trim());
  }

  private Map<URL, Date> getUrlsAndDates(Elements elements) throws MalformedURLException {
    Map<URL, Date> urlAndTitles = new HashMap<>();
    String linkElement = "href";
    String dateElement = "c-2";//!!!!!!!!

    for (Element element : elements) {
//            System.out.println("elementelement&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&" + element.text());
      String link = element.getElementsByClass("snippet-link").attr("href");

      URL url = new URL("https://www.avito.ru" + link);

      String date = element.getElementsByClass(dateElement).first().text().toLowerCase();
      date = insertVafterToday(date);
//      System.out.println(date + " getUrlsAnd Dates!!!");
      Date date1 = AdFactoryFromAdUrl.parseAvitoDate(date);

      urlAndTitles.put(url, date1);
    }
    return urlAndTitles;
  }

  private String insertVafterToday(String date) {
    StringBuffer stringBuffer = new StringBuffer(date);
    stringBuffer.insert(date.length() - 6, " в");
    //System.out.println(stringBuffer.toString() + "!!!!!!!!!!!!!!!!!!!!!!!!!!!");

    return stringBuffer.toString();
  }

  Map<URL, Date> getUrlsAndDatesMapFromFile() throws IOException {
    Map<URL, Date> result;
    try {
      TimeUnit.MILLISECONDS.sleep(ParserManager.rnd(300, 1000));
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    if (!file.exists()) try {
      Files.createFile(file.toPath());
    } catch (IOException e) {
      e.printStackTrace();
    }

    System.out.println(file.getAbsolutePath() + "absolute path");
//       Document doc = Jsoup.parse(file, "UTF-8", mainUrl);
    Document doc = parse(mainUrl);

    Elements sellBoardElements = new Elements();
    sellBoardElements = doc.select(".item_table-description");

    result = getUrlsAndDates(sellBoardElements);
//        System.out.println("result <<<<<<<<<<<<<<<<<<<<" + result);
    return result;
  }


  public static Document parse(String url) {
    Document document = null;

    try {
      if (!proxyList.isEmpty()) {
        logger.log(Level.WARNING, "Using Proxy...................." + " " + proxyList.get(0) + "\n" + "url: " + url);
        try {
          document = Jsoup.connect(url)
                  .userAgent(userAgent)
                  .proxy(proxyList.get(0))
                  .get();
        }catch (IOException e) {
          System.err.println(e.getMessage());
          deleteLastProxy();
        }

      } else {
        logger.log(Level.WARNING, "Without Proxy...................." + "\n" + "url: " + url);
        try {
          document = Jsoup.connect(url)
                  .userAgent(userAgent)
                  .get();
        } catch (IOException e) {
          System.err.println(e.getMessage());
        }
      }

    } catch (Exception e) {
      if (!proxyList.isEmpty()) {
        deleteLastProxy();
      }
      e.printStackTrace();
    }

    if (document != null) {
      System.out.println("Banned: " + document.toString().contains("Доступ с Вашего IP временно ограничен"));
    } else {
      if (proxyList.isEmpty()) {
        System.out.println("No Connection, parsing main URL Failed.");
      } else {
        System.out.println("Connection Error, changing proxy...");
      }
    }

    return document;
  }

  static void deleteLastProxy() {
    if (proxyList.size() > 0) {
      System.out.println("deleting proxy..." + proxyList.get(0));
      proxySetter.deleteProxy(proxyList.remove(0));
//            TimeUnit.HOURS.sleep(1);
      System.out.println("Proxy was removed");
    } else {
      try {
        TimeUnit.MILLISECONDS.sleep(10000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

  }
}
