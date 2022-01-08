package com.hipcommerce.product.domain;

import com.hipcommerce.product.dto.ProductDto.DetailedSearchCondition;
import com.hipcommerce.product.dto.ProductDto.SearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {

  Page<Product> retrieveProduct(DetailedSearchCondition searchCondition, Pageable pageable);

  Page<Product> retrieveProduct(SearchCondition searchCondition, Pageable pageable);

}
