package com.hipcommerce.product.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.hipcommerce.common.money.domain.Money;
import com.hipcommerce.product.domain.ProductOption;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.server.core.Relation;

public class ProductOptionDto {

  @Getter
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @Builder
  @Schema(name = "ProductOptionDto.Create", description = "상품 옵션 등록")
  public static class Create {

    @NotNull
    @Schema(description = "옵션명", example = "카고바지(블랙)", required = true)
    private String name;

    @Schema(description = "추가금액", example = "2000", required = false)
    private Long price;

    @Schema(description = "재고수량", example = "13", required = false)
    private int stockQuantity;

    @Schema(description = "옵션 이미지", example = "https://hipcommerce-bucket.s3.ap-northeast-2.amazonaws.com/data/image_1596187406745_1000.jpg", required = false)
    private String imageUrl;

    public ProductOption toEntity() {
      return ProductOption.builder()
          .name(this.name)
          .price(Money.wons(this.price))
          .stockQuantity(this.stockQuantity)
          .imageUrl(this.imageUrl)
          .build();
    }

  }

  @Setter
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Relation(collectionRelation = "productsOptions")
  @JsonInclude(Include.NON_EMPTY)
  @Schema(name = "ProductOptionDto.Response", description = "상품 옵션")
  public static class Response {

    @Schema(description = "아이디", example = "1", required = true)
    private Long id;

    @Schema(description = "옵션명", example = "1", required = true)
    private String name;

    @Schema(description = "추가금액", example = "2000", required = false)
    private Long price;

    @Schema(description = "재고수량", example = "13", required = false)
    private int stockQuantity;

    @Schema(description = "옵션 이미지", example = "https://hipcommerce-bucket.s3.ap-northeast-2.amazonaws.com/data/image_1596187406745_1000.jpg", required = false)
    private String imageUrl;

    @Schema(description = "등록일", example = "2022-01-11T13:16:32.139065", required = false)
    private LocalDateTime createdDate;

    @Schema(description = "수정일", example = "2022-01-12T15:42:06.139065", required = false)
    private LocalDateTime lastModifiedDate;

    public Response(ProductOption entity) {
      this.id = entity.getId();
      this.name = entity.getName();
      this.price = entity.getPrice().longValue();
      this.stockQuantity = entity.getStockQuantity();
      this.imageUrl = entity.getImageUrl();
      this.createdDate = entity.getCreatedTime();
      this.lastModifiedDate = entity.getLastModifiedDate();
    }

  }

}
