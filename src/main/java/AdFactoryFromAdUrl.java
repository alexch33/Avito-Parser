import org.jsoup.nodes.Document;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;

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
      String priceText = document.getElementsByClass("price-value-string js-price-value-string").text();
      String temp;
      if (priceText.contains("\u20BD")) {
        temp = priceText.substring(0, priceText.indexOf('\u20BD') - 1).replaceAll(" ", "");
      } else temp = "0";
      int price = Integer.parseInt(temp);
      String description = document.getElementsByClass("item-description").tagName("p").text();
      String title = document.getElementsByClass("title-info-title-text").text();
      String temp2 = document.getElementsByClass("title-info-metadata-item").text();
      String stringDate = temp2.substring(temp2.lastIndexOf("размещено") + 10, temp2.lastIndexOf("в") + 7).trim();
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
}