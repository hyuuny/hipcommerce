package com.hipcommerce.product.service;

import com.hipcommerce.common.web.model.HttpStatusMessageException;
import com.hipcommerce.product.domain.Product;
import com.hipcommerce.product.domain.ProductRepository;
import com.hipcommerce.product.dto.ProductDto.Create;
import com.hipcommerce.product.dto.ProductDto.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProductService {

  private final ProductRepository productRepository;

  public Long createProduct(Create dto) {
    Product newProduct = dto.toEntity();
    return productRepository.save(newProduct).getId();
  }

  public Response getProduct(final Long id) {
    Product existingProduct = productRepository.findById(id).orElseThrow(
        () -> new HttpStatusMessageException(HttpStatus.BAD_REQUEST, "product.notFound", id)
    );
    return new Response(existingProduct);
  }

}
