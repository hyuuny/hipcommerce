package com.hipcommerce.payments.service;

import com.hipcommerce.payments.domain.Payment;
import com.hipcommerce.payments.dto.PaymentDto.PayResult;
import com.hipcommerce.payments.dto.PaymentDto.Response;
import com.hipcommerce.payments.port.PaymentPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class PaymentService {

  private final PaymentPort paymentPort;

  @Transactional
  public Response payAndGet(PayResult dto) {
    Long savePaymentId = pay(dto);
    return getPayment(savePaymentId);
  }

  @Transactional
  public Long pay(PayResult dto) {
    return paymentPort.save(dto);
  }

  public Response getPayment(final Long id) {
    Payment foundPayment = paymentPort.getPayment(id);
    return new Response(foundPayment);
  }

  public Response getPayment(final String impUid) {
    Payment foundPayment = paymentPort.getPayment(impUid);
    return new Response(foundPayment);
  }
  
}
