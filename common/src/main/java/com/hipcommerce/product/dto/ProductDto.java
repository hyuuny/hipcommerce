package com.hipcommerce.product.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.common.collect.Lists;
import com.hipcommerce.common.money.domain.Money;
import com.hipcommerce.product.domain.Product;
import com.hipcommerce.product.domain.Product.Status;
import io.swagger.v3.oas.annotations.media.Schema;
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
  @Schema(name = "ProductDto.Create", description = "상품 등록")
  public static class Create {

    @NotNull
    @Schema(description = "카테고리 ID", example = "1", required = true)
    private Long categoryId;

    @NotNull
    @Schema(description = "상품명", example = "카고바지", required = true)
    private String name;

    @NotNull
    @Schema(description = "브랜드", example = "플랙진", required = true)
    private String brand;

    @NotNull
    @Schema(description = "상품가격", example = "36000", required = true)
    private Long price;

    @Default
    @Schema(description = "할인가격", example = "5000", required = false)
    private Long discountPrice = Money.ZERO.longValue();

    @Schema(description = "우선순위", example = "1", required = false)
    private int priorityNumber;

    @NotNull
    @Schema(description = "태그", example = "#힙한|#봄가을용|#인기템", required = true)
    private String tag;

    @NotNull
    @Schema(description = "상품상태", example = "ON_SALE", required = true)
    private Status status;

    @Schema(description = "상품 썸네일 url", example = "https://hipcommerce-bucket.s3.ap-northeast-2.amazonaws.com/data/image_1596187406745_1000.jpg", required = false)
    private String thumbnail;

    @Default
    @Schema(description = "상품옵션 리스트", required = false)
    private List<ProductOptionDto.Create> options = Lists.newArrayList();

    public Product toEntity() {
      Product product = Product.builder()
          .categoryId(this.categoryId)
          .name(this.name)
          .brand(this.brand)
          .price(Money.wons(this.price))
          .discountPrice(Money.wons(this.discountPrice))
          .priorityNumber(this.priorityNumber)
          .tag(this.tag)
          .status(this.status)
          .thumbnail(this.thumbnail)
          .build();

      options.stream()
          .map(ProductOptionDto.Create::toEntity)
          .forEach(product::addOption);

      return product;
    }
  }

  @Getter
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @Builder
  @Schema(name = "ProductDto.Update", description = "상품 수정")
  public static class Update {

    @NotNull
    @Schema(description = "카테고리 ID", example = "1", required = true)
    private Long categoryId;

    @NotNull
    @Schema(description = "상품명", example = "카고바지", required = true)
    private String name;

    @NotNull
    @Schema(description = "브랜드", example = "플랙진", required = true)
    private String brand;

    @NotNull
    @Schema(description = "상품가격", example = "36000", required = true)
    private Long price;

    @Default
    @Schema(description = "할인가격", example = "5000", required = false)
    private Long discountPrice = Money.ZERO.longValue();

    @Schema(description = "우선순위", example = "1", required = false)
    private int priorityNumber;

    @NotNull
    @Schema(description = "태그", example = "#힙한|#봄가을용|#인기템", required = true)
    private String tag;

    @NotNull
    @Schema(description = "상품상태", example = "ON_SALE", required = true)
    private Status status;

    @Schema(description = "상품 썸네일 url", example = "https://hipcommerce-bucket.s3.ap-northeast-2.amazonaws.com/data/image_1596187406745_1000.jpg", required = false)
    private String thumbnail;

    @Default
    @Schema(description = "상품옵션 리스트", required = false)
    private List<ProductOptionDto.Create> options = Lists.newArrayList();

    public void update(Product entity) {
      entity.changeCategoryId(this.categoryId);
      entity.changeName(this.name);
      entity.changeBrand(this.brand);
      entity.changePrice(this.price);
      entity.changeDiscountPrice(this.discountPrice);
      entity.changePriorityNumber(this.priorityNumber);
      entity.changeTag(this.tag);
      entity.changeStatus(this.status);
      entity.changeThumbnail(this.thumbnail);

      options.clear();
      options.stream()
          .map(ProductOptionDto.Create::toEntity)
          .forEach(entity::addOption);
    }
  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Relation(collectionRelation = "products")
  @JsonInclude(Include.NON_EMPTY)
  @Schema(name = "ProductDto.Response", description = "상품")
  public static class Response {

    @Schema(description = "아이디", example = "1", required = true)
    private Long id;

    @Schema(description = "카테고리 ID", example = "1", required = true)
    private Long categoryId;

    @Schema(description = "상품 코드", example = "P20220109140753969", required = true)
    private String code;

    @Schema(description = "상품명", example = "카고바지", required = true)
    private String name;

    @Schema(description = "브랜드", example = "플랙진", required = true)
    private String brand;

    @Schema(description = "상품가격", example = "36000", required = true)
    private Long price;

    @Schema(description = "할인가격", example = "5000", required = false)
    private Long discountPrice;

    @Schema(description = "할인 적용된 가격(삼품가격-할인가격)", example = "31000", required = true)
    private Long calculatedPrice;

    @Schema(description = "우선순위", example = "36000", required = false)
    private int priorityNumber;

    @Schema(description = "태그", example = "#힙한|#봄가을용|#인기템", required = true)
    private String tag;

    @Schema(description = "상품상태", example = "ON_SALE", required = true)
    private Status status;

    @Schema(description = "상품 썸네일 url", example = "https://hipcommerce-bucket.s3.ap-northeast-2.amazonaws.com/data/image_1596187406745_1000.jpg", required = false)
    private String thumbnail;

    @Schema(description = "상품옵션 리스트", required = false)
    private List<ProductOptionDto.Response> options;

    @Schema(description = "등록일", example = "2022-01-11T13:16:32.139065", required = false)
    private LocalDateTime createdDate;

    @Schema(description = "수정일", example = "2022-01-12T15:42:06.139065", required = false)
    private LocalDateTime lastModifiedDate;

    public Response(Product entity) {
      this.id = entity.getId();
      this.categoryId = entity.getCategoryId();
      this.name = entity.getName();
      this.brand = entity.getBrand();
      this.price = entity.toPrice();
      this.discountPrice = entity.toDiscountPrice();
      this.calculatedPrice = entity.getCalculatedPrice();
      this.priorityNumber = entity.getPriorityNumber();
      this.tag = entity.getTag();
      this.status = entity.getStatus();
      this.thumbnail = entity.getThumbnail();
      this.options = entity.getOptions().stream()
          .map(ProductOptionDto.Response::new)
          .collect(Collectors.toList());
      this.createdDate = entity.getCreatedDate();
      this.lastModifiedDate = entity.getLastModifiedDate();
    }
  }

  @Setter
  @Getter
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @Builder
  @Schema(name = "ProductDto.DetailedSearchCondition", description = "상품 상세검색조건")
  public static class DetailedSearchCondition {

    @Schema(description = "검색옵션", example = "productName", required = false)
    private String searchOption;

    @Schema(description = "검색어", example = "카고바지", required = false)
    private String keyword;

    @Schema(description = "카테고리 ID", example = "1", required = false)
    private Long categoryId;

    @Schema(description = "상태", example = "ON_SALE", required = false)
    private Status status;

  }

  @Setter
  @Getter
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @Builder
  @Schema(name = "ProductDto.SearchCondition", description = "상품 검색조건")
  public static class SearchCondition {

    @Schema(description = "검색옵션", example = "productName", required = false)
    private String searchOption;

    @Schema(description = "검색어", example = "카고바지", required = false)
    private String keyword;

    @Schema(description = "카테고리 ID", example = "1", required = false)
    private Long categoryId;

  }


}
