package com.hipcommerce.product.web;

import com.hipcommerce.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.MediaTypes;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping(path = ProductRestController.REQUEST_URL, produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class ProductRestController {

  static final String REQUEST_URL = "/api/v1/products";

  private final ProductService productService;

}
