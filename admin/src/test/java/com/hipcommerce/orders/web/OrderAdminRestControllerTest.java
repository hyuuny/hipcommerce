package com.hipcommerce.orders.web;

import static com.hipcommerce.DummyData.ADMIN_EMAIL;
import static com.hipcommerce.DummyData.ADMIN_PASSWORD;
import static com.hipcommerce.DummyData.aCategory;
import static com.hipcommerce.DummyData.aProduct;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.common.collect.Lists;
import com.hipcommerce.categories.domain.CategoryRepository;
import com.hipcommerce.categories.service.CategoryService;
import com.hipcommerce.common.BaseIntegrationTest;
import com.hipcommerce.common.address.domain.Address;
import com.hipcommerce.common.money.domain.Money;
import com.hipcommerce.orders.domain.DeliveryInfo;
import com.hipcommerce.orders.domain.Order.PayMethod;
import com.hipcommerce.orders.domain.OrderItem.Status;
import com.hipcommerce.orders.domain.OrderRepository;
import com.hipcommerce.orders.domain.OrderSheetRepository;
import com.hipcommerce.orders.domain.Orderer;
import com.hipcommerce.orders.domain.Recipient;
import com.hipcommerce.orders.dto.OrderCheckoutDto;
import com.hipcommerce.orders.dto.OrderCheckoutDto.OrderCheckoutItem;
import com.hipcommerce.orders.dto.OrderDto.ChangeDeliveryInfo;
import com.hipcommerce.orders.dto.OrderDto.ChangeOrderItemStatus;
import com.hipcommerce.orders.dto.OrderDto.ChangeOrderItemsStatus;
import com.hipcommerce.orders.dto.OrderDto.ChangeOrderItemsStatus.ChangeOrderItemsStatusBuilder;
import com.hipcommerce.orders.dto.OrderDto.OrderResult;
import com.hipcommerce.orders.dto.OrderPlaceDto;
import com.hipcommerce.orders.dto.OrderSheetDto.OrderSheetResult;
import com.hipcommerce.orders.service.OrderService;
import com.hipcommerce.orders.service.OrderSheetService;
import com.hipcommerce.product.domain.ProductRepository;
import com.hipcommerce.product.dto.ProductDto.Response;
import com.hipcommerce.product.service.ProductService;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@Slf4j
class OrderAdminRestControllerTest extends BaseIntegrationTest {

  @Autowired
  private OrderSheetRepository orderSheetRepository;

  @Autowired
  private OrderSheetService orderSheetService;

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private OrderService orderService;

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private ProductService productService;

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private CategoryService categoryService;

  private Long categoryId1;
  private Long categoryId2;

  @BeforeEach
  void setUp() {
    categoryId1 = categoryService.createCategoryAndGet(aCategory().name("아우터").priorityNumber(1).build()).getId();
    categoryId2 = categoryService.createCategoryAndGet(aCategory().name("신발").priorityNumber(1).build()).getId();
  }

  @AfterEach
  void tearDown() {
    log.info("OrderAdminRestControllerTest.deleteAll");
    orderSheetRepository.deleteAll();
    orderRepository.deleteAll();
    productRepository.deleteAll();
    categoryRepository.deleteAll();
  }

