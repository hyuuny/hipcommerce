package com.hipcommerce.common.generators;

import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.ofPattern;
import static org.springframework.util.ObjectUtils.isEmpty;

import java.util.concurrent.atomic.AtomicLong;

public class UniqueIdGenerator {

  private static AtomicLong LAST_TIME = new AtomicLong(0);

  private UniqueIdGenerator() {
  }

  public static String nextProductCode() {
    return nextId("P");
  }

  private static String nextId(final String prefix) {
    long prev;
    long next = Long.parseLong(now().format(ofPattern("yyyyMMddHHmmssSSS")));
    do {
      prev = LAST_TIME.get();
      next = next > prev ? next : prev + 1;
    } while (!LAST_TIME.compareAndSet(prev, next));
    return isEmpty(null) ? Long.toString(next) : prefix + next;
  }

}
