package com.hipcommerce.product.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.hipcommerce.common.money.domain.Money;
import com.hipcommerce.product.domain.ProductOption;
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
  public static class Create {

    @NotNull
    private String name;

    private Long price;

    private int stockQuantity;

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
  public static class Response {

    private Long id;

    private String name;

    private Long price;

    private int stockQuantity;

    private String imageUrl;

    private LocalDateTime createdDate;

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
