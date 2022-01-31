package com.hipcommerce.members.dto;

import com.hipcommerce.members.domain.Authority;
import com.hipcommerce.members.domain.Member;
import com.hipcommerce.members.domain.Member.Gender;
import java.time.LocalDateTime;
import java.util.UUID;

public class MemberSearchDto {

  private Member member;

  private Authority authority;

  public Long getId() {
    return member.getId();
  }

  public UUID getUuId() {
    return member.getUuid();
  }

  public String toUuid() {
    return member.getUuid().toString();
  }

  public String getUsername() {
    return member.getUsername();
  }

  public String getEmail() {
    return member.getEmail();
  }

  public String getMobilePhone() {
    return member.getMobilePhone();
  }

  public String getName() {
    return member.getName();
  }

  public Gender getGender() {
    return member.getGender();
  }

  public String toGender() {
    return member.getGender().toString();
  }

  public LocalDateTime getCreatedDate() {
    return member.getCreatedDate();
  }

  public LocalDateTime getLastModifiedDate() {
    return member.getLastModifiedDate();
  }

}
