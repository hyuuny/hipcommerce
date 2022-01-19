package com.hipcommerce.orders.dto;

import static org.springframework.util.ObjectUtils.isEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.common.collect.Lists;
import com.hipcommerce.orders.domain.DeliveryInfo;
import com.hipcommerce.orders.domain.Order;
import com.hipcommerce.orders.domain.OrderItem;
import com.hipcommerce.orders.domain.Orderer;
import com.hipcommerce.payments.domain.Payment;
import com.hipcommerce.payments.domain.Payment.PayMethod;
import com.hipcommerce.payments.domain.Payment.Status;
import io.swagger.v3.oas.annotations.Hidden;
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

public class OrderDto {

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(name = "OrderDto.Response", description = "주문")
  @Relation(collectionRelation = "orders")
  @JsonInclude(Include.NON_EMPTY)
  public static class Response {

    @Schema(description = "주문 ID", example = "1", required = true)
    private Long id;

    @Schema(description = "주문 코드", example = "O201801310000050", required = true)
    private String orderCode;

    @Schema(description = "유저 ID", example = "1", required = false)
    private Long userId;

    @Schema(description = "배송지 정보", required = true)
    private DeliveryInfo deliveryInfo;

    @Schema(description = "주문자 정보", required = true)
    private Orderer orderer;

    @Schema(description = "결제수단코드", example = "CARD", required = true)
    private Order.PayMethod payMethod;

    @Schema(description = "주문금액요약", required = true)
    private OrderSummaryDto.Response orderSummary;

    @Schema(description = "주문 상품", required = true)
    private List<OrderItemDto.Response> orderItems;

    @Schema(description = "결제정보", required = false)
    private PaymentResponse payment;

    public Response(RetrieveOrderPaymentDto entity) {
      this.id = entity.getId();
      this.orderCode = entity.getOrderCode();
      this.userId = entity.getUserId();
      this.deliveryInfo = entity.getDeliveryInfo();
      this.orderer = entity.getOrderer();
      this.payMethod = entity.getPayMethod();
      this.orderSummary = new OrderSummaryDto.Response(entity.getOrderSummary());
      this.orderItems = entity.getOrderItems().stream()
          .map(OrderItemDto.Response::new)
          .collect(Collectors.toList());
      this.payment = isEmpty(entity.getPayment()) ? null : new PaymentResponse(entity.getPayment());
    }

  }

  @Setter
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(description = "주문 결제 정보")
  @JsonInclude(Include.NON_EMPTY)
  public static class PaymentResponse {

    @Schema(description = "아이디", example = "1", required = true)
    private Long id;

    @Schema(description = "아임포트 고유 결제번호", example = "imp_115453010835", required = true)
    private String impUid;

    @Schema(description = "주문코드", example = "O201801310000050", required = true)
    private String orderCode;

    @Schema(description = "결제수단", example = "CARD", required = true)
    private PayMethod payMethod;

    @Schema(description = "결제금액", example = "50000", required = true)
    private Long paidAmount;

    @Schema(description = "결제상태", example = "PAID", required = true)
    private Status status;

    @Schema(description = "결제 성공 여부", example = "true", required = false)
    private boolean success;

    @Schema(description = "등록일", example = "2022-01-11T13:16:32.139065", required = false)
    private LocalDateTime createdDate;

    @Schema(description = "수정일", example = "2022-01-12T15:42:06.139065", required = false)
    private LocalDateTime lastModifiedDate;

    public PaymentResponse(Payment entity) {
      this.id = entity.getId();
      this.impUid = entity.getImpUid();
      this.orderCode = entity.getOrderCode();
      this.payMethod = entity.getPayMethod();
      this.paidAmount = entity.toPaidAmount();
      this.status = entity.getStatus();
      this.success = entity.isSuccess();
      this.createdDate = entity.getCreatedDate();
      this.lastModifiedDate = entity.getLastModifiedDate();
    }

  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(name = "OrderDto.OrderResult", description = "주문 상세 정보")
  @JsonInclude(Include.NON_EMPTY)
  public static class OrderResult {

    @Schema(description = "주문정보", required = true)
    private OrderDto.Response order;

    @Schema(description = "결제정보", required = false)
    private PaymentResponse payment;

    public OrderResult(RetrieveOrderPaymentDto entity) {
      this(entity, null);
    }

    public OrderResult(RetrieveOrderPaymentDto entity, Payment payment) {
      this.order = new OrderDto.Response(entity);
      this.payment = isEmpty(payment) ? null : new PaymentResponse(payment);
    }

    @Hidden
    @JsonIgnore
    public Long getId() {
      return order.getId();
    }

    @Hidden
    @JsonIgnore
    public Long getUserId() {
      return order.getUserId();
    }

    @Hidden
    @JsonIgnore
    public List<OrderItemDto.Response> getOrderItems() {
      return order.getOrderItems();
    }

  }

  @Getter
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @Builder
  @Schema(name = "OrderDto.changeOrderItemsStatus", description = "주문 상품 상태 변경 목록")
  public static class ChangeOrderItemsStatus {

    @Default
    @Schema(description = "주문 상품 상태 변경 목록", required = true)
    List<ChangeOrderItemStatus> changeOrderItemStatuses = Lists.newArrayList();

  }

  @Getter
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @Builder
  @Schema(name = "OrderDto.changeOrderItemStatus", description = "주문 상품 상태 변경")
  public static class ChangeOrderItemStatus {

    @Schema(description = "아이디", example = "1", required = true)
    private Long id;

    @Schema(description = "주문 상품 ID", example = "1", required = true)
    private Long orderItemId;

    @Schema(description = "상태", example = "WAITING_DELIVERY", required = false)
    private OrderItem.Status status;

  }

  @Getter
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @Builder
  @Schema(name = "OrderDto.ChangeDeliveryInfo", description = "배송지 정보 변경")
  public static class ChangeDeliveryInfo {

    @NotNull
    @Schema(description = "배송지", required = true)
    private DeliveryInfo deliveryInfo;

  }


}


