package com.hipcommerce.product.domain;

import static com.hipcommerce.categories.domain.QCategory.category;
import static com.hipcommerce.product.domain.QProduct.product;
import static org.springframework.util.ObjectUtils.isEmpty;

import com.hipcommerce.common.jpa.support.Querydsl4RepositorySupport;
import com.hipcommerce.product.domain.Product.Status;
import com.hipcommerce.product.dto.ProductDto.DetailedSearchCondition;
import com.hipcommerce.product.dto.ProductDto.SearchCondition;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.util.List;
import java.util.Objects;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class ProductRepositoryImpl extends Querydsl4RepositorySupport implements
    ProductRepositoryCustom {

  public ProductRepositoryImpl() {
    super(Product.class);
  }

  @Override
  public Page<Product> retrieveProduct(DetailedSearchCondition searchCondition, Pageable pageable) {
    return applyPagination(pageable, contentQuery -> contentQuery
        .selectFrom(product)
        .join(category).on(category.id.eq(product.categoryId))
        .where(
            statusEq(searchCondition.getStatus()),
            categoryIdsIn(searchCondition.getCategoryIds()),
            keywordSearch(searchCondition.getSearchOption(), searchCondition.getKeyword())
        ));
  }

  @Override
  public Page<Product> retrieveProduct(SearchCondition searchCondition, Pageable pageable) {
    return applyPagination(pageable, contentQuery -> contentQuery
        .selectFrom(product)
        .join(category).on(category.id.eq(product.categoryId))
        .where(
            categoryIdsIn(searchCondition.getCategoryIds()),
            keywordSearch(searchCondition.getSearchOption(), searchCondition.getKeyword())
        ));
  }

  private BooleanExpression categoryIdsIn(List<Long> categoryIds) {
    return isEmpty(categoryIds) ? null : product.categoryId.in(categoryIds);
  }

  private BooleanExpression statusEq(final Status status) {
    return isEmpty(status) ? null : product.status.eq(status);
  }

  private BooleanExpression keywordSearch(final String searchOption, final String keyword) {
    if (isEmpty(searchOption) || isEmpty(keyword)) {
      return null;
    }

    if (Objects.equals(searchOption, "productName")) {
      return product.name.contains(keyword);
    }

    if (Objects.equals(searchOption, "productBrand")) {
      return product.brand.contains(keyword);
    }

    if (Objects.equals(searchOption, "productTag")) {
      return product.tag.contains(keyword);
    }

    return null;
  }

}
