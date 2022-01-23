package com.hipcommerce.orders.domain;

import com.hipcommerce.orders.dto.OrderDto.DetailedSearchCondition;
import com.hipcommerce.orders.dto.OrderSearchDto;
import com.hipcommerce.orders.dto.RetrieveOrderPaymentDto;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderRepositoryCustom {

  Optional<RetrieveOrderPaymentDto> fetchById(final Long orderId);

  Optional<OrderItem> findByIdAndOrderItemId(final Long id, final Long orderItemId);

  Page<OrderSearchDto> retrieveOrder(DetailedSearchCondition searchCondition, Pageable pageable);

}
