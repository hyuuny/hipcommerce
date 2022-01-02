package com.hipcommerce.product.service;

import com.hipcommerce.product.domain.Product.Status;
import com.hipcommerce.product.dto.ProductDto.Create;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProductServiceTest {

  @Autowired
  private ProductService productService;

  @Test
  void createProduct() {
    Create create = Create.builder()
        .name("힙한 바지")
        .price(5000L)
        .status(Status.ON_SALE)
        .build();

    productService.createProduct(create);
  }

}