package com.hipcommerce.orders.web;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.hipcommerce.orders.dto.OrderDto;
import com.hipcommerce.orders.dto.OrderDto.ChangeOrderItemsStatus;
import com.hipcommerce.orders.dto.OrderDto.DetailedSearchCondition;
import com.hipcommerce.orders.dto.OrderDto.OrderResult;
import com.hipcommerce.orders.dto.OrderDto.Response;
import com.hipcommerce.orders.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Secured("ROLE_ADMIN")
@Tag(name = "주문 API")
@RequiredArgsConstructor
@RestController
@RequestMapping(path = OrderAdminRestController.REQUEST_URL, produces = MediaTypes.HAL_JSON_VALUE)
public class OrderAdminRestController {

  static final String REQUEST_URL = "/api/v1/orders";

  private final OrderService orderService;
  private final OrderResourceAssembler orderResourceAssembler;
  private final OrderResultResourceAssembler orderResultResourceAssembler;

  @Operation(summary = "주문 조회 및 검색")
  @GetMapping
  public ResponseEntity<PagedModel<EntityModel<Response>>> retrieveOrder(
      @ParameterObject @Valid DetailedSearchCondition searchCondition,
      @ParameterObject @PageableDefault(sort = "createdDate", direction = Direction.DESC) Pageable pageable,
      PagedResourcesAssembler<Response> pagedResourcesAssembler
  ) {
    Page<Response> page = orderService.retrieveOrder(searchCondition, pageable);
    return ResponseEntity
        .ok(pagedResourcesAssembler.toModel(page, orderResourceAssembler));
  }

  @Operation(summary = "주문 상세조회")
  @GetMapping("/{id}")
  public ResponseEntity<EntityModel<OrderResult>> getOrder(@PathVariable final Long id) {
    OrderResult orderResult = orderService.getOrder(id);
    return ResponseEntity.ok(orderResultResourceAssembler.toModel(orderResult));
  }

  @Operation(summary = "주문 상태 변경")
  @PutMapping("/change-statuses")
  public ResponseEntity<?> changeStatuses(
      @RequestBody @Valid ChangeOrderItemsStatus dto
  ) {
    orderService.changeOrderItemsStatus(dto);
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "배송지 변경")
  @PutMapping("/{id}/delivery")
  public ResponseEntity<EntityModel<OrderResult>> changeDeliveryInfo(
      @PathVariable Long id,
      @RequestBody @Valid OrderDto.ChangeDeliveryInfo dto
  ) {
    OrderResult orderResult = orderService.changeDeliveryInfo(id, dto);
    return ResponseEntity.ok(orderResultResourceAssembler.toModel(orderResult));
  }

  @Component
  static class OrderResultResourceAssembler implements
      RepresentationModelAssembler<OrderResult, EntityModel<OrderResult>> {

    @Override
    public EntityModel<OrderDto.OrderResult> toModel(OrderDto.OrderResult entity) {
      return EntityModel.of(
          entity,
          linkTo(methodOn(OrderAdminRestController.class)
              .getOrder(entity.getId())
          ).withSelfRel()
      );
    }
  }

  @Component
  static class OrderResourceAssembler implements
      RepresentationModelAssembler<Response, EntityModel<Response>> {

    @Override
    public EntityModel<Response> toModel(Response entity) {
      return EntityModel.of(
          entity,
          linkTo(methodOn(OrderAdminRestController.class)
              .getOrder(entity.getUserId()))
              .withSelfRel()
      );
    }
  }

}
