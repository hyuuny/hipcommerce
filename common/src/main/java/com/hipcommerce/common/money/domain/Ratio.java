package com.hipcommerce.common.money.domain;

import java.math.BigDecimal;

public class Ratio {

  private double rate;

  public static Ratio fromPercentage(int rate) {
    return new Ratio(BigDecimal.valueOf(rate).divide(BigDecimal.valueOf(100)).doubleValue());
  }

  public static Ratio valueOf(double rate) {
    return new Ratio(rate);
  }

  Ratio(double rate) {
    this.rate = rate;
  }

  Ratio() {
  }

  public Money of(Money price) {
    return price.times(rate);
  }

  public double getRate() {
    return rate;
  }
}
