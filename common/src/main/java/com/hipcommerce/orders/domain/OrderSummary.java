package com.hipcommerce.orders.domain;

import com.hipcommerce.common.money.domain.Money;
import lombok.Getter;

@Getter
public class OrderSummary {

  private Money totalProductPrice;
  private Money totalDiscountPrice;
  private Money TotalPrice;

  public OrderSummary(Order order) {
    this.totalProductPrice = order.calculateTotalProductPrice();
    this.totalDiscountPrice = order.calculateTotalDiscountPrice();
    this.TotalPrice = order.calculateTotalPrice();
  }

}
