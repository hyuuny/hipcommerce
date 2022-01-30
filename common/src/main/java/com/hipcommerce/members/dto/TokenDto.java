package com.hipcommerce.members.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class TokenDto {

  @Getter
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @AllArgsConstructor
  @Builder
  @Schema(description = "AccessToken")
  @JsonInclude(Include.NON_EMPTY)
  public static class Request {

    @Schema(description = "jwt 토큰", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzaHl1bmVAa25vdS5hYy5rciIsIm")
    private String accessToken;

    @Schema(description = "리프레시 토큰", example = "eyJhbGciOiJIUzUxMiJ9.eyJleHAiOjE2NDM5OTIyNDJ9.V-M6zSwy-KNYwpHhPhp-w8pjLW4Xvw2j")
    private String refreshToken;

  }

}
