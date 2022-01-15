package com.hipcommerce.orders.service;

import com.hipcommerce.common.money.domain.Money;
import com.hipcommerce.orders.domain.OrderSheet;
import com.hipcommerce.orders.domain.OrderSheetItem;
import com.hipcommerce.orders.dto.OrderCheckoutDto;
import com.hipcommerce.orders.dto.OrderCheckoutDto.OrderCheckoutItem;
import com.hipcommerce.product.domain.Product;
import com.hipcommerce.product.port.ProductPort;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class OrderSheetMapper {

  private final ProductPort productPort;

  public OrderSheet mapForm(OrderCheckoutDto orderCheckout) {
    List<OrderSheetItem> orderSheetItems = toOrderSheetItems(
        orderCheckout,
        toProductsMap(orderCheckout)
    );

    return new OrderSheet(orderCheckout.getUserId(), orderSheetItems);
  }

  private Map<Long, Product> toProductsMap(OrderCheckoutDto orderCheckout) {
    return productPort.getProducts(toProductIds(orderCheckout)).stream()
        .collect(Collectors.toMap(Product::getId, Function.identity()));
  }

  private List<Long> toProductIds(OrderCheckoutDto orderCheckout) {
    return orderCheckout.getOrderCheckoutItems().stream()
        .map(OrderCheckoutItem::getProductId)
        .collect(Collectors.toList());
  }

  private List<OrderSheetItem> toOrderSheetItems(
      OrderCheckoutDto orderCheckout,
      Map<Long, Product> productMap
  ) {
    return orderCheckout.getOrderCheckoutItems().stream()
        .map(checkoutItem -> {
          Product existingProduct = productMap.get(checkoutItem.getProductId());

          return OrderSheetItem.builder()
              .productCode(existingProduct.getCode())
              .name(existingProduct.getName())
              .brand(existingProduct.getBrand())
              .thumbnail(existingProduct.getThumbnail())
              .optionName(checkoutItem.getOptionName())
              .price(existingProduct.getPrice())
              .optionPrice(Money.wons(checkoutItem.getOptionPrice()))
              .discountPrice(existingProduct.getDiscountPrice())
              .quantity(checkoutItem.getQuantity())
              .build();
        })
        .collect(Collectors.toList());
  }

}
