package com.hipcommerce.orders.service;

import com.hipcommerce.orders.domain.Order;
import com.hipcommerce.orders.domain.OrderSheet;
import com.hipcommerce.orders.domain.OrderSummary;
import com.hipcommerce.orders.dto.OrderCheckoutDto;
import com.hipcommerce.orders.dto.OrderSheetDto.OrderSheetResult;
import com.hipcommerce.orders.port.OrderSheetPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class OrderSheetService {

  private final OrderSheetPort orderSheetPort;
  private final OrderSheetMapper orderSheetMapper;
  private final OrderMapper orderMapper;

  @Transactional
  public OrderSheetResult checkout(OrderCheckoutDto orderCheckout) {
    OrderSheet newOrderSheet = orderSheetMapper.mapForm(orderCheckout);
    Order newOrder = orderMapper.mapForm(newOrderSheet);
    return new OrderSheetResult(
        orderSheetPort.save(newOrderSheet),
        new OrderSummary(newOrder)
    );
  }

  public OrderSheetResult getOrderSheet(final Long id) {
    OrderSheet foundOrderSheet = orderSheetPort.getOrderSheet(id);
    return new OrderSheetResult(
        foundOrderSheet,
        new OrderSummary(orderMapper.mapForm(foundOrderSheet))
    );
  }


}
