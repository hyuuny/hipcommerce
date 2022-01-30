package com.hipcommerce.members.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Authority {

  USER("회원"), ADMIN("관리자");

  private final String title;

}
