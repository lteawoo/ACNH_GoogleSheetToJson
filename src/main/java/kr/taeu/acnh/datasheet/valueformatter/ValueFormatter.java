package kr.taeu.acnh.datasheet.valueformatter;

@FunctionalInterface
public interface ValueFormatter<T> {
  public T format(String value);
}
