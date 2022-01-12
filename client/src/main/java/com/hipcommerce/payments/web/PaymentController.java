package com.hipcommerce.payments.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = PaymentController.REQUEST_URL)
@Controller
public class PaymentController {

  static final String REQUEST_URL = "/payments";
  static final String VIEWS_PATH = "payments";

  @GetMapping("/request")
  public String paymentForm(Model model) {
    model.addAttribute("data", "test");
    return VIEWS_PATH + "/request-form";
  }

//  @PostMapping("/request/verify")
//  public void paymentSuccess(
//      @RequestParam("imp_uid") String impUid,
//      @RequestParam("merchant_uid") String merchantIid
//      ) {
//    log.info("impUid={}", impUid);
//    log.info("merchantIid={}", merchantIid);
//  }



}
