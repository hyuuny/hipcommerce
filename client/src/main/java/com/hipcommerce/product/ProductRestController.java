package com.hipcommerce.product;

import com.hipcommerce.product.dto.ProductDto.Create;
import com.hipcommerce.product.dto.ProductDto.Response;
import com.hipcommerce.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping(path = ProductRestController.REQUEST_BASE_PATH, produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class ProductRestController {

  static final String REQUEST_BASE_PATH = "/api/products";
  private final ProductService productService;

  @PostMapping
  public ResponseEntity<Long> createProduct(@RequestBody Create dto) {
    Long product = productService.createProduct(dto);
    return ResponseEntity.ok(product);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Response> getProduct(@PathVariable final Long id) {
    Response foundProduct = productService.getProduct(id);
    return ResponseEntity.ok(foundProduct);
  }

}
