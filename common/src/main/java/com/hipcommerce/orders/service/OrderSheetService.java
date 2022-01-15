package com.hipcommerce.orders.service;

import com.hipcommerce.orders.port.OrderSheetPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class OrderSheetService {

  private final OrderSheetPort orderSheetPort;




}
