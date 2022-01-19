package com.hipcommerce.orders.port;

import com.hipcommerce.common.web.model.HttpStatusMessageException;
import com.hipcommerce.orders.domain.Order;
import com.hipcommerce.orders.domain.OrderItem;
import com.hipcommerce.orders.domain.OrderRepository;
import com.hipcommerce.orders.domain.OrderSheet;
import com.hipcommerce.orders.dto.RetrieveOrderPaymentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class OrderPort {

  private final OrderRepository orderRepository;
  private final OrderSheetPort orderSheetPort;

  public Order save(Order order) {
    return orderRepository.save(order);
  }

  public OrderSheet getOrderSheet(final Long orderSheetId) {
    return orderSheetPort.getOrderSheet(orderSheetId);
  }

  public RetrieveOrderPaymentDto getOrderDetail(final Long id) {
    return orderRepository.fetchById(id).orElseThrow(
        () -> new HttpStatusMessageException(HttpStatus.BAD_REQUEST, "order.id.notFound", id)
    );
  }

  public Order getOrder(final Long id) {
    return orderRepository.findById(id).orElseThrow(
        () -> new HttpStatusMessageException(HttpStatus.BAD_REQUEST, "order.id.notFound", id)
    );
  }

  public OrderItem getOrder(final Long id, final Long orderItemId) {
    return orderRepository.findByIdAndOrderItemId(id, orderItemId).orElseThrow(
        () -> new HttpStatusMessageException(HttpStatus.BAD_REQUEST, "order.orderItem.notFound")
    );
  }

}
