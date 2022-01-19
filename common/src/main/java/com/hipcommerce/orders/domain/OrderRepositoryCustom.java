package com.hipcommerce.orders.domain;

import com.hipcommerce.orders.dto.RetrieveOrderPaymentDto;
import java.util.Optional;

public interface OrderRepositoryCustom {

  Optional<RetrieveOrderPaymentDto> fetchById(final Long orderId);

  Optional<OrderItem> findByIdAndOrderItemId(final Long id, final Long orderItemId);

}
