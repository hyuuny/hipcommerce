package com.hipcommerce.orders.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.hipcommerce.orders.domain.OrderSheet;
import com.hipcommerce.orders.domain.OrderSheetItem;
import com.hipcommerce.orders.domain.OrderSummary;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class OrderSheetDto {

  @Getter
  @NoArgsConstructor
  @Schema(name = "OrderSheetDto.OrderSheetResult", description = "주문서")
  @JsonInclude(Include.NON_EMPTY)
  public static class OrderSheetResult {

    @Schema(description = "주문서 ID", required = true)
    private Long id;

    @Schema(description = "주문서 코드", required = true)
    private String code;

    @Schema(description = "유저 ID", required = true)
    private Long userId;

    @Schema(description = "생성일시", required = true)
    private LocalDateTime createdDate;

    @Schema(description = "주문상품목록", required = true)
    private List<OrderSheetItemDto> orderSheetItems;

    @Schema(description = "주문 금액 요약 정보", required = true)
    private OrderSummaryDto.Response orderSummary;

    public OrderSheetResult(OrderSheet entity, OrderSummary orderSummary) {
      this.id = entity.getId();
      this.code = entity.getCode();
      this.userId = entity.getUserId();
      this.createdDate = entity.getCreatedDate();
      this.orderSheetItems = entity.getOrderSheetItems().stream()
          .map(OrderSheetItemDto::new)
          .collect(Collectors.toList());
      this.orderSummary = new OrderSummaryDto.Response(orderSummary);
    }

  }

  @Getter
  @NoArgsConstructor
  @Schema(description = "주문서 주문상품")
  @JsonInclude(Include.NON_NULL)
  public static class OrderSheetItemDto {

    @Schema(description = "주문서 ID", required = true)
    private Long id;

    @Schema(description = "상품코드", example = "20220109140753969", required = true)
    private String productCode;

    @Schema(description = "상품명", example = "카고바지", required = true)
    private String name;

    @Schema(description = "브랜드", example = "플랙진", required = true)
    private String brand;

    @Schema(description = "상품 썸네일 url", example = "https://hipcommerce-bucket.s3.ap-northeast-2.amazonaws.com/data/image_1596187406745_1000.jpg", required = false)
    private String imageUrl;

    @Schema(description = "상품옵션명", example = "블랙", required = false)
    private String optionName;

    @Schema(description = "상품가격", example = "39900", required = true)
    private long price;

    @Schema(description = "상품옵션가격", example = "0", required = true)
    private long optionPrice;

    @Schema(description = "할인가격", example = "17900", required = true)
    private long discountPrice;

    @Schema(description = "수량", example = "5", required = true)
    private long quantity;

    @Schema(description = "상품금액 합계", example = "5", required = true)
    private long calculatedPrice;

    @Schema(description = "상품할인금액 합계", example = "5", required = true)
    private long calculatedDiscountPrice;

    @Schema(description = "상품주문금액 합계", example = "5", required = true)
    private long calculatedOrderPrice;

    @Schema(description = "생성일시", required = true)
    private LocalDateTime createdAt;

    public OrderSheetItemDto(OrderSheetItem entity) {
      this.id = entity.getId();
      this.productCode = entity.getProductCode();
      this.name = entity.getName();
      this.brand = entity.getBrand();
      this.imageUrl = entity.getThumbnail();
      this.optionName = entity.getOptionName();
      this.price = entity.getPrice().longValue();
      this.optionPrice = entity.getOptionPrice().longValue();
      this.discountPrice = entity.getDiscountPrice().longValue();
      this.quantity = entity.getQuantity();
      this.calculatedPrice = entity.calculatePrice().longValue();
      this.calculatedDiscountPrice = entity.calculateDiscountPrice().longValue();
      this.calculatedOrderPrice = entity.calculateOrderPrice().longValue();
      this.createdAt = entity.getCreatedDate();
    }
  }

}
