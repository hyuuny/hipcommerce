package com.hipcommerce.product.web;

import static com.hipcommerce.DummyData.aCategory;
import static com.hipcommerce.DummyData.aProduct;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hipcommerce.categories.domain.CategoryRepository;
import com.hipcommerce.categories.dto.CategoryDto;
import com.hipcommerce.categories.service.CategoryService;
import com.hipcommerce.common.BaseIntegrationTest;
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
class ProductRestControllerTest extends BaseIntegrationTest {

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private ProductService productService;

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private CategoryService categoryService;

  private CategoryDto.Response category1;
  private CategoryDto.Response category2;

  @BeforeEach
  void setUp() {
    category1 = categoryService.createCategoryAndGet(aCategory().name("상의").priorityNumber(1).build());
    category2 = categoryService.createCategoryAndGet(aCategory().name("바지").priorityNumber(1).build());
  }

  @AfterEach
  void tearDown() {
    log.info("ProductAdminRestControllerTest.deleteAll");
    productRepository.deleteAll();
    categoryRepository.deleteAll();
  }

  @DisplayName("상품 조회 및 검색")
  @Test
  void retrieveProduct() throws Exception {
    IntStream.range(1, 12).forEach(value -> {
      if (value % 2 == 0) {
        productService.createProduct(
            aProduct()
                .categoryId(category1.getId())
                .name(value + "순위 체크셔츠!!")
                .priorityNumber(value)
                .tag("#체크|#봄에최고|#가을도최고")
                .build());
      } else {
        productService.createProduct(
            aProduct()
                .categoryId(category2.getId())
                .name(value + "순위 슬렉스!!")
                .priorityNumber(value)
                .tag("#힙한|#와이드|#여름도착용가능")
                .build());
      }

    });

    mockMvc.perform(get(ProductRestController.REQUEST_URL)
//            .param("searchOption", "productTag")
//            .param("keyword", "여름")
//            .param("categoryId", "1")
//            .param("categoryId", "1")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaTypes.HAL_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(
            header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"))
        .andExpect(jsonPath("$._embedded").exists())
        .andExpect(jsonPath("$._embedded.products").exists())
        .andExpect(jsonPath("$._embedded.products", hasSize(10)))
        .andExpect(jsonPath("$.page.size").exists())
        .andExpect(jsonPath("$.page.totalElements").exists())
        .andExpect(jsonPath("$.page.totalElements").value(11))
        .andExpect(jsonPath("$.page.totalPages").exists())
        .andExpect(jsonPath("$.page.totalPages").value(2))
        .andExpect(jsonPath("$.page.number").exists())
        .andExpect(jsonPath("$._links.self").exists())
    ;
  }

  @DisplayName("상품 상세 조회")
  @Test
  void getProduct() throws Exception {
    Response savedProduct = productService.createProductAndGet(aProduct().categoryId(1L).build());

    this.mockMvc.perform(
            get(ProductRestController.REQUEST_URL + "/{id}", savedProduct.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.id").value(savedProduct.getId()))
        .andExpect(jsonPath("$.categoryId").value(savedProduct.getCategoryId()))
        .andExpect(jsonPath("$.name").value(savedProduct.getName()))
        .andExpect(jsonPath("$.brand").value(savedProduct.getBrand()))
        .andExpect(jsonPath("$.price").value(savedProduct.getPrice()))
        .andExpect(jsonPath("$.discountPrice").value(savedProduct.getDiscountPrice()))
        .andExpect(jsonPath("$.calculatedPrice").value(savedProduct.getCalculatedPrice()))
        .andExpect(jsonPath("$.priorityNumber").value(savedProduct.getPriorityNumber()))
        .andExpect(jsonPath("$.tag").value(savedProduct.getTag()))
        .andExpect(jsonPath("$.status").value(savedProduct.getStatus().toString()))
        .andExpect(jsonPath("$.thumbnail").value(savedProduct.getThumbnail()))
        .andExpect(jsonPath("$.options").exists())
    ;
  }
}