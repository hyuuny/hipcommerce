package com.hipcommerce.common.money.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;

public class Money extends Number implements Comparable<BigDecimal> {

  public static final Money ZERO = Money.wons(0);

  private final BigDecimal amount;

  public static Money wons(long amount) {
    return new Money(BigDecimal.valueOf(amount));
  }

  public static Money wons(double amount) {
    return new Money(BigDecimal.valueOf(amount));
  }

  public static <T> Money sum(Collection<T> bags, Function<T, Money> monetary) {
    return bags.stream().map(monetary).reduce(Money.ZERO, Money::plus);
  }

  Money() {
    this(BigDecimal.ZERO);
  }

  Money(BigDecimal amount) {
    this.amount = amount;
  }

  public Money plus(Money amount) {
    return new Money(this.amount.add(amount.amount));
  }

  public Money minus(Money amount) {
    return new Money(this.amount.subtract(amount.amount));
  }

  public Money times(double percent) {
    return new Money(this.amount.multiply(BigDecimal.valueOf(percent)));
  }

  public Money divide(double divisor) {
    return new Money(amount.divide(BigDecimal.valueOf(divisor), 2, RoundingMode.HALF_UP));
  }

  public boolean isLessThan(Money other) {
    return amount.compareTo(other.amount) < 0;
  }

  public boolean isLessThanOrEqual(Money other) {
    return amount.compareTo(other.amount) <= 0;
  }

  public boolean isGreaterThanOrEqual(Money other) {
    return amount.compareTo(other.amount) >= 0;
  }

  public boolean isGreaterThan(Money other) {
    return amount.compareTo(other.amount) > 0;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public int intValue() {
    return amount.intValue();
  }

  public long longValue() {
    return amount.longValue();
  }

  public float floatValue() {
    return amount.floatValue();
  }

  public double doubleValue() {
    return amount.doubleValue();
  }

  public String wonStringValue() {
    DecimalFormat formatter = new DecimalFormat("###,###");

    return (formatter.format(longValue()) + "원");
  }

  @Override
  public int compareTo(BigDecimal o) {
    return amount.compareTo(o);
  }

  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }

    if (!(object instanceof Money)) {
      return false;
    }

    Money other = (Money) object;
    return Objects.equals(amount.doubleValue(), other.amount.doubleValue());
  }

  public int hashCode() {
    return Objects.hashCode(amount);
  }

  @Override
  public String toString() {
    return amount.toString();
  }


}
