package com.hipcommerce.category.web;

import static com.hipcommerce.DummyData.aCategory;
import static java.util.Objects.requireNonNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hipcommerce.categories.domain.CategoryRepository;
import com.hipcommerce.categories.dto.CategoryDto.Create;
import com.hipcommerce.categories.dto.CategoryDto.Response;
import com.hipcommerce.categories.service.CategoryService;
import com.hipcommerce.common.BaseIntegrationTest;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@Slf4j
class CategoryRestControllerTest extends BaseIntegrationTest {

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private CategoryService categoryService;

  @AfterEach
  void tearDown() {
    log.info("categories delete All");
    categoryRepository.deleteAll();
  }

  @DisplayName("카테고리 상세 조회")
  @Test
  void getCategories() throws Exception {
    Create createCategory = aCategory().build();
    Response savedCategory = categoryService.createCategoryAndGet(createCategory);

    this.mockMvc.perform(get(CategoryRestController.REQUEST_URL + "/{id}", savedCategory.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaTypes.HAL_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"))
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.id").value(savedCategory.getId()))
        .andExpect(jsonPath("$.name").value(savedCategory.getName()))
        .andExpect(jsonPath("$.level").value(savedCategory.getLevel()))
        .andExpect(jsonPath("$.iconImageUrl").value(savedCategory.getIconImageUrl()))
        ;
  }

  @DisplayName("없는 카테고리 조회하면 예외")
  @Test
  void getCategoryEx() throws Exception {
    this.mockMvc.perform(get(CategoryRestController.REQUEST_URL + "/{id}", 99999)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaTypes.HAL_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(result -> Assertions.assertThat("category.id.notFound")
            .isEqualTo(requireNonNull(result.getResolvedException().getMessage())))
        ;
  }

  @DisplayName("카테고리 리스트 조회")
  @Test
  void getChildCategories() throws Exception {
    IntStream.range(1, 5).forEach(value -> {
      categoryService.createCategory(
          aCategory()
              .name(value + "번째 카테고리")
              .level(value)
              .priorityNumber(value)
              .build());
    });

    this.mockMvc.perform(get(CategoryRestController.REQUEST_URL)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaTypes.HAL_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(
            header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"))
    ;
  }

  @DisplayName("자녀 카테고리 조회")
  @Test
  void getCategory() throws Exception {
    Long savedParentCategoryId = categoryService.createCategory(aCategory().name("바지").build());

    categoryService.createChildCategory(savedParentCategoryId,
        aCategory()
            .name("데님")
            .level(2)
            .priorityNumber(2)
            .build());

    categoryService.createChildCategory(savedParentCategoryId,
        aCategory()
            .name("트레이닝")
            .level(2)
            .priorityNumber(1)
            .build());

    this.mockMvc.perform(
            get(CategoryRestController.REQUEST_URL + "/{id}/children", savedParentCategoryId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
    ;
  }

}