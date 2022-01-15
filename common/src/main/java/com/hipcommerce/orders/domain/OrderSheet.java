package com.hipcommerce.orders.domain;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.EAGER;

import com.google.common.collect.Lists;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.EqualsAndHashCode.Include;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@EntityListeners(AuditingEntityListener.class)
@Entity
public class OrderSheet{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(unique = true, nullable = false)
  @Include
  private Long id;

  @Column(nullable = false)
  private Long userId;

  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime createdDate;

  @OneToMany(cascade = ALL, fetch = EAGER)
  @JoinColumn(name = "order_sheet_id")
  private List<OrderSheetItem> orderSheetItems = Lists.newArrayList();

  public OrderSheet(
      final Long userId,
      List<OrderSheetItem> orderSheetItems
  ) {
    this.userId = userId;
    this.orderSheetItems = orderSheetItems;
  }

}
