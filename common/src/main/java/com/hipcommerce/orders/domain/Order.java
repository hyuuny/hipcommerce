package com.hipcommerce.orders.domain;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;

import com.google.common.collect.Lists;
import com.hipcommerce.common.generators.UniqueIdGenerator;
import com.hipcommerce.common.jpa.domain.BaseEntity;
import com.hipcommerce.common.money.domain.Money;
import com.hipcommerce.common.money.infra.MoneyConverter;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Table;
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
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Table(name = "orders")
@Entity
public class Order extends BaseEntity {

  @Getter
  @RequiredArgsConstructor
  public enum PayMethod {
    CARD("신용카드"),
    TRANS("실시간계좌이체"),
    VBANK("가상계좌"),
    PHONE("휴대폰소액결제"),
    KAKAOPAY("카카오페이"),
    PAYCO("페이코"),
    LPAY("롯데페이"),
    SSGPAY("신세계페이"),
    TOSSPAY("토스페이"),
    POINT("포인트결제");

    private final String title;
  }


  @Default
  @Column(unique = true, nullable = false, updatable = false)
  private String code = UniqueIdGenerator.nextOrderCode();

  @Column(nullable = false)
  private Long userId;

  @Embedded
  private DeliveryInfo deliveryInfo;

  @Embedded
  private Orderer orderer;

  private Long orderSheetId;

  @Convert(converter = MoneyConverter.class)
  private Money totalPrice;

  @Enumerated(EnumType.STRING)
  private PayMethod payMethod;

  @Default
  @OneToMany(mappedBy = "order", cascade = ALL, fetch = LAZY, orphanRemoval = true)
  private List<OrderItem> orderItems = Lists.newArrayList();

  public void addOrderItem(OrderItem orderItem) {
    orderItem.setOrder(this);
  }

  public Money calculateTotalProductPrice() {
    return Money.sum(this.orderItems, OrderItem::calculatePrice);
  }

  public Money calculateTotalDiscountPrice() {
    return Money.sum(this.orderItems, OrderItem::calculateDiscountPrice);
  }

  public Money calculateTotalPrice() {
    return Money.sum(this.orderItems, OrderItem::calculateTotalPrice);
  }

  public void place(PayMethod payMethod) {
    this.payMethod = payMethod;
    this.orderItems.stream()
        .forEach(OrderItem::paid);
  }



}
