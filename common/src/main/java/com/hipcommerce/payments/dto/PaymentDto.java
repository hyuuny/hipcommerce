package com.hipcommerce.payments.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hipcommerce.common.money.domain.Money;
import com.hipcommerce.payments.domain.Payment;
import com.hipcommerce.payments.domain.Payment.PayMethod;
import com.hipcommerce.payments.domain.Payment.Status;
import com.hipcommerce.product.dto.ProductOptionDto;
import com.hipcommerce.product.dto.ProductOptionDto.Response;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.server.core.Relation;

public class PaymentDto {

  @Getter
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @Builder
  @Schema(name = "PaymentDto.PayResult", description = "아임포트 결제요청 결과")
  public static class PayResult {

    @NotNull
    @JsonProperty("imp_uid")
    @Schema(description = "아임포트 고유 결제번호", example = "imp_115453010835", required = true)
    private String impUid;

    @NotNull
    @JsonProperty("merchant_uid")
    @Schema(description = "주문코드", example = "OI201801310000050", required = true)
    private String orderCode;

    @NotNull
    @JsonProperty("pay_method")
    @Schema(description = "결제수단", example = "CARD", required = true)
    private String payMethod;

    @NotNull
    @JsonProperty("paid_amount")
    @Schema(description = "결제금액", example = "50000", required = true)
    private Long paidAmount;

    @NotNull
    @JsonProperty("status")
    @Schema(description = "결제상태", example = "PAID", required = true)
    private String status;

    @JsonProperty("success")
    @Schema(description = "결제 성공 여부", example = "true", required = false)
    private boolean success;

    public Payment toEntity() {
      return Payment.builder()
          .impUid(this.impUid)
          .orderCode(this.orderCode)
          .payMethod(Payment.getPaymentMethod(this.payMethod))
          .paidAmount(Money.wons(this.paidAmount))
          .status(Payment.getPaymentStatus(this.status))
          .success(this.success)
          .build();
    }

  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Relation(collectionRelation = "payments")
  @JsonInclude(Include.NON_EMPTY)
  @Schema(name = "PaymentDto.Response", description = "결제 정보")
  public static class Response {

    @Schema(description = "아이디", example = "1", required = true)
    private Long id;

    @Schema(description = "아임포트 고유 결제번호", example = "imp_115453010835", required = true)
    private String impUid;

    @Schema(description = "주문코드", example = "OI201801310000050", required = true)
    private String orderCode;

    @Schema(description = "결제수단", example = "CARD", required = true)
    private PayMethod payMethod;

    @Schema(description = "결제금액", example = "50000", required = true)
    private Long paidAmount;

    @Schema(description = "결제상태", example = "PAID", required = true)
    private Status status;

    @Schema(description = "결제 성공 여부", example = "true", required = false)
    private boolean success;

    @Schema(description = "등록일", example = "2022-01-11T13:16:32.139065", required = false)
    private LocalDateTime createdDate;

    @Schema(description = "수정일", example = "2022-01-12T15:42:06.139065", required = false)
    private LocalDateTime lastModifiedDate;

    public Response(Payment entity) {
      this.id = entity.getId();
      this.impUid = entity.getImpUid();
      this.orderCode = entity.getOrderCode();
      this.payMethod = entity.getPayMethod();
      this.paidAmount = entity.toPaidAmount();
      this.status = entity.getStatus();
      this.success = entity.isSuccess();
      this.createdDate = entity.getCreatedDate();
      this.lastModifiedDate = entity.getLastModifiedDate();
    }

  }


}
