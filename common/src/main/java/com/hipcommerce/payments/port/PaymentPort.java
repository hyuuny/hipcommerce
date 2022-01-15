package com.hipcommerce.payments.port;

import com.hipcommerce.common.web.model.HttpStatusMessageException;
import com.hipcommerce.payments.domain.Payment;
import com.hipcommerce.payments.domain.PaymentRepository;
import com.hipcommerce.payments.dto.PaymentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PaymentPort {

  private final PaymentRepository paymentRepository;

  public Long save(PaymentDto.PayResult dto) {
    Payment newPayment = dto.toEntity();
    return paymentRepository.save(newPayment).getId();
  }

  public Payment getPayment(final Long id) {
    return paymentRepository.findById(id).orElseThrow(
        () -> new HttpStatusMessageException(HttpStatus.BAD_REQUEST, "payment.id.notFound", id)
    );
  }

  public Payment getPayment(final String impUid) {
    return paymentRepository.findByImpUid(impUid).orElseThrow(
        () -> new HttpStatusMessageException(HttpStatus.BAD_REQUEST, "payment.impUid.notFound", impUid)
    );
  }

}
