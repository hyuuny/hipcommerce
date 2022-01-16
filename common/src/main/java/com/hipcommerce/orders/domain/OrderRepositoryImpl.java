package com.hipcommerce.orders.domain;

import static com.hipcommerce.orders.domain.QOrder.order;
import static com.hipcommerce.payments.domain.QPayment.payment;
import static com.querydsl.core.types.Projections.fields;

import com.hipcommerce.common.jpa.support.Querydsl4RepositorySupport;
import com.hipcommerce.orders.dto.RetrieveOrderPaymentDto;
import com.querydsl.core.types.ExpressionUtils;
import java.util.Optional;

public class OrderRepositoryImpl extends Querydsl4RepositorySupport implements OrderRepositoryCustom {

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
}
