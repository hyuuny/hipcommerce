package com.hipcommerce.product.port;

import com.hipcommerce.common.web.model.HttpStatusMessageException;
import com.hipcommerce.product.domain.Product;
import com.hipcommerce.product.domain.ProductRepository;
import com.hipcommerce.product.domain.event.ProductHitEvent;
import com.hipcommerce.product.dto.ProductDto.Create;
import com.hipcommerce.product.dto.ProductDto.DetailedSearchCondition;
import com.hipcommerce.product.dto.ProductDto.Response;
import com.hipcommerce.product.dto.ProductDto.SearchCondition;
import com.hipcommerce.product.dto.ProductDto.Update;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ProductPort {

  private final ProductRepository productRepository;
  private final ApplicationEventPublisher eventPublisher;


  public Product save(Create dto) {
    Product newProduct = dto.toEntity();
    return productRepository.save(newProduct);
  }

  public Product getProduct(Long id) {
    return productRepository.findById(id).orElseThrow(
        () -> new HttpStatusMessageException(HttpStatus.BAD_REQUEST, "product.id.notFound", id)
    );
  }

  public Product getProduct(final Long id, boolean hit) {
    Product foundProduct = getProduct(id);

    if (hit) {
      eventPublisher.publishEvent(new ProductHitEvent(id));
    }

    return foundProduct;
  }

  public Product updateProduct(final Long id, Update dto) {
    Product foundProduct = getProduct(id);
    dto.update(foundProduct);
    return foundProduct;
  }

  public void deleteProduct(final Long id) {
    Product foundProduct = getProduct(id);
    foundProduct.delete();
  }

  public Page<Response> retrieveProduct(
      DetailedSearchCondition searchCondition,
      Pageable pageable
  ) {
    Page<Product> pages = productRepository.retrieveProduct(searchCondition, pageable);
    List<Response> products = toResponse(pages);
    return new PageImpl<>(products, pageable, pages.getTotalElements());
  }

  private List<Response> toResponse(Page<Product> pages) {
    return pages.getContent().stream()
        .map(Response::new)
        .collect(Collectors.toList());
  }

  public Page<Response> retrieveProduct(SearchCondition searchCondition, Pageable pageable) {
    Page<Product> pages = productRepository.retrieveProduct(searchCondition, pageable);
    List<Response> products = toResponse(pages);
    return new PageImpl<>(products, pageable, pages.getTotalElements());
  }

  public List<Product> getProducts(List<Long> productIds) {
    return productRepository.findAllById(productIds);
  }

}
