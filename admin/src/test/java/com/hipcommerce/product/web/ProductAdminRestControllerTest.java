package com.hipcommerce.product.web;

import static com.hipcommerce.DummyData.aCategory;
import static com.hipcommerce.DummyData.aProduct;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.common.collect.Lists;
import com.hipcommerce.categories.domain.CategoryRepository;
import com.hipcommerce.categories.dto.CategoryDto;
import com.hipcommerce.categories.service.CategoryService;
import com.hipcommerce.categories.web.CategoryAdminRestController;
import com.hipcommerce.common.BaseIntegrationTest;
import com.hipcommerce.product.domain.Product.Status;
import com.hipcommerce.product.domain.ProductRepository;
import com.hipcommerce.product.dto.ProductDto.Create;
import com.hipcommerce.product.dto.ProductDto.Response;
import com.hipcommerce.product.dto.ProductDto.Update;
import com.hipcommerce.product.dto.ProductOptionDto;
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
class ProductAdminRestControllerTest extends BaseIntegrationTest {

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
                .build());
      } else {
        productService.createProduct(
            aProduct()
                .categoryId(category2.getId())
                .name(value + "순위 슬렉스!!")
                .priorityNumber(value)
                .build());
      }

    });

    mockMvc.perform(get(ProductAdminRestController.REQUEST_URL)
//            .param("searchOption", "productName")
//            .param("keyword", "슬렉스")
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

  @DisplayName("상품 등록")
  @Test
  void createProduct() throws Exception {
    Create createProduct = aProduct().categoryId(1L).build();

    mockMvc.perform(post(ProductAdminRestController.REQUEST_URL)
            .content(this.objectMapper.writeValueAsString(createProduct))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaTypes.HAL_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isCreated())
    ;
  }

  @DisplayName("상품 상세 조회")
  @Test
  void getProduct() throws Exception {
    Response savedProduct = productService.createProductAndGet(aProduct().categoryId(1L).build());

    this.mockMvc.perform(
            get(ProductAdminRestController.REQUEST_URL + "/{id}", savedProduct.getId())
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

  @DisplayName("상품 수정")
  @Test
  void updateProduct() throws Exception {
    Long savedProductId = productService.createProduct(aProduct().categoryId(category1.getId()).build());

    Update updateProduct = Update.builder()
        .categoryId(category2.getId())
        .name("보온 빵빵 후리스")
        .brand("구찌")
        .price(120000L)
        .discountPrice(40000L)
        .tag("#완소|#너도가져봐|#가성비최고")
        .priorityNumber(5)
        .thumbnail(
            "https://hipcommerce-bucket.s3.ap-northeast-2.amazonaws.com/data/image_123456.jpg")
        .status(Status.ON_SALE)
        .options(Lists.newArrayList(
            ProductOptionDto.Create.builder()
                .name("골드")
                .imageUrl(
                    "https://hipcommerce-bucket.s3.ap-northeast-2.amazonaws.com/data/image_1596187406745_1000.jpg")
                .stockQuantity(500)
                .build(),
            ProductOptionDto.Create.builder()
                .name("골드")
                .imageUrl(
                    "https://hipcommerce-bucket.s3.ap-northeast-2.amazonaws.com/data/image_1596187406745_2000.jpg")
                .stockQuantity(500)
                .build()
        ))
        .build();

    this.mockMvc.perform(put(ProductAdminRestController.REQUEST_URL + "/{id}", savedProductId)
            .content(this.objectMapper.writeValueAsString(updateProduct))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaTypes.HAL_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.categoryId").value(updateProduct.getCategoryId()))
        .andExpect(jsonPath("$.name").value(updateProduct.getName()))
        .andExpect(jsonPath("$.brand").value(updateProduct.getBrand()))
        .andExpect(jsonPath("$.price").value(updateProduct.getPrice()))
        .andExpect(jsonPath("$.discountPrice").value(updateProduct.getDiscountPrice()))
        .andExpect(jsonPath("$.calculatedPrice").value(updateProduct.getPrice() - updateProduct.getDiscountPrice()))
        .andExpect(jsonPath("$.priorityNumber").value(updateProduct.getPriorityNumber()))
        .andExpect(jsonPath("$.tag").value(updateProduct.getTag()))
        .andExpect(jsonPath("$.status").value(updateProduct.getStatus().toString()))
        .andExpect(jsonPath("$.thumbnail").value(updateProduct.getThumbnail()))
        .andExpect(jsonPath("$.options").exists())
    ;

  }

  @DisplayName("상품 삭제")
  @Test
  void deleteProduct() throws Exception {

    Long savedProductId = productService.createProduct(aProduct().categoryId(category1.getId()).build());

    this.mockMvc.perform(delete(ProductAdminRestController.REQUEST_URL + "/{id}", savedProductId)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaTypes.HAL_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isNoContent())
    ;
  }
}