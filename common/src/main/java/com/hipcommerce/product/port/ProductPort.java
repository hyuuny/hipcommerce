package com.hipcommerce.product.port;

import com.hipcommerce.common.web.model.HttpStatusMessageException;
import com.hipcommerce.product.domain.Product;
import com.hipcommerce.product.domain.ProductRepository;
import com.hipcommerce.product.domain.event.ProductHitEvent;
import com.hipcommerce.product.dto.ProductDto.Create;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ProductPort {

  private final ProductRepository productRepository;
  private final ApplicationEventPublisher eventPublisher;


  public Long save(Create dto) {
    Product savedProduct = dto.toEntity();
    return productRepository.save(savedProduct).getId();
  }

  public Product getProduct(Long id) {
    return productRepository.findById(id).orElseThrow(
        () -> new HttpStatusMessageException(HttpStatus.BAD_REQUEST, "product.notFound", id)
    );
  }

  public Product getProduct(final Long id, boolean hit) {
    Product foundProduct = getProduct(id);

    if (hit) {
      eventPublisher.publishEvent(new ProductHitEvent(id));
    }

    return foundProduct;
  }

}
