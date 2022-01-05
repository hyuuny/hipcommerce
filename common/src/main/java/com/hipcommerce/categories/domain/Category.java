package com.hipcommerce.categories.domain;


import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;
import static org.springframework.util.ObjectUtils.isEmpty;

import com.google.common.collect.Lists;
import com.hipcommerce.common.jpa.domain.BaseEntity;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder.Default;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Entity
public class Category extends BaseEntity {

  @Column(nullable = false)
  private String name;

  private int level;

  private int priorityNumber;

  private String iconImageUrl;

  private boolean invisible;

  @ManyToOne(fetch = LAZY)
  private Category parent;

  @Default
  @OneToMany(mappedBy = "parent", cascade = ALL, orphanRemoval = true)
  private List<Category> children = Lists.newArrayList();

  public void setParent(Category parent) {
    if (!isEmpty(this.parent)) {
      this.parent.getChildren().remove(this);
    }
    this.parent = parent;
    this.parent.getChildren().add(this);
  }

  public Long getParentId() {
    return isExistParent() ? this.parent.getId() : null;
  }

  public boolean isExistParent() {
    return !isEmpty(this.parent);
  }

  public void changeName(final String name) {
    this.name = name;
  }

  public void changeLevel(final int level) {
    this.level = level;
  }

  public void changePriorityNumber(final int priorityNumber) {
    this.priorityNumber = priorityNumber;
  }

  public void changeIconImageUrl(final String iconImageUrl) {
    this.iconImageUrl = iconImageUrl;
  }

  public void changeInvisible(final boolean invisible) {
    this.invisible = invisible;
  }



}
