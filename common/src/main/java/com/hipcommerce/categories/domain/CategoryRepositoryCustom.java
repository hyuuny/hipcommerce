package com.hipcommerce.categories.domain;

import com.hipcommerce.categories.dto.CategoryDto.DetailedSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryRepositoryCustom {

  Page<Category> retrieveCategory(DetailedSearchCondition searchCondition, Pageable pageable);

}
