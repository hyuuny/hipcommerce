package com.hipcommerce.payments.web;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.hipcommerce.payments.dto.PaymentDto.PayResult;
import com.hipcommerce.payments.dto.PaymentDto.Response;
import com.hipcommerce.payments.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping(path = PaymentRestController.REQUEST_URL, produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class PaymentRestController {

  static final String REQUEST_URL = "/api/v1/payments";

  private final PaymentService paymentService;
  private final PaymentResourceAssembler paymentResourceAssembler;

  @Operation(summary = "결제 결과")
  @PostMapping("/request")
  public ResponseEntity<?> requestPayments(@RequestBody PayResult payResult) {
    Response foundPayment = paymentService.payAndGet(payResult);
    return ResponseEntity.ok(paymentResourceAssembler.toModel(foundPayment));
  }

  @Operation(summary = "결제 내역 상세 조회")
  @GetMapping("/{id}")
  public ResponseEntity<EntityModel<Response>> getPayment(@PathVariable final Long id) {
    Response foundPayment = paymentService.getPayment(id);
    return ResponseEntity.ok(paymentResourceAssembler.toModel(foundPayment));
  }


  @Component
  static class PaymentResourceAssembler implements
      RepresentationModelAssembler<Response, EntityModel<Response>> {

    @Override
    public EntityModel<Response> toModel(Response entity) {
      return EntityModel.of(
          entity,
          linkTo(methodOn(PaymentRestController.class)
              .getPayment(entity.getId()))
              .withSelfRel()
      );
    }
  }

}
