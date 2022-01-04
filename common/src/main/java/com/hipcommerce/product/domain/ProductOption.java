package com.hipcommerce.product.domain;


import static javax.persistence.FetchType.LAZY;

import com.hipcommerce.common.jpa.domain.BaseNoEqualsEntity;
import com.hipcommerce.common.money.domain.Money;
import com.hipcommerce.common.money.infra.MoneyConverter;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.EqualsAndHashCode;
import lombok.EqualsAndHashCode.Include;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Entity
public class ProductOption extends BaseNoEqualsEntity {

  @ManyToOne(optional = false, fetch = LAZY)
  private Product product;

  @Include
  @Column(nullable = false)
  private String name;

  @Default
  @Convert(converter = MoneyConverter.class)
  private Money price = Money.ZERO;

  private int stockQuantity;

  private String imageUrl;

  public void setProduct(Product product) {
    if (this.product != null) {
      this.product.getOptions().remove(this);
    }
    this.product = product;
    this.product.getOptions().add(this);
  }

}
