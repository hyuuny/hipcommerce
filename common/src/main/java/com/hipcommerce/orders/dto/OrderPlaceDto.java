package com.hipcommerce.orders.dto;

import com.hipcommerce.orders.domain.DeliveryInfo;
import com.hipcommerce.orders.domain.Order.PayMethod;
import com.hipcommerce.orders.domain.Orderer;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "주문요청")
public class OrderPlaceDto {

  @Schema(description = "유저 ID", example = "1", required = true)
  private Long userId;

  @NotNull
  @Schema(description = "주문서 ID", required = true)
  private Long orderSheetId;

  @NotNull
  @Schema(description = "배송지 정보", required = true)
  private DeliveryInfo deliveryInfo;

  @NotNull
  @Schema(description = "주문자 정보", required = true)
  private Orderer orderer;

  @NotNull
  @Schema(description = "결제수단", example = "CARD", required = true)
  private PayMethod payMethod;

}
