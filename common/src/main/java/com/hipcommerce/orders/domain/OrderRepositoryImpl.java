package com.hipcommerce.orders.domain;

import static com.google.common.collect.Lists.newArrayList;
import static com.hipcommerce.orders.domain.QOrder.order;
import static com.hipcommerce.orders.domain.QOrderItem.orderItem;
import static com.hipcommerce.payments.domain.QPayment.payment;
import static com.querydsl.core.types.Projections.fields;
import static org.springframework.util.ObjectUtils.isEmpty;

import com.hipcommerce.common.jpa.support.Querydsl4RepositorySupport;
import com.hipcommerce.orders.domain.Order.PayMethod;
import com.hipcommerce.orders.domain.OrderItem.Status;
import com.hipcommerce.orders.dto.OrderDto.DetailedSearchCondition;
import com.hipcommerce.orders.dto.OrderSearchDto;
import com.hipcommerce.orders.dto.RetrieveOrderPaymentDto;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DatePath;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;

public class OrderRepositoryImpl extends Querydsl4RepositorySupport implements
    OrderRepositoryCustom {

  public OrderRepositoryImpl() {
    super(Order.class);
  }

  @Override
  public Optional<RetrieveOrderPaymentDto> fetchById(final Long orderId) {
    RetrieveOrderPaymentDto retrieveOrderPaymentDto = select(fields(RetrieveOrderPaymentDto.class,
        ExpressionUtils.as(order, "order"),
        ExpressionUtils.as(payment, "payment")
    ))
        .from(order)
        .leftJoin(payment).on(payment.impUid.eq(order.code))
        .where(
            order.id.eq(orderId)
        )
        .fetchOne();

    return Optional.ofNullable(retrieveOrderPaymentDto);
  }

  @Override
  public Optional<OrderItem> findByIdAndOrderItemId(
      final Long id,
      final Long orderItemId
  ) {
    OrderItem foundOrderItem = getQueryFactory()
        .selectFrom(orderItem)
        .join(order).on(order.id.eq(orderItem.order.id))
        .where(
            orderIdEq(id),
            orderItemEq(orderItemId)
        )
        .fetchOne();

    return Optional.ofNullable(foundOrderItem);
  }

  @Override
  public Page<OrderSearchDto> retrieveOrder(
      DetailedSearchCondition searchCondition,
      Pageable pageable
  ) {
    return applyPagination(pageable, contentQuery -> contentQuery
        .select(fields(OrderSearchDto.class,
            ExpressionUtils.as(order, "order"),
            ExpressionUtils.as(payment, "payment")
        ))
        .from(order)
        .leftJoin(payment).on(payment.impUid.eq(order.code))
        .where(
            statusEq(searchCondition.getStatus()),
            orderCodeEq(searchCondition.getOrderCode()),
            orderItemSearch(newArrayList(
                orderItemProductNameContains(searchCondition.getProductName()),
                orderItemProductCodeEq(searchCondition.getProductCode())
            )),
            recipientNameEq(searchCondition.getRecipientName()),
            ordererEmailEq(searchCondition.getOrdererEmail()),
            ordererNameEq(searchCondition.getOrdererName()),
            ordererMobilePhoneEq(searchCondition.getOrdererMobilePhone()),
            payMethodEq(searchCondition.getPayMethod()),
            periodContains(
                searchCondition.getPeriodType(),
                searchCondition.getFromDate(),
                searchCondition.getToDate()
            )
        )
    );
  }

  private BooleanExpression periodContains(
      final String periodType,
      final LocalDate fromDate,
      final LocalDate toDate
  ) {
    if (isEmpty(periodType) || (isEmpty(fromDate) && isEmpty(toDate))) {
      return null;
    }

    DatePath<LocalDateTime> periodPath = Expressions.datePath(LocalDateTime.class, order,
        periodType);
    if (!isEmpty(fromDate) && !isEmpty(toDate)) {
      return periodPath.between(
          LocalDateTime.of(fromDate, LocalTime.MIN),
          LocalDateTime.of(toDate, LocalTime.MAX)
      );
    }

    if (isEmpty(fromDate)) {
      return periodPath.loe(LocalDateTime.of(toDate, LocalTime.MAX));
    }

    return periodPath.goe(LocalDateTime.of(fromDate, LocalTime.MIN));
  }

  private BooleanExpression payMethodEq(PayMethod payMethod) {
    return isEmpty(payMethod) ? null : order.payMethod.eq(payMethod);
  }

  private BooleanExpression ordererMobilePhoneEq(final String ordererMobilePhone) {
    return isEmpty(ordererMobilePhone) ? null : order.orderer.mobilePhone.eq(ordererMobilePhone);
  }

  private BooleanExpression ordererNameEq(final String ordererName) {
    return isEmpty(ordererName) ? null : order.orderer.name.eq(ordererName);
  }

  private BooleanExpression ordererEmailEq(final String ordererEmail) {
    return isEmpty(ordererEmail) ? null : order.orderer.email.eq(ordererEmail);
  }

  private BooleanExpression recipientNameEq(final String recipientName) {
    return isEmpty(recipientName) ? null : order.deliveryInfo.recipient.name.eq(recipientName);
  }

  private BooleanExpression orderItemSearch(List<BooleanExpression> conditions) {
    if (CollectionUtils.isEmpty(conditions) || conditions.stream().noneMatch(Objects::nonNull)) {
      return null;
    }

    return order.id.in(
        JPAExpressions
            .select(orderItem.order.id)
            .from(orderItem)
            .where(
                conditions.toArray(new BooleanExpression[conditions.size()])
            )
    );
  }

  private BooleanExpression orderItemProductNameContains(final String productName) {
    return isEmpty(productName) ? null : orderItem.name.contains(productName);
  }

  private BooleanExpression orderItemProductCodeEq(final String productCode) {
    return isEmpty(productCode) ? null : orderItem.productCode.eq(productCode);
  }

  private BooleanExpression orderCodeEq(final String orderCode) {
    return isEmpty(orderCode) ? null : order.code.eq(orderCode);
  }

  private BooleanExpression statusEq(Status status) {
    return isEmpty(status) ? null : orderItem.status.eq(status);
  }

  private BooleanExpression orderIdEq(final Long id) {
    return order.id.eq(id);
  }

  private BooleanExpression orderItemEq(final Long orderItemId) {
    return orderItem.id.eq(orderItemId);
  }
}
