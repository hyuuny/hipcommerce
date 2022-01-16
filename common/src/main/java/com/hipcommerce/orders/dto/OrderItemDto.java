package com.hipcommerce.orders.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.hipcommerce.orders.domain.OrderItem;
import com.hipcommerce.orders.domain.OrderItem.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.server.core.Relation;

public class OrderItemDto {

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(name = "OrderItemDto.Response", description = "주문 상품")
  @Relation(collectionRelation = "orderItems")
  @JsonInclude(Include.NON_EMPTY)
  public static class Response {

    @Schema(description = "주문상품 ID", required = true)
    private Long id;

    @Schema(description = "주문상품 상태", example = "PAID", required = true)
    private Status status;

    @Schema(description = "주문상품 코드", required = true)
    private String code;

    @Schema(description = "상품 ID", required = true, example = "1")
    private Long productId;

    @Schema(description = "상품코드", example = "P20220109140753969", required = true)
    private String productCode;

    @Schema(description = "상품명", example = "카고바지", required = true)
    private String name;

    @Schema(description = "브랜드", example = "유한킴벌리", required = false)
    private String brand;

    @Schema(description = "대표이미지(이미지 URL)", example = "https://hipcommerce-bucket.s3.ap-northeast-2.amazonaws.com/data/image_1596187406745_1000.jpg", required = false)
    private String thumbnail;

    @Schema(description = "상품옵션명", example = "베이지", required = false)
    private String optionName;

    @Schema(description = "상품가격", example = "39900", required = true)
    private long price;

    @Schema(description = "상품옵션가격", example = "0", required = true)
    private long optionPrice;

    @Schema(description = "할인가격", example = "17900", required = true)
    private long discountPrice;

    @Schema(description = "수량", example = "13", required = true)
    private long quantity;

    @Schema(description = "상품가격 합계", example = "105000", required = true)
    private long calculatedPrice;

    @Schema(description = "상품할인가격 합계", example = "25000", required = true)
    private long calculatedDiscountPrice;

    @Schema(description = "상품주문가격 합계", example = "80000", required = true)
    private long calculatedTotalPrice;

    @Schema(description = "생성일시", required = true)
    private LocalDateTime createdDate;

    public Response(OrderItem entity) {
      this.id = entity.getId();
      this.status = entity.getStatus();
      this.code = entity.getCode();
      this.productId = entity.getProductId();
      this.productCode = entity.getProductCode();
      this.name = entity.getName();
      this.brand = entity.getBrand();
      this.thumbnail = entity.getThumbnail();
      this.optionName = entity.getOptionName();
      this.price = entity.getPrice().longValue();
      this.optionPrice = entity.getOptionPrice().longValue();
      this.discountPrice = entity.getDiscountPrice().longValue();
      this.quantity = entity.getQuantity();
      this.calculatedTotalPrice = entity.calculateTotalPrice().longValue();
      this.createdDate = entity.getCreatedDate();
    }
  }

}
