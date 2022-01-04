package com.hipcommerce.product.domain.event;

import com.hipcommerce.product.domain.Product;
import com.hipcommerce.product.port.ProductPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class ProductHitEventHandler {

  private final ProductPort productPort;

  @Async
  @EventListener
  @Transactional
  public void handle(ProductHitEvent event) {
    Product foundProduct = productPort.getProduct(event.getId());
    foundProduct.addHits();
  }

}
