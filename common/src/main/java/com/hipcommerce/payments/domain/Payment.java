package com.hipcommerce.payments.domain;

import com.hipcommerce.common.jpa.domain.BaseEntity;
import com.hipcommerce.common.money.domain.Money;
import com.hipcommerce.common.money.infra.MoneyConverter;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Entity
public class Payment extends BaseEntity {

  @Getter
  @RequiredArgsConstructor
  public enum PayMethod {
    CARD("신용카드"),
    TRANS("실시간계좌이체"),
    VBANK("가상계좌"),
    PHONE("휴대폰소액결제"),
    KPAY("KPay 앱"),
    KAKAOPAY("카카오페이"),
    PAYCO("페이코"),
    LPAY("롯데페이"),
    SSGPAY("신세계페이"),
    TOSSPAY("토스페이"),
    POINT("포인트결제");

    private final String title;
  }

  @Getter
  @RequiredArgsConstructor
  public enum Status {
    READY("미결제"),
    PAID("결제완료"),
    FAILED("결제실패");

    private final String title;
  }

  @Enumerated(EnumType.STRING)
  private PayMethod payMethod;

  @Column(nullable = false, unique = true)
  private String impUid;

  @Column(nullable = false, unique = true)
  private String orderCode;

  @Column(nullable = false)
  @Convert(converter = MoneyConverter.class)
  protected Money paidAmount;

  private boolean success;

  @Enumerated(EnumType.STRING)
  private Status status;

  private String errorCode;

  private String errorMsg;

  public static PayMethod getPaymentMethod(String payMethod) {
    switch (payMethod) {
      case "card" : return PayMethod.CARD;
      case "trans" : return PayMethod.TRANS;
      case "vbank" : return PayMethod.VBANK;
      case "phone" : return PayMethod.PHONE;
      case "kakaopay" : return PayMethod.KAKAOPAY;
      case "payco" : return PayMethod.PAYCO;
      case "lpay" : return PayMethod.LPAY;
      case "ssgpay" : return PayMethod.SSGPAY;
      case "tosspay" : return PayMethod.TOSSPAY;
      case "point" : return PayMethod.POINT;
    }
    return null;
  }

  public static Status getPaymentStatus(String status) {
    switch (status) {
      case "ready" : return Status.READY;
      case "paid" : return Status.PAID;
      case "failed" : return Status.FAILED;
    }
    return null;
  }

  public Long toPaidAmount() {
    return this.paidAmount.longValue();
  }

}
