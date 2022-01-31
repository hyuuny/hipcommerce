package com.hipcommerce.config.security.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
@Schema(description = "AccessToken")
@JsonInclude(Include.NON_EMPTY)
public class TokenDto {

  @JsonProperty("access_token")
  private String accessToken;

  @JsonProperty("refresh_token")
  private String refreshToken;

  @JsonProperty("expired_date")
  private Long expireDate;

  public TokenDto(TokenDto entity) {
    this.accessToken = entity.getAccessToken();
    this.refreshToken = entity.getRefreshToken();
    this.expireDate = entity.getExpireDate();
  }

}
