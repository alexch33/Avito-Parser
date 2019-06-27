import org.jsoup.nodes.Document;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdFactoryFromAdUrl {

  private AdFactoryFromAdUrl() {
  }

  public static Ad createNewAd(URL url) {
    Document document = null;
    try {
      document = Searcher.parse(url.toString());
    }catch (Exception e){
      e.printStackTrace();
    }
    sleep();


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
      if (date == null) return null;
      return new Ad(price, description, title, url, date);
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