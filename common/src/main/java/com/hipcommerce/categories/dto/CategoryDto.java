package com.hipcommerce.categories.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.hipcommerce.categories.domain.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.server.core.Relation;

public class CategoryDto {

  @Getter
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @Builder
  @Schema(name = "CategoryDto.Create", description = "카테고리 등록")
  public static class Create {

    @NotNull
    @Schema(description = "이름", example = "아우터", required = true)
    private String name;

    @NotNull
    @Schema(description = "레벨(부모1, 자녀2)", example = "1", required = true)
    private Integer level;

    @NotNull
    @Schema(description = "우선순위", example = "1", required = true)
    private Integer priorityNumber;

    @Schema(description = "공개여부", example = "false", required = false)
    private boolean invisible;

    @Schema(description = "카테고리 아이콘 이미지 url", example = "https://hipcommerce-bucket.s3.ap-northeast-2.amazonaws.com/data/image_1596187406745_1000.jpg", required = false)
    private String iconImageUrl;

    public Category toEntity() {
      return Category.builder()
          .name(this.name)
          .level(this.level)
          .priorityNumber(this.priorityNumber)
          .iconImageUrl(this.iconImageUrl)
          .build();
    }

  }

  @Getter
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @Builder
  @Schema(name = "CategoryDto.Update", description = "카테고리 수정")
  public static class Update {

    @NotNull
    @Schema(description = "이름", example = "아우터", required = true)
    private String name;

    @NotNull
    @Schema(description = "레벨(부모1, 자녀2)", example = "1", required = true)
    private Integer level;

    @NotNull
    @Schema(description = "우선순위", example = "1", required = true)
    private Integer priorityNumber;

    @Schema(description = "공개여부", example = "false", required = false)
    private boolean invisible;

    @Schema(description = "카테고리 아이콘 이미지 url", example = "https://hipcommerce-bucket.s3.ap-northeast-2.amazonaws.com/data/image_1596187406745_1000.jpg", required = false)
    private String iconImageUrl;

    public void update(Category entity) {
      entity.changeName(this.name);
      entity.changeLevel(this.level);
      entity.changePriorityNumber(this.priorityNumber);
      entity.changeInvisible(this.invisible);
      entity.changeIconImageUrl(this.iconImageUrl);
    }

  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Relation(collectionRelation = "categories")
  @JsonInclude(Include.NON_EMPTY)
  @Schema(name = "CategoryDto.Response", description = "카테고리")
  public static class Response {

    @Schema(description = "아이디", example = "1", required = true)
    private Long id;

    @Schema(description = "이름", example = "아우터", required = true)
    private String name;

    @Schema(description = "레벨", example = "1", required = true)
    private Integer level;

    @Schema(description = "우선순위", example = "1", required = true)
    private Integer priorityNumber;

    @Schema(description = "공개여부", example = "false", required = false)
    private boolean invisible;

    @Schema(description = "카테고리 아이콘 이미지 url", example = "https://hipcommerce-bucket.s3.ap-northeast-2.amazonaws.com/data/image_1596187406745_1000.jpg", required = false)
    private String iconImageUrl;

    @Schema(description = "부모 카테고리 아이디", required = false)
    private Long parentCategoryId;

    @Schema(description = "등록일", example = "2022-01-11T13:16:32.139065", required = false)
    private LocalDateTime createdDate;

    @Schema(description = "수정일", example = "2022-01-12T15:42:06.139065", required = false)
    private LocalDateTime lastModifiedDate;

    public Response(Category entity) {
      this.id = entity.getId();
      this.name = entity.getName();
      this.level = entity.getLevel();
      this.priorityNumber = entity.getPriorityNumber();
      this.invisible = entity.isInvisible();
      this.iconImageUrl = entity.getIconImageUrl();
      this.parentCategoryId = entity.getParentId();
      this.createdDate = entity.getCreatedDate();
      this.lastModifiedDate = entity.getLastModifiedDate();
    }

  }

  @Setter
  @Getter
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @Builder
  @Schema(name = "CategoryDto.DetailedSearchCondition", description = "카테고리 상세검색조건")
  public static class DetailedSearchCondition {

    @Schema(description = "검색옵션", example = "categoryName", required = false)
    private String searchOption;

    @Schema(description = "검색어", example = "아우터", required = false)
    private String keyword;

  }

}
