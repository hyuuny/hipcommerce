package com.hipcommerce.orders.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.hipcommerce.common.money.domain.Money;
import com.hipcommerce.orders.domain.OrderSummary;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class OrderSummaryDto {

  @Getter
  @NoArgsConstructor
  @Schema(name = "OrderSummaryDto.Response", description = "주문 금액 요약 정보")
  @JsonInclude(Include.NON_NULL)
  public static class Response {

    @Schema(description = "상품금액", example = "158000", required = true)
    private Money totalProductPrice;

    @Schema(description = "상품할인액", example = "47400", required = true)
    private Money totalDiscountPrice;

    @Schema(description = "총 금액", example = "110600", required = true)
    private Money totalPrice;

    public Response(OrderSummary entity) {
      this.totalProductPrice = entity.getTotalProductPrice();
      this.totalDiscountPrice = entity.getTotalDiscountPrice();
      this.totalPrice = entity.getTotalPrice();
    }

  }

}
