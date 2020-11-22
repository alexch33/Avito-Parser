import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AvitoDateParser {
  public static Date avitoDatePars(String date) {
    String hours = getFirstStringGroupRegex(date, "^(\\d+).+(час).*");
    String minutes = getFirstStringGroupRegex(date, "^(\\d+).+(мин).*");
    String today = getFirstStringGroupRegex(date, ".*(\\d{2}:\\d{2})");
    if (hours != null) {
      return calculateDateFromHours(hours);
    } else if (minutes != null) {
      return calculateDateFromMinutes(minutes);
    } else if (today != null) {
      String todayHours = getFirstStringGroupRegex(today, "(\\d{2}):\\d{2}");
      String todayMinutes = getFirstStringGroupRegex(today, "\\d{2}:(\\d{2})");
      if ((todayHours != null) && (todayMinutes != null)) {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(todayHours));
        calendar.set(Calendar.MINUTE, Integer.parseInt(todayMinutes));
        return calendar.getTime();
      }
    }

    return null;
  }

   private static String getFirstStringGroupRegex(String userNameString, String pattern){
    if (userNameString == null) return  null;

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

  private static Date calculateDateFromMinutes(String minutesBack) {
    if (minutesBack != null) {
      int minutes = Integer.parseInt(minutesBack.trim());
      Calendar cal = Calendar.getInstance();
      cal.add(Calendar.MINUTE, -minutes);
      return cal.getTime();
    }
    return null;
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
