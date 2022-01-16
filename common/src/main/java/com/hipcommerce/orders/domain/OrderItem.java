package com.hipcommerce.orders.domain;

import static javax.persistence.FetchType.LAZY;

import com.hipcommerce.common.generators.UniqueIdGenerator;
import com.hipcommerce.common.jpa.domain.BaseNoEqualsEntity;
import com.hipcommerce.common.money.domain.Money;
import com.hipcommerce.common.money.infra.MoneyConverter;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.EqualsAndHashCode;
import lombok.EqualsAndHashCode.Include;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Entity
public class OrderItem extends BaseNoEqualsEntity {

  @Getter
  @RequiredArgsConstructor
  public enum Status {
    ORDERED("주문"),
    WAITING_PAYMENT("결제대기"),
    PAID("결제완료"),
    WAITING_DELIVERY("배송준비"),
    DELIVERING("배송중"),
    DELIVERED("배송완료"),
    CONFIRMED("구매완료"),
    CANCEL_REQUEST("취소요청"),
    CANCEL_COMPLETE("취소완료"),
    RETURN_REQUEST("반품요청"),
    RETURN_COMPLETE("반품완료");

    private final String title;
  }

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Status status;

  @ManyToOne(optional = false, fetch = LAZY)
  private Order order;

  @Default
  @Column(unique = true, nullable = false, updatable = false)
  private String code = UniqueIdGenerator.nextOrderItemCode();

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

  @Convert(converter = MoneyConverter.class)
  private Money deliveryFee;

  private long quantity;

  private LocalDateTime deliveredDateTime;

  public void setOrder(Order order) {
    if (this.order != null) {
      order.getOrderItems().remove(this);
    }
    this.order = order;
    this.order.getOrderItems().add(this);
  }


  public void ordered() {
    this.status = Status.ORDERED;
  }

  public void waitingPayment() {
    this.status = Status.WAITING_PAYMENT;
  }

  public void paid() {
    this.status = Status.PAID;
  }

  public void waitingDelivery() {
    this.status = Status.WAITING_DELIVERY;
  }

  public void startDelivery() {
    this.status = Status.DELIVERING;
  }

  public void delivered() {
    this.status = Status.DELIVERED;
    this.deliveredDateTime = LocalDateTime.now();
  }

  public void confirmed() {
    this.status = Status.CONFIRMED;
  }

  public void cancelRequest() {
    this.status = Status.CANCEL_REQUEST;
  }

  public void cancelComplete() {
    this.status = Status.CANCEL_COMPLETE;
  }

  public void returnRequest() {
    this.status = Status.RETURN_REQUEST;
  }

  public void returnComplete() {
    this.status = Status.RETURN_COMPLETE;
  }

  public Money calculateTotalPrice() {
    return calculatePrice().minus(calculateDiscountPrice());
  }

  public Money calculatePrice() {
    return this.price.times(this.quantity);
  }

  public Money calculateDiscountPrice() {
    return this.discountPrice.times(this.quantity);
  }

}
