package com.hipcommerce.category.web;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.hipcommerce.categories.dto.CategoryDto.Response;
import com.hipcommerce.categories.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "카테고리 API")
@RequiredArgsConstructor
@RequestMapping(path =CategoryRestController.REQUEST_URL, produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class CategoryRestController {

  static final String REQUEST_URL = "/api/v1/categories";

  private final CategoryService categoryService;
  private final CategoryResourceAssembler categoryResourceAssembler;

  @Operation(summary = "카테고리 리스트 조회")
  @GetMapping
  public ResponseEntity<List<Response>> getCategories() {
    List<Response> categories = categoryService.getAllCategories();
    return ResponseEntity.ok(categories);
  }

  @Operation(summary = "자녀 카테고리 조회")
  @GetMapping("/{id}/children")
  public ResponseEntity<List<Response>> getChildCategories(
      @Parameter(description = "부모 카테고리 ID") @PathVariable final Long id
  ) {
    List<Response> childCategories = categoryService.getChildCategories(id);
    return ResponseEntity.ok(childCategories);
  }

  @Operation(summary = "카테고리 상세 조회")
  @GetMapping("/{id}")
  public ResponseEntity<EntityModel<Response>> getCategory(@PathVariable final Long id) {
    Response foundCategory = categoryService.getCategory(id);
    return ResponseEntity.ok(categoryResourceAssembler.toModel(foundCategory));
  }


  @Component
  static class CategoryResourceAssembler implements
      RepresentationModelAssembler<Response, EntityModel<Response>> {

    @Override
    public EntityModel<Response> toModel(Response entity) {
      return EntityModel.of(
          entity,
          linkTo(methodOn(CategoryRestController.class)
              .getCategory(entity.getId()))
              .withSelfRel()
      );
    }
  }


}
