package com.hipcommerce.config.security.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Getter
@NoArgsConstructor
@Schema(description = "로그인 요청 정보")
public class Credential {

  @Schema(description = "이메일", example = "prumwellness@prumwellness.com", required = false)
  private String username;

  @Schema(description = "비밀번호", example = "secret", required = false)
  private String password;

  @Builder
  public Credential(final String username, final String password) {
    this.username = username;
    this.password = password;
  }

  public UsernamePasswordAuthenticationToken toAuthentication() {
    return new UsernamePasswordAuthenticationToken(this.username, this.password);
  }

}
