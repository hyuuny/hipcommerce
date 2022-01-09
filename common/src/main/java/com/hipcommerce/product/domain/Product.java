package com.hipcommerce.product.domain;

import static com.hipcommerce.product.domain.Product.Status.ON_SALE;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;
import static org.springframework.util.ObjectUtils.isEmpty;

import com.google.common.collect.Lists;
import com.hipcommerce.common.generators.UniqueIdGenerator;
import com.hipcommerce.common.jpa.domain.BaseEntity;
import com.hipcommerce.common.money.domain.Money;
import com.hipcommerce.common.money.infra.MoneyConverter;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder.Default;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Entity
public class Product extends BaseEntity {

  @Getter
  @RequiredArgsConstructor
  public enum Status {
    ON_SALE("판매중"),
    SOLD_OUT("품절"),
    STOP_SELLING("판매중지"),
    DELETED("삭제");

    private final String title;
  }

  @Column(nullable = false)
  private Long categoryId;

  @Default
  @Column(nullable = false, unique = true, updatable = false)
  private String code = UniqueIdGenerator.nextProductCode();

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String brand;

  @Default
  @Convert(converter = MoneyConverter.class)
  private Money price = Money.ZERO;

  @Default
  @Convert(converter = MoneyConverter.class)
  private Money discountPrice = Money.ZERO;

  private int priorityNumber;

  private String tag;

  @Default
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Status status = ON_SALE;

  private String thumbnail;

  private long hits;

  @Default
  @OneToMany(mappedBy = "product", cascade = ALL, fetch = LAZY, orphanRemoval = true)
  private List<ProductOption> options = Lists.newArrayList();

  public void addOption(ProductOption option) {
    option.setProduct(this);
  }

  public void changeCategoryId(final Long categoryId) {
    if (!isEmpty(categoryId)) {
      this.categoryId = categoryId;
    }
  }

  public void changeName(final String name) {
    this.name = name;
  }

  public void changeBrand(final String brand) {
    this.brand = brand;
  }

  public void changePrice(final Long price) {
    this.price = Money.wons(price);
  }

  public void changeDiscountPrice(final Long discountPrice) {
    this.discountPrice = Money.wons(discountPrice);
  }

  public void changePriorityNumber(final int priorityNumber) {
    this.priorityNumber = priorityNumber;
  }

  public void changeTag(final String tag) {
    this.tag = tag;
  }

  public void changeStatus(final Status status) {
    this.status = status;
  }

  public void changeThumbnail(final String thumbnail) {
    this.thumbnail = thumbnail;
  }

  public Long toPrice() {
    return this.price.longValue();
  }

  public Long getCalculatedPrice() {
    return calculatePrice().longValue();
  }

  public Long toDiscountPrice() {
    return this.discountPrice.longValue();
  }

  public Money calculatePrice() {
    return this.price.minus(this.discountPrice);
  }

  public void addHits() {
    this.hits += 1;
  }

  public void delete() {
    this.status = Status.DELETED;
  }


}