  @DisplayName("주문 조회 및 검색")
  @Test
  void retrieveOrder() throws Exception {

    IntStream.range(1, 22).forEach(value -> {

      Response orderingProduct1 = createOrderingProduct1();
      Response orderingProduct2 = createOrderingProduct2();
      OrderCheckoutDto orderCheckoutDto = createOrderCheckoutDto(orderingProduct1, orderingProduct2);
      OrderSheetResult orderSheetResult = orderSheetService.checkout(orderCheckoutDto);

      OrderPlaceDto createOrder = createOrderPlaceDto(orderSheetResult);
      OrderResult orderResult = orderService.place(createOrder);

    });

    mockMvc.perform(get(OrderAdminRestController.REQUEST_URL)
            .header(HttpHeaders.AUTHORIZATION, getBearerToken(ADMIN_EMAIL, ADMIN_PASSWORD))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaTypes.HAL_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(
            header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"))
        .andExpect(jsonPath("$._embedded").exists())
        .andExpect(jsonPath("$._embedded.orders").exists())
        .andExpect(jsonPath("$._embedded.orders", hasSize(10)))
        .andExpect(jsonPath("$.page.size").exists())
        .andExpect(jsonPath("$.page.totalElements").exists())
        .andExpect(jsonPath("$.page.totalElements").value(21))
        .andExpect(jsonPath("$.page.totalPages").exists())
        .andExpect(jsonPath("$.page.totalPages").value(3))
        .andExpect(jsonPath("$.page.number").exists())
        .andExpect(jsonPath("$._links.self").exists())
        ;

  }

  private Response createOrderingProduct1() {
    return productService.createProductAndGet(
        aProduct()
            .categoryId(categoryId1)
            .name("코닥 후드집업")
            .priorityNumber(1)
            .price(13000L)
            .discountPrice(3000L)
            .tag("#체크|#봄에최고|#가을도최고")
            .build());
  }

  private Response createOrderingProduct2() {
    return productService.createProductAndGet(
        aProduct()
            .categoryId(categoryId1)
            .name("에어맥스")
            .priorityNumber(2)
            .price(25000L)
            .discountPrice(5000L)
            .tag("#힙한|#와이드|#여름도착용가능")
            .build());
  }

  private OrderCheckoutDto createOrderCheckoutDto(Response orderingProduct1,
      Response orderingProduct2) {
    return new OrderCheckoutDto(
        1L,
        Lists.newArrayList(
            new OrderCheckoutItem(
                orderingProduct1.getId(),
                "블랙",
                Money.ZERO.longValue(),
                5
            ),
            new OrderCheckoutItem(
                orderingProduct2.getId(),
                "화이트",
                Money.ZERO.longValue(),
                2
            )
        )
    );
  }

  private OrderPlaceDto createOrderPlaceDto(OrderSheetResult orderSheetResult) {
    Recipient recipient = new Recipient("나받음", "01045674567");
    Address address = new Address("12345", "경기도 광명시 시청로 110", "반포자이 306동 2108호");
    return OrderPlaceDto.builder()
        .userId(1L)
        .orderSheetId(orderSheetResult.getId())
        .deliveryInfo(new DeliveryInfo(recipient, address, "집앞에 보관해주세요!"))
        .orderer(new Orderer("나주문", "01012341234", "gumea@naver.com"))
        .payMethod(PayMethod.CARD)
        .build();
  }

  @DisplayName("주문 상세 조회")
  @Test
  void getOrder() throws Exception {
    Response orderingProduct1 = createOrderingProduct1();
    Response orderingProduct2 = createOrderingProduct2();
    OrderCheckoutDto orderCheckoutDto = createOrderCheckoutDto(orderingProduct1, orderingProduct2);
    OrderSheetResult orderSheetResult = orderSheetService.checkout(orderCheckoutDto);

    OrderPlaceDto createOrder = createOrderPlaceDto(orderSheetResult);
    OrderResult orderResult = orderService.place(createOrder);

    this.mockMvc.perform(get(OrderAdminRestController.REQUEST_URL + "/{id}", orderResult.getId())
            .header(HttpHeaders.AUTHORIZATION, getBearerToken(ADMIN_EMAIL, ADMIN_PASSWORD))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaTypes.HAL_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(
            header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"))
        .andExpect(jsonPath("$.order").exists())
        .andExpect(jsonPath("$.order.orderItems",
            hasSize(orderCheckoutDto.getOrderCheckoutItems().size())))
    ;
  }

  @DisplayName("주문 상태 변경")
  @Test
  void changeStatuses() throws Exception {
      Response orderingProduct1 = createOrderingProduct1();
      Response orderingProduct2 = createOrderingProduct2();
      OrderCheckoutDto orderCheckoutDto = createOrderCheckoutDto(orderingProduct1, orderingProduct2);
      OrderSheetResult orderSheetResult = orderSheetService.checkout(orderCheckoutDto);

      OrderPlaceDto createOrder = createOrderPlaceDto(orderSheetResult);
      OrderResult orderResult = orderService.place(createOrder);

    ChangeOrderItemsStatus changeOrderItemsStatus = ChangeOrderItemsStatus.builder()
        .changeOrderItemStatuses(Lists.newArrayList(
            ChangeOrderItemStatus.builder()
                .orderId(orderResult.getId())
                .orderItemId(orderResult.getOrderItems().get(0).getId())
                .status(Status.DELIVERING)
                .build(),
            ChangeOrderItemStatus.builder()
                .orderId(orderResult.getId())
                .orderItemId(orderResult.getOrderItems().get(1).getId())
                .status(Status.WAITING_DELIVERY)
                .build()
        ))
        .build();

    this.mockMvc.perform(put(OrderAdminRestController.REQUEST_URL + "/change-statuses")
            .header(HttpHeaders.AUTHORIZATION, getBearerToken(ADMIN_EMAIL, ADMIN_PASSWORD))
        .content(this.objectMapper.writeValueAsString(changeOrderItemsStatus))
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .accept(MediaTypes.HAL_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        ;

  }

  @DisplayName("배송지 변경")
  @Test
  void changeDeliveryInfo() throws Exception {
    Response orderingProduct1 = createOrderingProduct1();
    Response orderingProduct2 = createOrderingProduct2();
    OrderCheckoutDto orderCheckoutDto = createOrderCheckoutDto(orderingProduct1, orderingProduct2);
    OrderSheetResult orderSheetResult = orderSheetService.checkout(orderCheckoutDto);

    OrderPlaceDto createOrder = createOrderPlaceDto(orderSheetResult);
    OrderResult orderResult = orderService.place(createOrder);

    Recipient updateRecipient = new Recipient("수정됨", "01058435132");
    Address updateAddress = new Address("58432", "수정시 수정동 수정로 682-3", "수정아파트 1120동 202호");
    ChangeDeliveryInfo updateDeliveryInfo = ChangeDeliveryInfo.builder()
        .deliveryInfo(new DeliveryInfo(updateRecipient, updateAddress, "배송전 연락주세요"))
        .build();

    this.mockMvc.perform(put(OrderAdminRestController.REQUEST_URL + "/{id}/delivery", orderResult.getId())
            .header(HttpHeaders.AUTHORIZATION, getBearerToken(ADMIN_EMAIL, ADMIN_PASSWORD))
            .content(this.objectMapper.writeValueAsString(updateDeliveryInfo))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaTypes.HAL_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(
            header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"))
        .andExpect(jsonPath("$.order").exists())
        .andExpect(jsonPath("$.order.deliveryInfo.recipient.name").value(updateDeliveryInfo.getDeliveryInfo().getRecipient().getName()))
        .andExpect(jsonPath("$.order.deliveryInfo.recipient.phone").value(updateDeliveryInfo.getDeliveryInfo().getRecipient().getPhone()))
        .andExpect(jsonPath("$.order.deliveryInfo.address.zipCode").value(updateDeliveryInfo.getDeliveryInfo().getAddress().getZipCode()))
        .andExpect(jsonPath("$.order.deliveryInfo.address.address").value(updateDeliveryInfo.getDeliveryInfo().getAddress().getAddress()))
        .andExpect(jsonPath("$.order.deliveryInfo.address.detailedAddress").value(updateDeliveryInfo.getDeliveryInfo().getAddress().getDetailedAddress()))
    ;
  }
}