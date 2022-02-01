package com.hipcommerce.members.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.hipcommerce.config.security.model.AccessToken;
import com.hipcommerce.members.domain.Member;
import com.hipcommerce.members.domain.Member.Gender;
import com.hipcommerce.members.domain.Member.Status;
import com.hipcommerce.members.service.MemberAdapter;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.server.core.Relation;

public class MemberDto {

  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  @Schema(name = "MemberDto.SignUpRequest", description = "회원가입 요청")
  public static class SignUpRequest {

    @NotNull
    @Schema(description = "이메일", example = "shyune@knou.ac.kr", required = true)
    private String email;

    @Schema(description = "비밀번호", example = "secret")
    private String password;

    @NotNull
    @Schema(description = "휴대전화", example = "01012341234", required = true)
    private String mobilePhone;

    @NotNull
    @Schema(description = "이름", example = "김성현", required = true)
    private String name;

    @NotNull
    @Schema(description = "성별", example = "MALE", required = true)
    private Gender gender;

    public Member toEntity() {
      return Member.builder()
          .username(this.email)
          .email(this.email)
          .password(this.password)
          .mobilePhone(this.mobilePhone)
          .name(this.name)
          .gender(this.gender)
          .build();
    }

  }

  @Getter
  @NoArgsConstructor
  @JsonInclude(Include.NON_EMPTY)
  @Schema(name = "MemberDto.UserWithToken", description = "회원정보 및 토큰")
  public static class UserWithToken {

    @Schema(description = "회원정보", required = true)
    private Response user;

    @Schema(description = "인증토큰", required = true)
    private AccessToken token;

    public UserWithToken(Response user, AccessToken token) {
      this.user = user;
      this.token = token;
    }

  }

  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  @Schema(name = "MemberDto.Update", description = "회원정보 수정")
  public static class Update {

    @NotNull
    @Schema(description = "휴대전화", example = "01012341234", required = true)
    private String mobilePhone;

    @NotNull
    @Schema(description = "이름", example = "김성현", required = true)
    private String name;

    @NotNull
    @Schema(description = "성별", example = "MALE", required = true)
    private Gender gender;

    public void update(Member entity) {
      entity.changeMobilePhone(this.mobilePhone);
      entity.changeName(this.name);
      entity.changeGender(this.gender);
    }

  }

  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  @Schema(name = "MemberDto.ResetPassword", description = "비밀번호 수정")
  public static class ChangePassword {

    @NotNull
    @Schema(description = "비밀번호", example = "secret")
    private String password;

    public void update(Member entity) {
      entity.changePassword(this.password);
    }

  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Relation(collectionRelation = "members")
  @JsonInclude(Include.NON_EMPTY)
  @Schema(name = "MemberDto.Response", description = "회원")
  public static class Response {

    @Schema(description = "회원 ID", example = "1", required = true)
    private Long id;

    @Schema(description = "회원 UUID", required = true)
    private UUID uuid;

    @Schema(description = "회원 식별 값, 이메일과 동일", example = "shyune@knou.ac.kr", required = true)
    private String username;

    @Schema(description = "이메일", example = "shyune@knou.ac.kr", required = true)
    private String email;

    @Schema(description = "휴대전화", example = "01012341234", required = true)
    private String mobilePhone;

    @Schema(description = "이름", example = "김성현", required = true)
    private String name;

    @Schema(description = "성별", example = "MALE", required = true)
    private String gender;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Schema(description = "등록일", example = "2022-01-11T13:16:32.139065", required = false)
    private LocalDateTime createdDate;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Schema(description = "수정일", example = "2022-01-12T15:42:06.139065", required = false)
    private LocalDateTime lastModifiedDate;

    public Response(Member entity) {
      this.id = entity.getId();
      this.uuid = entity.getUuid();
      this.username = entity.getUsername();
      this.email = entity.getEmail();
      this.mobilePhone = entity.getMobilePhone();
      this.name = entity.getName();
      this.gender = entity.toGender();
      this.createdDate = entity.getCreatedDate();
      this.lastModifiedDate = entity.getLastModifiedDate();
    }

    public Response(MemberAdapter entity) {
      this.id = entity.getUserId();
      this.uuid = entity.getUuid();
      this.username = entity.getUsername();
      this.email = entity.getEmail();
      this.mobilePhone = entity.getMobilePhone();
      this.name = entity.getName();
      this.gender = entity.toGender();
      this.createdDate = entity.getCreatedDate();
      this.lastModifiedDate = entity.getLastModifiedDate();
    }

    public Response(MemberSearchDto entity) {
      this.id = entity.getId();
      this.uuid = entity.getUuId();
      this.username = entity.getUsername();
      this.email = entity.getEmail();
      this.mobilePhone = entity.getMobilePhone();
      this.name = entity.getName();
      this.gender = entity.toGender();
      this.createdDate = entity.getCreatedDate();
      this.lastModifiedDate = entity.getLastModifiedDate();
    }
  }

  @Setter
  @Getter
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @Builder
  @Schema(description = "회원 상세검색조건")
  public static class DetailedSearchCondition {

    @Schema(description = "회원상태", example = "ACTIVE", required = false)
    private Status status;

    @Schema(description = "이메일", example = "shyune@knou.ac.kr", required = false)
    private String email;

    @Schema(description = "휴대전화", example = "01012341234", required = false)
    private String mobilePhone;

    @Schema(description = "이름", example = "김성현", required = false)
    private String name;

    @Schema(description = "성별", example = "MALE", required = false)
    private Gender gender;

  }


}
