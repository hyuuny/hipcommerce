package com.hipcommerce.categories.domain;

import static com.hipcommerce.categories.domain.QCategory.category;
import static org.springframework.util.ObjectUtils.isEmpty;

import com.hipcommerce.categories.dto.CategoryDto.DetailedSearchCondition;
import com.hipcommerce.common.jpa.support.Querydsl4RepositorySupport;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.util.Objects;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class CategoryRepositoryImpl extends Querydsl4RepositorySupport implements
    CategoryRepositoryCustom {

  public CategoryRepositoryImpl() {
    super(Category.class);
  }

  @Override
  public Page<Category> retrieveCategory(
      DetailedSearchCondition searchCondition,
      Pageable pageable
  ) {
    return applyPagination(pageable, contentQuery -> contentQuery
        .selectFrom(category)
        .where(
            keywordSearch(searchCondition.getSearchOption(), searchCondition.getKeyword())
        ));
  }


  private BooleanExpression keywordSearch(final String searchOption, final String keyword) {
    if (isEmpty(searchOption) || isEmpty(keyword)) {
      return null;
    }

    if (Objects.equals(searchOption, "categoryName")) {
      return category.name.contains(keyword);
    }

    return null;
  }
}
