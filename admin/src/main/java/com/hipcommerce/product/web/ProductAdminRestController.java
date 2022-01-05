package com.hipcommerce.product.web;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.hipcommerce.product.dto.ProductDto.Create;
import com.hipcommerce.product.dto.ProductDto.Response;
import com.hipcommerce.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.net.URISyntaxException;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "상품 API")
@RequiredArgsConstructor
@RestController
@RequestMapping(path = ProductAdminRestController.REQUEST_URL, produces = MediaTypes.HAL_JSON_VALUE)
public class ProductAdminRestController {

  static final String REQUEST_URL = "/api/v1/products";

  private final ProductService productService;
  private final ProductResourceAssembler productResourceAssembler;

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

  @Operation(summary = "상품 상세조회")
  @GetMapping("/{id}")
  public ResponseEntity<EntityModel<Response>> getProduct(@PathVariable final Long id) {
    Response foundProduct = productService.getProduct(id, true);
    return ResponseEntity.ok(productResourceAssembler.toModel(foundProduct));
  }

//  @Operation(summary = "상품 수정")
//  @PutMapping("/v1/products/{id}")
//  public ResponseEntity<EntityModel<Response>> updateProduct(
//      @PathVariable final Long id,
//      @RequestBody @Valid Update dto
//  ) {
//  }

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
