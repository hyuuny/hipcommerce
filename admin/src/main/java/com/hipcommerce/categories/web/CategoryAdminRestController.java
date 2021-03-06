package com.hipcommerce.categories.web;

import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.hipcommerce.categories.dto.CategoryDto.Create;
import com.hipcommerce.categories.dto.CategoryDto.DetailedSearchCondition;
import com.hipcommerce.categories.dto.CategoryDto.Response;
import com.hipcommerce.categories.dto.CategoryDto.Update;
import com.hipcommerce.categories.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Secured("ROLE_ADMIN")
@Tag(name = "???????????? API")
@RequiredArgsConstructor
@RestController
@RequestMapping(path = CategoryAdminRestController.REQUEST_URL, produces = MediaTypes.HAL_JSON_VALUE)
public class CategoryAdminRestController {

  static final String REQUEST_URL = "/api/v1/categories";

  private final CategoryService categoryService;
  private final CategoryResourceAssembler categoryResourceAssembler;

  @Operation(summary = "???????????? ?????? ??? ??????", description = "searchOption: \n"
      + "????????????: cardName")
  @GetMapping
  public ResponseEntity<PagedModel<EntityModel<Response>>> retrieveCategory(
      @ParameterObject @Valid DetailedSearchCondition searchCondition,
      @ParameterObject @PageableDefault(sort = "createdDate", direction = DESC) Pageable pageable,
      PagedResourcesAssembler<Response> pagedResourcesAssembler
  ) {
    Page<Response> page = categoryService.retrieveCategory(searchCondition, pageable);
    return ResponseEntity
        .ok(pagedResourcesAssembler.toModel(page, categoryResourceAssembler));
  }

  @Operation(summary = "???????????? ????????? ??????")
  @GetMapping("/all")
  public ResponseEntity<List<Response>> getCategories() {
    List<Response> categories = categoryService.getAllCategories();
    return ResponseEntity.ok(categories);
  }

  @Operation(summary = "?????? ???????????? ??????")
  @GetMapping("/{id}/children")
  public ResponseEntity<List<Response>> getChildCategories(
      @Parameter(description = "?????? ???????????? ID") @PathVariable final Long id
  ) {
    List<Response> childCategories = categoryService.getChildCategories(id);
    return ResponseEntity.ok(childCategories);
  }


  @Operation(summary = "???????????? ??????")
  @PostMapping
  public ResponseEntity<EntityModel<Response>> createCategory(@RequestBody @Valid Create dto)
      throws URISyntaxException {
    Response savedCategory = categoryService.createCategoryAndGet(dto);
    EntityModel<Response> resource = categoryResourceAssembler.toModel(savedCategory);
    return ResponseEntity
        .created(new URI(resource.getRequiredLink(IanaLinkRelations.SELF).getHref()))
        .body(resource);
  }

  @Operation(summary = "?????? ???????????? ??????")
  @PostMapping("/{id}/children")
  public ResponseEntity<?> createChildCategory(
      @Parameter(description = "?????? ???????????? ID") @PathVariable final Long id,
      @RequestBody @Valid Create dto
  ) throws URISyntaxException {
    Response savedChildCategory = categoryService.createChildCategory(id, dto);
    EntityModel<Response> resource = categoryResourceAssembler.toModel(savedChildCategory);
    return ResponseEntity
        .created(new URI(resource.getRequiredLink(IanaLinkRelations.SELF).getHref()))
        .body(resource);
  }

  @Operation(summary = "???????????? ????????????")
  @GetMapping("/{id}")
  public ResponseEntity<EntityModel<Response>> getCategory(@PathVariable final Long id) {
    Response foundCategory = categoryService.getCategory(id);
    return ResponseEntity.ok(categoryResourceAssembler.toModel(foundCategory));
  }

  @Operation(summary = "???????????? ??????")
  @PutMapping("/{id}")
  public ResponseEntity<EntityModel<Response>> updateCategory(
      @PathVariable final Long id,
      @RequestBody @Valid Update dto
  ) {
    Response updatedCategory = categoryService.updateCategory(id, dto);
    return ResponseEntity.ok(categoryResourceAssembler.toModel(updatedCategory));
  }

  @Operation(summary = "???????????? ??????")
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteCategory(@PathVariable final Long id) {
    categoryService.deleteCategory(id);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "???????????? ?????? ?????????")
  @DeleteMapping("/caches")
  public ResponseEntity<?> cacheEvict() {
    categoryService.evictCaches();
    return ResponseEntity.noContent().build();
  }


  @Component
  static class CategoryResourceAssembler implements
      RepresentationModelAssembler<Response, EntityModel<Response>> {

    @Override
    public EntityModel<Response> toModel(Response entity) {
      return EntityModel.of(
          entity,
          linkTo(methodOn(CategoryAdminRestController.class)
              .getCategory(entity.getId()))
              .withSelfRel()
      );
    }
  }

}
