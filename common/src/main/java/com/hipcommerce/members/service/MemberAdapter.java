package com.hipcommerce.members.service;

import static org.springframework.util.ObjectUtils.isEmpty;

import com.hipcommerce.members.domain.Authority;
import com.hipcommerce.members.domain.Member;
import com.hipcommerce.members.domain.Member.Gender;
import com.hipcommerce.members.domain.Member.Status;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class MemberAdapter extends User {

  private Member member;

  public MemberAdapter(Member member) {
    this(member, authorities(member.getAuthorities()));
  }

  public MemberAdapter(
      Member member,
      Collection<? extends GrantedAuthority> authorities
  ) {
    super(
        member.getUsername(),
        isEmpty(member.getPassword()) ? UUID.randomUUID().toString() : member.getPassword(),
        member.isEnabled(),
        member.isAccountNonExpired(),
        member.isCredentialsNonExpired(),
        member.isAccountNonLocked(),
        authorities
    );
    this.member = member;
  }

  private static Collection<? extends GrantedAuthority> authorities(Set<Authority> roles) {
    return roles.stream()
        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
        .collect(Collectors.toSet());
  }

  public Long getUserId() {
    return member.getId();
  }

  public UUID getUuid() {
    return member.getUuid();
  }

  public String getUsername() {
    return member.getUsername();
  }

  public String getEmail() {
    return member.getEmail();
  }

  public String getPassword() {
    return member.getPassword();
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

  public Status getStatus() {
    return member.getStatus();
  }

  public LocalDateTime getCreatedDate() {
    return member.getCreatedDate();
  }

  public LocalDateTime getLastModifiedDate() {
    return member.getLastModifiedDate();
  }

}
