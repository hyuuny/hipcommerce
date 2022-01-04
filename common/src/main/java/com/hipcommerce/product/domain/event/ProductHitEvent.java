package com.hipcommerce.product.domain.event;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProductHitEvent {

  private final Long id;

  public Long getId() {
    return id;
  }

}
