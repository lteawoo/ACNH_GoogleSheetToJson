package kr.taeu.acnh.datasheet.valueformatter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class ValueFormatters {
  public static ValueFormatter<String> extractImageUrl() {
    return v -> v.substring(8, v.length() - 3);
  }
  
  public static ValueFormatter<String> normalizeUse() {
    return v -> {
      if(ValueFormatters.isInteger(v)) {
        return v;
      }
      
      if(v.equals("Unlimited")) {
        return "-1";
      }
      
      // unexpected value
      System.out.println("unexpected value: " + v);
      return "-1";
    };
  }
  
  public static ValueFormatter<String> normalizeBirthday() {
    return v -> {
      DateTimeFormatter inFormatter = DateTimeFormatter.ofPattern("yyyy/M/d");
      DateTimeFormatter outFormatter = DateTimeFormatter.ofPattern("MM/dd");
      
      String tempDate = LocalDate.now().getYear() + "/" + v;
      LocalDate localDate = LocalDate.parse(tempDate, inFormatter);
      return localDate.format(outFormatter);
    };
  }
  
  public static ValueFormatter<String[]> normalizeSource() {
    return v -> Arrays.stream(((v.contains("\n")) ? v.split("\n") : v.split(";")))
        .map(s -> s.trim())
        .toArray(String[]::new); 
  }
  
  private static boolean isInteger(String input) {
    try {
      Integer.parseInt(input);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }
}