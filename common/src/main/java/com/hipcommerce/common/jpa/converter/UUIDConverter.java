package com.hipcommerce.common.jpa.converter;

import java.util.UUID;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import org.springframework.util.ObjectUtils;

@Converter(autoApply = true)
public class UUIDConverter implements AttributeConverter<UUID, String> {

  @Override
  public String convertToDatabaseColumn(UUID attribute) {
    return attribute.toString();
  }

  @Override
  public UUID convertToEntityAttribute(String dbData) {
    if (ObjectUtils.isEmpty(dbData)) {
      return null;
    }
    return UUID.fromString(dbData);
  }

}
