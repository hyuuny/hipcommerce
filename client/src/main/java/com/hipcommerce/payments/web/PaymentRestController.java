package com.hipcommerce.payments.web;

import com.hipcommerce.payments.dto.PaymentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping(path = PaymentRestController.REQUEST_URL, produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class PaymentRestController {

  static final String REQUEST_URL = "/api/v1/payments";

  @PostMapping("/request")
  public ResponseEntity<?> requestPayments(@RequestBody PaymentDto.RequestBody requestBody) {

//    System.out.println(requestParam.getImpUid());
//    System.out.println(requestParam.getMerchantUid());
    return ResponseEntity.ok().build();
  }

}
