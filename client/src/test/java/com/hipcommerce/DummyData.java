package com.hipcommerce;

import com.hipcommerce.categories.dto.CategoryDto;

public class DummyData {

  public static CategoryDto.Create.CreateBuilder aCategory() {
    return CategoryDto.Create.builder()
        .name("아우터")
        .level(1)
        .priorityNumber(1)
        .iconImageUrl("https://hipcommerce-bucket.s3.ap-northeast-2.amazonaws.com/data/image_1596187406745_1000.jpg")
        ;
  }

}
