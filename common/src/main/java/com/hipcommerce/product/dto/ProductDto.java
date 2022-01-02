package com.hipcommerce.product.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.common.collect.Lists;
import com.hipcommerce.common.money.domain.Money;
import com.hipcommerce.product.domain.Product;
import com.hipcommerce.product.domain.Product.Status;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.server.core.Relation;

public class ProductDto {

  @Getter
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @Builder
  public static class Create {

    @NotNull
    private String name;

    @NotNull
    private Long price;

    @NotNull
    private Status status;

    private String thumbnail;

    @Default
    private List<ProductOptionDto.Create> options = Lists.newArrayList();

    public Product toEntity() {
      Product product = Product.builder()
          .name(this.name)
          .price(Money.wons(this.price))
          .status(this.status)
          .thumbnail(this.thumbnail)
          .build();

      options.stream()
          .map(ProductOptionDto.Create::toEntity)
          .forEach(product::addOption);

      return product;
    }
  }

  @Setter
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Relation(collectionRelation = "products")
  @JsonInclude(Include.NON_EMPTY)
  public static class Response {

    private Long id;

    private String name;

    private Long price;

    private Status status;

    private String thumbnail;

    private List<ProductOptionDto.Response> options;

    private LocalDateTime createdDate;

    private LocalDateTime lastModifiedDate;

    public Response(Product entity) {
      this.id = entity.getId();
      this.name = entity.getName();
      this.price = entity.getPrice().longValue();
      this.status = entity.getStatus();
      this.thumbnail = entity.getThumbnail();
      this.options = entity.getOptions().stream()
          .map(ProductOptionDto.Response::new)
          .collect(Collectors.toList());
      this.createdDate = entity.getCreatedTime();
      this.lastModifiedDate = entity.getLastModifiedTime();
    }
  }

}
