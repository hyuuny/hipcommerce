package com.hipcommerce.common.money.infra;

import com.hipcommerce.common.money.domain.Ratio;
import java.util.Optional;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class RatioConverter implements AttributeConverter<Ratio, Double> {

  @Override
  public Double convertToDatabaseColumn(Ratio ratio) {
    return Optional.ofNullable(ratio)
      .map(Ratio::getRate)
      .orElse(0d);
  }

  @Override
  public Ratio convertToEntityAttribute(Double rate) {
    return Ratio.valueOf(rate);
  }

}
