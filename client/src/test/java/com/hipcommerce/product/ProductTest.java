//package com.hipcommerce.product;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import com.hipcommerce.common.BaseIntegrationTest;
//import com.hipcommerce.product.domain.Product.Status;
//import com.hipcommerce.product.domain.ProductRepository;
//import com.hipcommerce.product.dto.ProductDto.Create;
//import com.hipcommerce.product.service.ProductService;
//import com.hipcommerce.product.web.ProductRestController;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.hateoas.MediaTypes;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.ResultActions;
//import org.springframework.web.context.WebApplicationContext;
//
//@Slf4j
//@SpringBootTest
//public class ProductTest extends BaseIntegrationTest {
//
//  @Autowired
//  ProductService productService;
//
//  @Autowired
//  ProductRepository productRepository;
//
//  @Autowired
//  WebApplicationContext ctx;
//
//  @AfterEach
//  private void tearDown() {
//    log.info("product Delete!");
//    productRepository.deleteAll();
//  }
//
//  @Test
//  @DisplayName("상품 저장")
//  void createProduct() throws Exception {
//      Create create = Create.builder()
//          .name("힙한 바지")
//          .price(5000L)
//          .status(Status.ON_SALE)
//          .build();
//
//    ResultActions resultActions = mockMvc.perform(post(ProductRestController.REQUEST_BASE_PATH)
//            .content(this.objectMapper.writeValueAsString(create))
//            .contentType(MediaType.APPLICATION_JSON_VALUE)
//            .accept(MediaTypes.HAL_JSON_VALUE))
//        .andDo(print());
//
//    // then
//    resultActions
//        .andExpect(status().isOk());
//
//  }
//
//  @Test
//  @DisplayName("상품 조회")
//  void getProduct() throws Exception {
//    Create create = Create.builder()
//        .name("힙한 바지")
//        .price(5000L)
//        .status(Status.ON_SALE)
//        .build();
//
//    Long savedId = productService.createProduct(create);
//
//    ResultActions resultActions = mockMvc.perform(get(ProductRestController.REQUEST_BASE_PATH + "/{id}", 92)
//            .contentType(MediaType.APPLICATION_JSON_VALUE)
//            .accept(MediaTypes.HAL_JSON_VALUE))
//        .andDo(print());
//
//    // then
//    resultActions
//        .andExpect(status().isOk());
//
//  }
//
//
//
//}
