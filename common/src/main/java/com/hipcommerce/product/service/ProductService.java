package com.hipcommerce.product.service;

import com.hipcommerce.product.domain.Product;
import com.hipcommerce.product.dto.ProductDto.Create;
import com.hipcommerce.product.dto.ProductDto.DetailedSearchCondition;
import com.hipcommerce.product.dto.ProductDto.Response;
import com.hipcommerce.product.dto.ProductDto.SearchCondition;
import com.hipcommerce.product.dto.ProductDto.Update;
import com.hipcommerce.product.port.ProductPort;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ProductService {

  private final ProductPort productPort;


  @CacheEvict(value = "searchProductCache", allEntries = true)
  @Transactional
  public Response createProductAndGet(Create dto) {
    Long savedProductId = createProduct(dto);
    return getProduct(savedProductId, false);
  }

  @Transactional
  public Long createProduct(Create dto) {
    return productPort.save(dto).getId();
  }

  @Cacheable(value = "productCache", key = "#id")
  @Transactional
  public Response getProduct(final Long id, boolean hit) {
    Product foundProduct = productPort.getProduct(id, hit);
    return new Response(foundProduct);
  }

  public Page<Response> retrieveProduct(
      DetailedSearchCondition searchCondition,
      Pageable pageable
  ) {
    Page<Response> products = productPort.retrieveProduct(searchCondition, pageable);
    return products;
  }

  @Cacheable(value = "searchProductCache", keyGenerator = "customKeyGenerator", unless = "#result.getContent().size() == 0")
  public Page<Response> retrieveProduct(SearchCondition searchCondition, Pageable pageable) {
    Page<Response> products = productPort.retrieveProduct(searchCondition, pageable);
    return products;
  }

  @Caching(
      evict = {
          @CacheEvict(value = "productCache", key = "#id"),
          @CacheEvict(value = "searchProductCache", allEntries = true)
      }
  )
  @Transactional
  public Response updateProduct(final Long id, Update dto) {
    Product updatedProduct = productPort.updateProduct(id, dto);
    return new Response(updatedProduct);
  }

  @Caching(
      evict = {
          @CacheEvict(value = "productCache", key = "#id"),
          @CacheEvict(value = "searchProductCache", allEntries = true)
      }
  )
  @Transactional
  public void deleteProduct(final Long id) {
    productPort.deleteProduct(id);
  }

}
