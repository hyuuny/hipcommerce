package com.hipcommerce.payments.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PaymentDto {

  @NoArgsConstructor
  @AllArgsConstructor
  @Getter
  public static class RequestBody {

    @JsonProperty("imp_uid")
    private String impUid;

    @JsonProperty("merchant_uid")
    private String merchantUid;

  }


}
