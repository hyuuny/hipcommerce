package com.hipcommerce.orders.domain;

import com.hipcommerce.orders.dto.RetrieveOrderPaymentDto;
import java.util.Optional;

public interface OrderRepositoryCustom {

  Optional<RetrieveOrderPaymentDto> fetchById(Long orderId);

}
