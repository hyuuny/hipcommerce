package com.hipcommerce.categories.web;

import static com.hipcommerce.DummyData.aCategory;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hipcommerce.categories.domain.CategoryRepository;
import com.hipcommerce.categories.dto.CategoryDto.Create;
import com.hipcommerce.categories.dto.CategoryDto.Response;
import com.hipcommerce.categories.dto.CategoryDto.Update;
import com.hipcommerce.categories.service.CategoryService;
import com.hipcommerce.common.BaseIntegrationTest;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@Slf4j
class CategoryAdminRestControllerTest extends BaseIntegrationTest {

  @Autowired
  CategoryRepository categoryRepository;

  @Autowired
  CategoryService categoryService;

  @AfterEach
  void tearDown() {
    log.info("categories delete All");
    categoryRepository.deleteAll();
  }

  @DisplayName("카테고리 조회 및 검색")
  @Test
  void retrieveCategory() throws Exception {
    IntStream.range(1, 12).forEach(value -> {
      categoryService.createCategory(
          aCategory()
              .name(value + "번째 카테고리")
              .level(value)
              .priorityNumber(value)
              .build());
    });

    mockMvc.perform(get(CategoryAdminRestController.REQUEST_URL + "/search")
//            .param("searchOption", "categoryName")
//            .param("keyword", "3번째 카테고리")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaTypes.HAL_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(
            header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"))
        .andExpect(jsonPath("$._embedded").exists())
        .andExpect(jsonPath("$._embedded.categories").exists())
        .andExpect(jsonPath("$._embedded.categories", hasSize(10)))
        .andExpect(jsonPath("$.page.size").exists())
        .andExpect(jsonPath("$.page.totalElements").exists())
        .andExpect(jsonPath("$.page.totalElements").value(11))
        .andExpect(jsonPath("$.page.totalPages").exists())
        .andExpect(jsonPath("$.page.totalPages").value(2))
        .andExpect(jsonPath("$.page.number").exists())
        .andExpect(jsonPath("$._links.self").exists())
    ;
  }

  @DisplayName("카테고리 등록")
  @Test
  void createCategory() throws Exception {
    Create createCategory = aCategory().build();

    mockMvc.perform(post(CategoryAdminRestController.REQUEST_URL)
            .content(this.objectMapper.writeValueAsString(createCategory))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaTypes.HAL_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isCreated())
    ;
  }

  @DisplayName("하위 카테고리 등록")
  @Test
  void createChildCategory() throws Exception {
    Create createParentCategory = aCategory().name("바지").build();
    Long savedParentCategoryId = categoryService.createCategory(createParentCategory);

    Create createChild1 = aCategory()
        .name("데님")
        .priorityNumber(1)
        .level(2)
        .build();

    this.mockMvc.perform(
            post(CategoryAdminRestController.REQUEST_URL + "/{id}/children", savedParentCategoryId)
                .content(this.objectMapper.writeValueAsString(createChild1))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isCreated())
    ;
  }

  @DisplayName("카테고리 상세 조회")
  @Test
  void getCategory() throws Exception {
    Response savedCategory = categoryService.createCategoryAndGet(aCategory().build());

    this.mockMvc.perform(
            get(CategoryAdminRestController.REQUEST_URL + "/{id}", savedCategory.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.id").value(savedCategory.getId()))
        .andExpect(jsonPath("$.name").value(savedCategory.getName()))
        .andExpect(jsonPath("$.level").value(savedCategory.getLevel()))
        .andExpect(jsonPath("$.iconImageUrl").value(savedCategory.getIconImageUrl()))
    ;
  }

  @DisplayName("카테고리 수정")
  @Test
  void updateCategory() throws Exception {
    Long savedCategoryId = categoryService.createCategory(aCategory().build());

    Update updateCategory = Update.builder()
        .name("상의")
        .level(2)
        .priorityNumber(2)
        .iconImageUrl(
            "https://hipcommerce-bucket.s3.ap-northeast-2.amazonaws.com/data/image_1596187406745_7000.jpg")
        .build();

    this.mockMvc.perform(put(CategoryAdminRestController.REQUEST_URL + "/{id}", savedCategoryId)
            .content(this.objectMapper.writeValueAsString(updateCategory))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaTypes.HAL_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.name").value(updateCategory.getName()))
        .andExpect(jsonPath("$.level").value(updateCategory.getLevel()))
        .andExpect(jsonPath("$.priorityNumber").value(updateCategory.getPriorityNumber()))
        .andExpect(jsonPath("$.iconImageUrl").value(updateCategory.getIconImageUrl()))
    ;
  }

  @DisplayName("카테고리 삭제")
  @Test
  void deleteCategory() throws Exception {
    Long savedCategoryId = categoryService.createCategory(aCategory().build());

    this.mockMvc.perform(delete(CategoryAdminRestController.REQUEST_URL + "/{id}", savedCategoryId)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaTypes.HAL_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isNoContent())
    ;
  }

  @DisplayName("카테고리 리스트 조회")
  @Test
  void getCategories() throws Exception {
    IntStream.range(1, 5).forEach(value -> {
      categoryService.createCategory(aCategory()
              .name(value + "번째 카테고리")
          .level(value)
          .priorityNumber(value)
          .build());
    });

    this.mockMvc.perform(get(CategoryAdminRestController.REQUEST_URL)
        .content(MediaType.APPLICATION_JSON_VALUE)
        .accept(MediaTypes.HAL_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        ;
  }

  @DisplayName("자식 카테고리 조회")
  @Test
  void getChildrenCategories() throws Exception {
    Long savedParentCategoryId = categoryService.createCategory(aCategory().name("상의").build());

    Response childCategory1 = categoryService.createChildCategory(savedParentCategoryId,
        aCategory()
            .name("셔츠")
            .level(2)
            .priorityNumber(2)
            .build());

    Response childCategory2 = categoryService.createChildCategory(savedParentCategoryId,
        aCategory()
            .name("맨투맨")
            .level(2)
            .priorityNumber(1)
            .build());

    this.mockMvc.perform(
            get(CategoryAdminRestController.REQUEST_URL + "/{id}/children", savedParentCategoryId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
    ;

  }


}