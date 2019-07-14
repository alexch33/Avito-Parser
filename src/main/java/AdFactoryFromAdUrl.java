import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.NoSuchElementException;

import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdFactoryFromAdUrl {
  private static WebDriver driver;

  private AdFactoryFromAdUrl() {
  }

  private static void configureWebDriver(){
    System.out.println("Check web Driver Configuration...........");
    driver = Searcher.getConfiguredWebDriver();
    System.out.println("Web driver configured successfully");
  }

  public static Ad createNewAd(URL url) {
    Document document = null;
    try {
      document = Searcher.parse(url.toString());
    }catch (Exception e){
      e.printStackTrace();
    }
    sleep();

    // web Driver section ****************************************
    // Web Driver config
    configureWebDriver();

    try {
      driver.get(url.toString());
    } catch (Exception e) {
      System.err.println("Driver connection failed trying to reconfigure...");
      configureWebDriver();
      try {
        driver.get(url.toString());
      } catch (Exception e1){
        System.err.println(e1.getMessage());
        System.out.println("Driver Connection failed, skipping.....");
      }
    }

    String phoneNumberBase64 = null;
    try {
      // Достаём кнопку, жмём и пробуем получить елемент с картинкой номера телефона
        driver.findElement(
              By.className("item-phone-number")).findElement(By.tagName("a")).click();

      TimeUnit.MILLISECONDS.sleep(1000);

      phoneNumberBase64 = driver.findElement(
              By.className("item-phone-button_with-img")).findElement(By.tagName("img")).getAttribute("src");
    } catch (NoSuchElementException e1) {
      System.err.println("Phone Number Not found!!!, phone number will be empty");
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    // web Driver section ****************************************

    System.out.println("document is null: " + (document == null));

    if (document != null) {
      //достаём цену и содержание
      String priceText = document.getElementsByClass("js-item-price").text();
      String temp = priceText.substring(0, priceText.length() / 2);

      if (temp.length() > 0) {
        temp = temp.replaceAll(" ", "");
      }

      else temp = "0";

      int price = Integer.parseInt(temp);
      String description = document.getElementsByClass("item-description").tagName("p").text();
      String title = document.getElementsByClass("title-info-title-text").text();
      String temp2 = document.getElementsByClass("title-info-metadata-item-redesign").text();
      String stringDate = getTimeFromString(temp2);
      Date date = AvitoDateParser.avitoDatePars(stringDate);

      if (date == null) {
        Calendar calendar1 = new GregorianCalendar();

        calendar1.set(Calendar.YEAR, 2000);
        calendar1.set(Calendar.HOUR, 1);
        calendar1.set(Calendar.MONTH, 1);
        calendar1.set(Calendar.SECOND, 1);
        calendar1.set(Calendar.MILLISECOND, 1);
      }

      List<String> urls = new ArrayList<>();
      Elements imageEls = document.getElementsByClass("gallery-list-item-link");
      for (Element el: imageEls) {
        String imgUrl = el.attr("style").substring(24, el.attr("style").length() - 2);
        imgUrl = imgUrl.replaceAll("\\d+x\\d+", "640x480");
        urls.add(imgUrl);
      }

      Ad ad = new Ad(price, description, title, url, date, urls.toArray(new String[0]), phoneNumberBase64);
      ad.setPhoneNumberString(OcrPhoneScrapper.getInstance().ocrPhoneFromBase64Picture(ad.getPhoneNumberBase64()));

      return ad;
    }
    return null;
  }

  public static Date parseAvitoDate(String date) {


//        System.out.println("parseravitodate " + date);

    Date result;

    result = AvitoDateParser.avitoDatePars(date);
    return result;

  }


  private static void sleep() {
    try {
      int a = (int) ParserManager.rnd(2000, 6000);

      TimeUnit.MILLISECONDS.sleep(a);
    } catch (InterruptedException e1) {
      e1.printStackTrace();
    }
  }

  private static String getTimeFromString(String line){
    String string = line.toLowerCase();

    String pattern = "размещено (.*\\d{2}:\\d{2})";

    Pattern r = Pattern.compile(pattern);

    Matcher m = r.matcher(string);

    if (m.find()) {
      return m.group(1).trim();
    }else {
      System.out.println("Time not found in string");
    }

    return null;
  }
}