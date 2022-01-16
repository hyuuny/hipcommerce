package com.hipcommerce.orders.domain;

import com.hipcommerce.common.money.domain.Money;
import com.hipcommerce.common.money.infra.MoneyConverter;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.EqualsAndHashCode;
import lombok.EqualsAndHashCode.Include;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@EntityListeners(AuditingEntityListener.class)
@Entity
public class OrderSheetItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(unique = true, nullable = false)
  @Include
  private Long id;

  @Column(nullable = false)
  public Long productId;

  @Include
  @Column(nullable = false)
  public String productCode;

  @Include
  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String brand;

  @Column(nullable = false)
  private String thumbnail;

  private String optionName;

  @Default
  @Include
  @Convert(converter = MoneyConverter.class)
  private Money price = Money.ZERO;

  @Default
  @Include
  @Convert(converter = MoneyConverter.class)
  private Money optionPrice = Money.ZERO;

  @Default
  @Convert(converter = MoneyConverter.class)
  private Money discountPrice = Money.ZERO;

  private long quantity;

  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime createdDate;

  public Money calculateOrderPrice() {
    return calculatePrice().minus(calculateDiscountPrice());
  }

  public Money calculatePrice() {
    return this.price.plus(this.optionPrice).times(this.quantity);
  }

  public Money calculateDiscountPrice() {
    return this.discountPrice.times(quantity);
  }

  public OrderItem toOrderItem() {
    return OrderItem.builder()
        .productId(this.productId)
        .productCode(this.productCode)
        .thumbnail(this.thumbnail)
        .name(this.name)
        .brand(this.brand)
        .optionName(this.optionName)
        .price(this.price)
        .optionPrice(this.optionPrice)
        .discountPrice(this.discountPrice)
        .quantity(this.quantity)
        .build();
  }

}
