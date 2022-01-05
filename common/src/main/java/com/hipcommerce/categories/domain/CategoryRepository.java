package com.hipcommerce.categories.domain;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long>,
    CategoryRepositoryCustom {

  List<Category> findByParent(Category category, Sort sort);

}
