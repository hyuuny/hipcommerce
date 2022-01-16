package com.hipcommerce.orders.port;

import com.hipcommerce.common.web.model.HttpStatusMessageException;
import com.hipcommerce.orders.domain.OrderSheet;
import com.hipcommerce.orders.domain.OrderSheetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class OrderSheetPort {

  private final OrderSheetRepository orderSheetRepository;

  public OrderSheet save(OrderSheet orderSheet) {
    return orderSheetRepository.save(orderSheet);
  }

  public OrderSheet getOrderSheet(final Long id) {
    return orderSheetRepository.findById(id).orElseThrow(
        () -> new HttpStatusMessageException(HttpStatus.BAD_REQUEST, "orderSheet.id.notFound")
    );
  }

}
