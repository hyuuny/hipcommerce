package com.hipcommerce.members.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "refresh_token")
@Entity
public class RefreshToken {

  @Id
  private String key;

  private String value;

  public RefreshToken updateValue(String token) {
    this.value = token;
    return this;
  }

}