package com.hipcommerce.orders.service;

import com.hipcommerce.orders.domain.Order;
import com.hipcommerce.orders.domain.OrderItem;
import com.hipcommerce.orders.domain.OrderSheet;
import com.hipcommerce.orders.dto.OrderDto;
import com.hipcommerce.orders.dto.OrderDto.OrderResult;
import com.hipcommerce.orders.dto.OrderPlaceDto;
import com.hipcommerce.orders.port.OrderPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class OrderService {

  private final OrderPort orderPort;
  private final OrderMapper orderMapper;

  @Transactional
  public OrderDto.OrderResult place(OrderPlaceDto dto) {
    OrderSheet foundOrderSheet = orderPort.getOrderSheet(dto.getOrderSheetId());
    Order newOrder = orderMapper.mapForm(
        foundOrderSheet,
        dto.getUserId(),
        dto.getDeliveryInfo(),
        dto.getOrderer()
    );

    newOrder.changePayMethod(dto.getPayMethod());
    newOrder.getOrderItems().stream()
        .forEach(OrderItem::paid);

    Order savedOrder = orderPort.save(newOrder);
    log.info("Place order={}", savedOrder);
    log.info("orderCode={}", savedOrder.getCode());
    return new OrderResult(orderPort.getOrderDetail(savedOrder.getId()));
  }

  public OrderDto.OrderResult getOrder(final Long id) {
    return new OrderResult(orderPort.getOrderDetail(id));
  }

}
