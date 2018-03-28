import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class AvitoDateParser {
  private final static String TODAY = "сегодня";
  private final static String YESTERDAY = "вчера";


  public static Date avitoDatePars(String date) {

    if (isNumber(date.charAt(0))) {
      // return parseIfBeginNumber(date);
      return null;
    }
    if (date.charAt(0) == 'с') {
      return parseIfBeginC(date);
    }
    if (date.charAt(0) == 'в') {
      return null;
// return parseIfBeginB(date);
    }

    return null;
  }

  private static Date parseIfBeginB(String date) {
    System.out.println(date + " begins B");

    int hh, mm;
    String[] strings = date.split(" ");
    String[] time = strings[strings.length - 1].split(":");
    hh = Integer.parseInt(time[0].substring(2).trim());
    mm = Integer.parseInt(time[1]);

    Calendar calendar = new GregorianCalendar();
    int day = calendar.get(Calendar.DAY_OF_MONTH) - 1;
    calendar.set(Calendar.DAY_OF_MONTH, day);
    calendar.set(Calendar.HOUR_OF_DAY, hh);
    calendar.set(Calendar.MINUTE, mm);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);

    return null;
  }

  private static Date parseIfBeginC(String date) {
    // System.out.println(date + " begins C");
    int hh, mm;
    String[] strings = date.split(" ");
    String[] time = strings[strings.length - 1].split(":");
    // System.out.println(time[0].substring(2).trim() + "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
    // System.out.println(Arrays.toString(time) + " " + time[0] + " " + time[1]);
    String datetmp;
    if (time[0].length() > 2)
      datetmp = time[0].substring(2).trim();
    else
      datetmp = time[0];
    //System.out.println(datetmp + "**************************************************************");
    hh = Integer.parseInt(datetmp);
    mm = Integer.parseInt(time[1]);

    Calendar calendar = new GregorianCalendar();
    calendar.set(Calendar.HOUR_OF_DAY, hh);
    calendar.set(Calendar.MINUTE, mm);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);

    return calendar.getTime();
  }

  private static Date parseIfBeginNumber(String date) {
    //System.out.println(date + " begins Num");


    String[] strings = date.split(" ");
    String day = strings[0].trim();
    String month = strings[1].trim();
    month = replaceMonth(month);
    String time = strings[strings.length - 1].trim();

    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(day).append(" ").append(month).append(" ").append(time);
    System.out.println("string builder: " + stringBuilder.toString());
    String adDate = stringBuilder.toString();
    System.out.println("adDate: " + adDate);

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM hh:mm", new Locale("ru", "RU"));
    Date date1;
    try {
      date1 = simpleDateFormat.parse(adDate);
    } catch (ParseException e) {
      date1 = new Date();
      e.printStackTrace();
    }

    Calendar calendar = new GregorianCalendar();
    int year = calendar.get(Calendar.YEAR);
    calendar.setTime(date1);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    calendar.set(Calendar.YEAR, year);


    return calendar.getTime();
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
