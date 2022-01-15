package com.hipcommerce.orders.port;

import com.hipcommerce.orders.domain.OrderSheetRepository;
import com.hipcommerce.orders.service.OrderSheetMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class OrderSheetPort {

  private final OrderSheetRepository orderSheetRepository;
  private final OrderSheetMapper orderSheetMapper;


}
