package kr.taeu.acnh.datasheet.valueformatter;

public class ValueFormatters {
  public static ValueFormatter extractImageUrl() {
    return v -> v.substring(8, v.length() - 3);
  }
  
  public static ValueFormatter normalizeUse() {
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
  
  public static ValueFormatter normalizeBirthday() {
    return v -> v.substring(8, v.length() - 3);
  }
  
  public static ValueFormatter normalizeSource() {
    return v -> v.substring(8, v.length() - 3);
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