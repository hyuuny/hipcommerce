package com.hipcommerce.members.domain;


import static org.springframework.util.ObjectUtils.isEmpty;

import com.google.common.collect.Sets;
import com.hipcommerce.common.jpa.converter.UUIDConverter;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.EqualsAndHashCode.Include;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class Member{

  @Getter
  @AllArgsConstructor
  public enum Status {
    ACTIVE("활성화"),
    LEAVE("탈퇴");

    private final String title;
  }

  @Getter
  @AllArgsConstructor
  public enum Gender {
    MALE("남성"),
    FEMALE("여성");

    private final String title;
  }

  @Id
  @Include
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(unique = true, nullable = false)
  private Long id;

  @Default
  @Convert(converter = UUIDConverter.class)
  @Column(unique = true, nullable = false)
  private UUID uuid = UUID.randomUUID();

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false)
  private String email;

  private String password;

  @Column(nullable = false)
  private String mobilePhone;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Gender gender;

  @Default
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Status status = Status.ACTIVE;

  @ElementCollection
  @Default
  @Enumerated(EnumType.STRING)
  private Set<Authority> authorities = Sets.newHashSet();

  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime createdDate;

  @LastModifiedDate
  private LocalDateTime lastModifiedDate;

//  public Collection<? extends GrantedAuthority> getAuthorities() {
//    return this.authorities.stream()
//        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
//        .collect(Collectors.toSet());
//  }


//  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
//  @Override
//  public String getUsername() {
//    return this.email;
//  }
//
//  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
//  @Override
//  public boolean isAccountNonExpired() {
//    return true;
//  }
//
//  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
//  @Override
//  public boolean isAccountNonLocked() {
//    return true;
//  }
//
//  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
//  @Override
//  public boolean isCredentialsNonExpired() {
//    return true;
//  }
//
//  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
//  @Override
//  public boolean isEnabled() {
//    return true;
//  }

  public boolean isEnabled() {
    return true;
  }

  public boolean isAccountNonExpired() {
    return true;
  }

  public boolean isAccountNonLocked() {
    return true;
  }

  public boolean isCredentialsNonExpired() {
    return true;
  }


  public void addRole(Set<Authority> authorities) {
    this.authorities = authorities;
  }

  public void signUp(
      Set<Authority> authorities,
      PasswordEncoder passwordEncoder
  ) {
    this.addRole(authorities);
    if (!isEmpty(this.password)) {
      this.password = passwordEncoder.encode(this.password);
    }
  }

  public String toGender() {
    return this.gender.toString();
  }

}
