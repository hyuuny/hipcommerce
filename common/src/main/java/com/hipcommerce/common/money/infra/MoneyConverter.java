package com.hipcommerce.common.money.infra;

import com.hipcommerce.common.money.domain.Money;
import java.util.Optional;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class MoneyConverter implements AttributeConverter<Money, Long> {

  @Override
  public Long convertToDatabaseColumn(Money money) {
    return Optional.ofNullable(money)
      .map(Money::longValue)
      .orElse(0L);
  }

  @Override
  public Money convertToEntityAttribute(Long amount) {
    return Money.wons(amount);
  }

}
