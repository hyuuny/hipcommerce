package com.hipcommerce.product.service;

import com.hipcommerce.product.domain.Product;
import com.hipcommerce.product.dto.ProductDto.Create;
import com.hipcommerce.product.dto.ProductDto.Response;
import com.hipcommerce.product.port.ProductPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ProductService {

  private final ProductPort productPort;


  @Transactional
  public Response createProductAndGet(Create dto) {
    Long savedProductId = createProduct(dto);
    return getProduct(savedProductId, false);
  }

  @Transactional
  public Long createProduct(Create dto) {
    return productPort.save(dto).getId();
  }

  @Transactional
  public Response getProduct(final Long id, boolean hit) {
    Product foundProduct = productPort.getProduct(id, hit);
    return new Response(foundProduct);
  }


}
