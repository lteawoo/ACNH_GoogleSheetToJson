package kr.taeu.acnh.datasheet.valueformatter;

@FunctionalInterface
public interface ValueFormatter {
  public String format(String value);
}
