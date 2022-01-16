package com.hipcommerce.orders.dto;

import com.hipcommerce.orders.domain.DeliveryInfo;
import com.hipcommerce.orders.domain.Order;
import com.hipcommerce.orders.domain.Order.PayMethod;
import com.hipcommerce.orders.domain.OrderItem;
import com.hipcommerce.orders.domain.OrderSummary;
import com.hipcommerce.orders.domain.Orderer;
import com.hipcommerce.payments.domain.Payment;
import java.util.List;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class RetrieveOrderPaymentDto {

  private Order order;

  private Payment payment;

  public Long getId() {
    return order.getId();
  }

  public String getOrderCode() {
    return order.getCode();
  }

  public Long getUserId() {
    return order.getUserId();
  }

  public DeliveryInfo getDeliveryInfo() {
    return order.getDeliveryInfo();
  }

  public Orderer getOrderer() {
    return order.getOrderer();
  }

  public PayMethod getPayMethod() {
    return order.getPayMethod();
  }

  public OrderSummary getOrderSummary() {
    return new OrderSummary(order);
  }

  public List<OrderItem> getOrderItems() {
    return order.getOrderItems();
  }

  public Payment getPayment() {
    return payment;
  }


}
