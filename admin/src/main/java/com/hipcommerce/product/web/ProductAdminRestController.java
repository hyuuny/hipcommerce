package com.hipcommerce.product.web;

import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.hipcommerce.product.dto.ProductDto;
import com.hipcommerce.product.dto.ProductDto.Create;
import com.hipcommerce.product.dto.ProductDto.DetailedSearchCondition;
import com.hipcommerce.product.dto.ProductDto.Response;
import com.hipcommerce.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.net.URISyntaxException;
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
@Tag(name = "상품 API")
@RequiredArgsConstructor
@RestController
@RequestMapping(path = ProductAdminRestController.REQUEST_URL, produces = MediaTypes.HAL_JSON_VALUE)
public class ProductAdminRestController {

  static final String REQUEST_URL = "/api/v1/products";

  private final ProductService productService;
  private final ProductResourceAssembler productResourceAssembler;

  @Operation(summary = "상품 조회 및 검색", description = "searchOption: \n"
      + "상품이름: productName"
      + "상품브랜드: productBrand"
      + "상품태그: productTag")
  @GetMapping
  public ResponseEntity<PagedModel<EntityModel<Response>>> retrieveProduct(
      @ParameterObject @Valid DetailedSearchCondition searchCondition,
      @ParameterObject @PageableDefault(sort = "createdDate", direction = DESC) Pageable pageable,
      PagedResourcesAssembler<Response> pagedResourcesAssembler
  ) {
    Page<Response> page = productService.retrieveProduct(searchCondition, pageable);
    return ResponseEntity
        .ok(pagedResourcesAssembler.toModel(page, productResourceAssembler));
  }

  @Operation(summary = "상품 등록")
  @PostMapping
  public ResponseEntity<EntityModel<Response>> createProduct(@RequestBody @Valid Create dto)
      throws URISyntaxException {
    Response savedProduct = productService.createProductAndGet(dto);
    EntityModel<Response> resource = productResourceAssembler.toModel(savedProduct);
    return ResponseEntity
        .created(new URI(resource.getRequiredLink(IanaLinkRelations.SELF).getHref()))
        .body(resource);
  }

  @Operation(summary = "상품 상세 조회")
  @GetMapping("/{id}")
  public ResponseEntity<EntityModel<Response>> getProduct(@PathVariable final Long id) {
    Response foundProduct = productService.getProduct(id, true);
    return ResponseEntity.ok(productResourceAssembler.toModel(foundProduct));
  }

  @Operation(summary = "상품 수정")
  @PutMapping("/{id}")
  public ResponseEntity<EntityModel<Response>> updateProduct(
      @PathVariable final Long id,
      @RequestBody @Valid ProductDto.Update dto
  ) {
    Response updatedProduct = productService.updateProduct(id, dto);
    return ResponseEntity.ok(productResourceAssembler.toModel(updatedProduct));
  }

  @Operation(summary = "상품 삭제")
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteProduct(@PathVariable final Long id) {
    productService.deleteProduct(id);
    return ResponseEntity.noContent().build();
  }

  @Component
  static class ProductResourceAssembler implements
      RepresentationModelAssembler<Response, EntityModel<Response>> {

    @Override
    public EntityModel<Response> toModel(Response entity) {
      return EntityModel.of(
          entity,
          linkTo(methodOn(ProductAdminRestController.class)
              .getProduct(entity.getId()))
              .withSelfRel()
      );
    }
  }

}
