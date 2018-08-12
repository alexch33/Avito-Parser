import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AvitoDateParser {
  private final static String TODAY = "сегодня";
  private final static String YESTERDAY = "вчера";


  public static Date avitoDatePars(String date) {
    String hours = getFirstStringGroupRegex(date, "^(\\d+).+(час).*");
    String minutes = getFirstStringGroupRegex(date, "^(\\d+).+(мин).*");
    if (hours != null) {
      return calculateDateFromHours(hours);
    } else if (minutes != null) {
      return calculateDateFromMinutes(minutes);
    }

    return null;
  }

  private static String getFirstStringGroupRegex(String userNameString, String pattern){
    Pattern p = Pattern.compile(pattern);
    Matcher m = p.matcher(userNameString);
    if (m.find())
    {
      return m.group(1);
    }
    return null;
  }

  private static Date calculateDateFromHours(String hoursBack) {
    if (hoursBack != null) {
      int hours = Integer.parseInt(hoursBack.trim());
      Calendar cal = Calendar.getInstance();
      cal.add(Calendar.HOUR, -hours);
      return cal.getTime();
    }
    return null;
  }

  public static Date calculateDateFromMinutes(String minutesBack) {
    if (minutesBack != null) {
      int minutes = Integer.parseInt(minutesBack.trim());
      Calendar cal = Calendar.getInstance();
      cal.add(Calendar.MINUTE, -minutes);
      return cal.getTime();
    }
    return null;
  }

  private static String replaceMonth(String s) {
    Map<String, String> map = new HashMap<>(12);
    map.put("января", "январь");
    map.put("февраля", "февраль");
    map.put("марта", "март");
    map.put("апреля", "апрель");
    map.put("мая", "май");
    map.put("июня", "июнь");
    map.put("июля", "июль");
    map.put("августа", "август");
    map.put("сентября", "сентябрь");
    map.put("октября", "октябрь");
    map.put("ноябрь", "ноябрь");
    map.put("декабря", "декабрь");

    String result = null;

    if (map.containsKey(s.toLowerCase())) {
      result = s.replace(s, map.get(s));
    }

    return result;
  }

  private static boolean isNumber(char c) {
    try {
      int a = Integer.parseInt(c + "");
    } catch (NumberFormatException e) {
      return false;
    }
    return true;
  }

  public static boolean isToday(Date date) {
    if (date == null) return false;
    Calendar in = new GregorianCalendar();
    Calendar calendar = new GregorianCalendar();
    in.setTime(date);
    int dayIn = in.get(Calendar.DAY_OF_MONTH);
    int dayCal = calendar.get(Calendar.DAY_OF_MONTH);

    return dayIn == dayCal;
  }
}
