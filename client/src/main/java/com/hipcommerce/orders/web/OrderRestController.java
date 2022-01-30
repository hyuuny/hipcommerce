package com.hipcommerce.orders.web;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.hipcommerce.common.web.model.HttpStatusMessageException;
import com.hipcommerce.config.security.annotations.CurrentUserId;
import com.hipcommerce.orders.dto.OrderCheckoutDto;
import com.hipcommerce.orders.dto.OrderDto.ChangeDeliveryInfo;
import com.hipcommerce.orders.dto.OrderDto.ChangeOrderItemStatus;
import com.hipcommerce.orders.dto.OrderDto.OrderResult;
import com.hipcommerce.orders.dto.OrderPlaceDto;
import com.hipcommerce.orders.dto.OrderSheetDto.OrderSheetResult;
import com.hipcommerce.orders.service.OrderService;
import com.hipcommerce.orders.service.OrderSheetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "주문 API")
@RequiredArgsConstructor
@RequestMapping(path = OrderRestController.REQUEST_URL, produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class OrderRestController {

  static final String REQUEST_URL = "/api/v1/orders";

  private final OrderService orderService;
  private final OrderSheetService orderSheetService;
  private final OrderSheetResourceAssembler orderSheetResourceAssembler;
  private final OrderResourceAssembler orderResourceAssembler;


  @PreAuthorize("isAuthenticated() and (#dto.userId == #currentUserId)")
  @Operation(summary = "주문상품 체크아웃")
  @PostMapping("/ordering-products/checkout")
  public ResponseEntity<EntityModel<OrderSheetResult>> checkoutOrder(
      @RequestBody @Valid OrderCheckoutDto dto,
      @CurrentUserId final Long currentUserId
  ) throws URISyntaxException {
    OrderSheetResult savedOrderSheet = orderSheetService.checkout(dto);
    EntityModel<OrderSheetResult> resource = orderSheetResourceAssembler.toModel(savedOrderSheet);
    return ResponseEntity
        .created(new URI(resource.getRequiredLink(IanaLinkRelations.SELF).getHref()))
        .body(resource);
  }

  @PreAuthorize("isAuthenticated() and #currentUserId != null")
  @Operation(summary = "주문서 조회")
  @GetMapping("/order-sheets/{orderSheetId}")
  public ResponseEntity<EntityModel<OrderSheetResult>> getOrderSheet(
      @PathVariable final Long orderSheetId,
      @CurrentUserId final Long currentUserId
  ) {
    OrderSheetResult foundOrderSheet = orderSheetService.getOrderSheet(orderSheetId);
    if (!Objects.equals(foundOrderSheet.getUserId(), currentUserId)) {
      throw new HttpStatusMessageException(HttpStatus.FORBIDDEN, "exception.AccessDeniedException");
    }

    return ResponseEntity.ok(orderSheetResourceAssembler.toModel(foundOrderSheet));
  }

  @PreAuthorize("isAuthenticated() and (#dto.userId == #currentUserId)")
  @Operation(summary = "주문")
  @PostMapping
  public ResponseEntity<EntityModel<OrderResult>> placeOrder(
      @RequestBody @Valid OrderPlaceDto dto,
      @CurrentUserId final Long currentUserId
  ) throws URISyntaxException {
    OrderResult savedOrder = orderService.place(dto);
    EntityModel<OrderResult> resource = orderResourceAssembler.toModel(savedOrder);

    return ResponseEntity
        .created(new URI(resource.getRequiredLink(IanaLinkRelations.SELF).getHref()))
        .body(resource);
  }

  @PreAuthorize("isAuthenticated() and #currentUserId != null")
  @Operation(summary = "주문 상세 조회")
  @GetMapping("/{id}")
  public ResponseEntity<EntityModel<OrderResult>> getOrder(
      @PathVariable final Long id,
      @CurrentUserId final Long currentUserId
  ) {
    OrderResult foundOrder = orderService.getOrder(id);
    if (!Objects.equals(foundOrder.getUserId(), currentUserId)) {
      throw new HttpStatusMessageException(HttpStatus.FORBIDDEN, "exception.AccessDeniedException");
    }
    return ResponseEntity.ok(orderResourceAssembler.toModel(foundOrder));
  }

  @PreAuthorize("isAuthenticated() and #currentUserId != null")
  @Operation(summary = "배송지 변경")
  @PutMapping("/{id}/delivery")
  public ResponseEntity<EntityModel<OrderResult>> changeDeliveryInfo(
      @PathVariable Long id,
      @RequestBody @Valid ChangeDeliveryInfo dto,
      @CurrentUserId final Long currentUserId
  ) {
    validationOrderUser(id, currentUserId);
    OrderResult orderResult = orderService.changeDeliveryInfo(id, dto);
    return ResponseEntity.ok(orderResourceAssembler.toModel(orderResult));
  }

  @PreAuthorize("isAuthenticated() and #currentUserId != null")
  @Operation(summary = "구매확정")
  @PutMapping("/purchase-complete")
  public ResponseEntity<EntityModel<OrderResult>> purchaseComplete(
      @RequestBody @Valid ChangeOrderItemStatus dto,
      @CurrentUserId final Long currentUserId
  ) {
    validationOrderUser(dto.getOrderId(), currentUserId);
    OrderResult orderResult = orderService.purchaseCompleted(dto);
    return ResponseEntity.ok(orderResourceAssembler.toModel(orderResult));
  }

  @PreAuthorize("isAuthenticated() and #currentUserId != null")
  @Operation(summary = "주문 취소 요청")
  @PutMapping("/cancel-request")
  public ResponseEntity<EntityModel<OrderResult>> cancelRequest(
      @RequestBody @Valid ChangeOrderItemStatus dto,
      @CurrentUserId final Long currentUserId
  ) {
    validationOrderUser(dto.getOrderId(), currentUserId);
    OrderResult orderResult = orderService.cancelRequest(dto);
    return ResponseEntity.ok(orderResourceAssembler.toModel(orderResult));
  }

  @PreAuthorize("isAuthenticated() and #currentUserId != null")
  @Operation(summary = "주문 반품 요청")
  @PutMapping("/return-request")
  public ResponseEntity<EntityModel<OrderResult>> returnRequest(
      @RequestBody @Valid ChangeOrderItemStatus dto,
      @CurrentUserId final Long currentUserId
  ) {
    validationOrderUser(dto.getOrderId(), currentUserId);
    OrderResult orderResult = orderService.returnRequest(dto);
    return ResponseEntity.ok(orderResourceAssembler.toModel(orderResult));
  }

  private void validationOrderUser(final Long orderId, Long currentUserId) {
    OrderResult foundOrder = orderService.getOrder(orderId);
    if (!Objects.equals(foundOrder.getUserId(), currentUserId)) {
      throw new HttpStatusMessageException(HttpStatus.FORBIDDEN, "exception.AccessDeniedException");
    }
  }

  @Component
  static class OrderSheetResourceAssembler implements
      RepresentationModelAssembler<OrderSheetResult, EntityModel<OrderSheetResult>> {

    @Override
    public EntityModel<OrderSheetResult> toModel(OrderSheetResult entity) {
      return EntityModel.of(
          entity,
          linkTo(methodOn(OrderRestController.class)
              .getOrderSheet(entity.getId(), null))
              .withSelfRel()
      );
    }
  }

  @Component
  static class OrderResourceAssembler implements
      RepresentationModelAssembler<OrderResult, EntityModel<OrderResult>> {

    @Override
    public EntityModel<OrderResult> toModel(OrderResult entity) {
      return EntityModel.of(
          entity,
          linkTo(methodOn(OrderRestController.class)
              .getOrder(entity.getId(), null))
              .withSelfRel()
      );
    }
  }

}
