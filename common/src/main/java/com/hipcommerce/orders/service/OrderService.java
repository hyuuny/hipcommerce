package com.hipcommerce.orders.service;

import com.hipcommerce.orders.domain.Order;
import com.hipcommerce.orders.domain.OrderItem;
import com.hipcommerce.orders.domain.OrderSheet;
import com.hipcommerce.orders.dto.OrderDto;
import com.hipcommerce.orders.dto.OrderDto.DetailedSearchCondition;
import com.hipcommerce.orders.dto.OrderDto.OrderResult;
import com.hipcommerce.orders.dto.OrderDto.Response;
import com.hipcommerce.orders.dto.OrderPlaceDto;
import com.hipcommerce.orders.port.OrderPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
  public OrderResult place(OrderPlaceDto dto) {
    OrderSheet foundOrderSheet = orderPort.getOrderSheet(dto.getOrderSheetId());
    Order newOrder = orderMapper.mapForm(
        foundOrderSheet,
        dto.getUserId(),
        dto.getDeliveryInfo(),
        dto.getOrderer()
    );
    newOrder.place(dto.getPayMethod());

    Order savedOrder = orderPort.save(newOrder);
    log.info("place.orderCode={}", savedOrder.getCode());
    return new OrderResult(orderPort.getOrderDetail(savedOrder.getId()));
  }

  public OrderResult getOrder(final Long id) {
    return new OrderResult(orderPort.getOrderDetail(id));
  }

  @Transactional
  public OrderResult changeDeliveryInfo(final Long id, OrderDto.ChangeDeliveryInfo dto) {
    Order existingOrder = orderPort.getOrder(id);
    existingOrder.changeDeliveryInfo(dto.getDeliveryInfo());
    return getOrder(id);
  }

  public Page<Response> retrieveOrder(DetailedSearchCondition searchCondition, Pageable pageable) {
    Page<Response> orders = orderPort.retrieveOrder(searchCondition, pageable);
    return orders;
  }

  @Transactional
  public void changeOrderItemsStatus(OrderDto.ChangeOrderItemsStatus dto) {
    dto.getChangeOrderItemStatuses().stream()
        .forEach(changeOrderItemStatus -> {
          OrderItem existingOrderItem = orderPort.getOrder(changeOrderItemStatus.getOrderId(),
              changeOrderItemStatus.getOrderItemId());
          existingOrderItem.changeStatus(changeOrderItemStatus.getStatus());
        });
  }

  @Transactional
  public OrderResult purchaseCompleted(OrderDto.ChangeOrderItemStatus dto) {
    OrderItem existingOrderItem = orderPort.getOrder(dto.getOrderId(), dto.getOrderItemId());
    existingOrderItem.purchaseCompleted();
    return getOrder(dto.getOrderId());
  }

  @Transactional
  public OrderResult cancelRequest(OrderDto.ChangeOrderItemStatus dto) {
    OrderItem existingOrderItem = orderPort.getOrder(dto.getOrderId(), dto.getOrderItemId());
    existingOrderItem.cancelRequest();
    return getOrder(dto.getOrderId());
  }

  @Transactional
  public OrderResult returnRequest(OrderDto.ChangeOrderItemStatus dto) {
    OrderItem existingOrderItem = orderPort.getOrder(dto.getOrderId(), dto.getOrderItemId());
    existingOrderItem.returnRequest();
    return getOrder(dto.getOrderId());
  }

}
