package com.hipcommerce.orders.dto;

import com.google.common.collect.Lists;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "주문 체크아웃")
public class OrderCheckoutDto {

  @Schema(description = "유저 ID", example = "1", required = true)
  private Long userId;

  @Schema(description = "주문 상품",required = true)
  private List<OrderCheckoutItem> orderCheckoutItems = Lists.newArrayList();

  public OrderCheckoutDto(final Long userId, List<OrderCheckoutItem> orderCheckoutItems) {
    this.userId = userId;
    this.orderCheckoutItems = orderCheckoutItems;
  }

  @Getter
  @NoArgsConstructor
  @Schema(description = "체크아웃 주문 상품")
  public static class OrderCheckoutItem {

    @NotNull
    @Schema(description = "상품 ID", example = "1", required = true)
    private Long productId;

    @NotEmpty
    @Schema(description = "옵션명", example = "블랙", required = true)
    private String optionName;

    @NotNull
    @Schema(description = "옵션가격", example = "2000", required = true)
    private Long optionPrice;

    @NotNull
    @Schema(description = "수량", example = "13", required = true)
    private Integer quantity;

    public OrderCheckoutItem(
        Long productId,
        String optionName,
        Long optionPrice,
        int quantity
    ) {
      this.productId = productId;
      this.optionName = optionName;
      this.optionPrice = optionPrice;
      this.quantity = quantity;
    }

  }

}
