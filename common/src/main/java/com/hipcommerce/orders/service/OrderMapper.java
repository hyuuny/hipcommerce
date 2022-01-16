package com.hipcommerce.orders.service;

import com.hipcommerce.orders.domain.DeliveryInfo;
import com.hipcommerce.orders.domain.Order;
import com.hipcommerce.orders.domain.OrderItem;
import com.hipcommerce.orders.domain.OrderSheet;
import com.hipcommerce.orders.domain.OrderSheetItem;
import com.hipcommerce.orders.domain.Orderer;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class OrderMapper {

  public Order mapForm(OrderSheet orderSheet) {
    return mapForm(orderSheet, null, null);
  }

  public Order mapForm(OrderSheet orderSheet, DeliveryInfo deliveryInfo, Orderer orderer) {
    return mapForm(orderSheet, orderSheet.getUserId(), deliveryInfo, orderer);
  }

  public Order mapForm(
      OrderSheet orderSheet,
      final Long userId,
      DeliveryInfo deliveryInfo,
      Orderer orderer
  ) {

    List<OrderItem> orderItems = orderSheet.getOrderSheetItems().stream()
        .map(this::toOrderItem)
        .collect(Collectors.toList());

    Order order = Order.builder()
        .userId(userId)
        .deliveryInfo(deliveryInfo)
        .orderer(orderer)
        .orderSheetId(orderSheet.getId())
        .build();

    orderItems.forEach(order::addOrderItem);
    return order;
  }

  private OrderItem toOrderItem(OrderSheetItem orderSheetItem) {
    return orderSheetItem.toOrderItem();
  }

}
