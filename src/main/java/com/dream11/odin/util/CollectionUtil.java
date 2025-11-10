package com.dream11.odin.util;

import java.util.List;
import java.util.function.Predicate;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CollectionUtil {
  public <T> List<T> filterList(Predicate<T> selector, List<T> list) {
    return list.stream().filter(selector).toList();
  }
}
