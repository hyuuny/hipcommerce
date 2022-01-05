package com.hipcommerce.categories.port;

import com.hipcommerce.categories.domain.Category;
import com.hipcommerce.categories.domain.CategoryRepository;
import com.hipcommerce.categories.dto.CategoryDto.Create;
import com.hipcommerce.categories.dto.CategoryDto.DetailedSearchCondition;
import com.hipcommerce.categories.dto.CategoryDto.Response;
import com.hipcommerce.common.web.model.HttpStatusMessageException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CategoryPort {

  private final CategoryRepository categoryRepository;


  public Category save(Create dto) {
    Category newCategory = dto.toEntity();
    return categoryRepository.save(newCategory);
  }

  public Category save(Category category) {
    return categoryRepository.save(category);
  }

  public List<Category> getCategories() {
    return categoryRepository.findAll(Sort.by("level", "priorityNumber"));
  }

  public List<Response> toResponses(List<Category> categories) {
    return categories.stream()
        .map(Response::new)
        .collect(Collectors.toList());
  }

  public List<Category> getChildCategories(Category parentCategory) {
    return categoryRepository.findByParent(parentCategory, Sort.by("priorityNumber"));
  }

  public Page<Response> retrieveCategory(
      DetailedSearchCondition searchCondition,
      Pageable pageable
  ) {
    Page<Category> foundCategories = categoryRepository.retrieveCategory(searchCondition, pageable);
    List<Response> categories = toResponses(foundCategories);
    return new PageImpl(categories, pageable, foundCategories.getTotalElements());
  }

  private List<Response> toResponses(Page<Category> pages) {
    return pages.getContent().stream()
        .map(Response::new)
        .collect(Collectors.toList());
  }

  public Category getCategory(final Long id) {
    return categoryRepository.findById(id).orElseThrow(
        () -> new HttpStatusMessageException(HttpStatus.BAD_REQUEST, "category.id.notFound", id)
    );
  }

  public void delete(final Long id) {
    categoryRepository.deleteById(id);
  }

}
