package com.hipcommerce.categories.service;

import com.hipcommerce.categories.domain.Category;
import com.hipcommerce.categories.dto.CategoryDto.Create;
import com.hipcommerce.categories.dto.CategoryDto.DetailedSearchCondition;
import com.hipcommerce.categories.dto.CategoryDto.Response;
import com.hipcommerce.categories.dto.CategoryDto.Update;
import com.hipcommerce.categories.port.CategoryPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CategoryService {

  private final CategoryPort categoryPort;

  @Transactional
  public Response createCategoryAndGet(Create dto) {
    Long savedCategoryId = createCategory(dto);
    return getCategory(savedCategoryId);
  }

  @Transactional
  public Long createCategory(Create dto) {
    return categoryPort.save(dto).getId();
  }

  @Transactional
  public Response createChildCategory(final Long parentCategoryId, Create dto) {
    Category parentCategory = categoryPort.getCategory(parentCategoryId);
    Category newCategory = dto.toEntity();
    newCategory.setParent(parentCategory);
    return getCategory(categoryPort.save(newCategory).getId());
  }

  public Response getCategory(final Long id) {
    Category foundCategory = categoryPort.getCategory(id);
    return new Response(foundCategory);
  }

  public List<Response> getAllCategories() {
    List<Category> categories = categoryPort.getCategories();
    return categoryPort.toResponses(categories);
  }

  public List<Response> getChildCategories(final Long parentCategoryId) {
    Category parentCategory = categoryPort.getCategory(parentCategoryId);
    List<Category> childCategories = categoryPort.getChildCategories(parentCategory);
    return categoryPort.toResponses(childCategories);
  }

  public Page<Response> retrieveCategory(
      DetailedSearchCondition searchCondition,
      Pageable pageable
  ) {
    Page<Response> categories = categoryPort.retrieveCategory(searchCondition, pageable);
    return categories;
  }

  @Transactional
  public Response updateCategory(final Long id, Update dto) {
    Category updatedCategory = categoryPort.updateCategory(id, dto);
    return new Response(updatedCategory);
  }

  @Transactional
  public void deleteCategory(final Long id) {
    categoryPort.delete(id);
  }


}
