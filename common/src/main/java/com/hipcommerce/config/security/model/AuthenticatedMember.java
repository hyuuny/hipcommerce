package com.hipcommerce.config.security.model;

import com.hipcommerce.members.domain.Member;
import com.hipcommerce.members.domain.Member.Gender;
import com.hipcommerce.members.domain.Member.Status;
import com.hipcommerce.members.service.MemberAdapter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class AuthenticatedMember {

  private Long userId;

  private String username;

  private String email;

  private String mobilePhone;

  private String name;

  private Gender gender;

  private Status status;

  private LocalDateTime createdDate;

  private List<String> authorities;

  public AuthenticatedMember(MemberAdapter member) {
    this.userId = member.getUserId();
    this.username = member.getUsername();
    this.email = member.getEmail();
    this.mobilePhone = member.getMobilePhone();
    this.name = member.getName();
    this.gender = member.getGender();
    this.status = member.getStatus();
    this.createdDate = member.getCreatedDate();
    this.authorities = member.getAuthorities().stream()
      .map(GrantedAuthority::getAuthority)
      .collect(Collectors.toList());
  }

  public MemberAdapter toUser() {
    Member member = Member.builder()
      .id(this.userId)
      .username(this.username)
      .email(this.email)
      .mobilePhone(this.mobilePhone)
      .name(this.name)
      .gender(this.gender)
      .status(this.status)
      .build();

    List<SimpleGrantedAuthority> authorities = this.authorities.stream()
      .map(SimpleGrantedAuthority::new)
      .collect(Collectors.toList());

    return new MemberAdapter(member, authorities);
  }

}
