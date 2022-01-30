package com.hipcommerce.members.port;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.hipcommerce.common.web.model.HttpStatusMessageException;
import com.hipcommerce.config.security.model.AccessToken;
import com.hipcommerce.config.security.model.AuthenticatedMember;
import com.hipcommerce.config.security.provider.TokenProvider;
import com.hipcommerce.members.domain.RefreshToken;
import com.hipcommerce.members.domain.RefreshTokenRepository;
import com.hipcommerce.members.dto.TokenDto.Request;
import com.hipcommerce.members.service.MemberAdapter;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class AuthPort {

  private final RefreshTokenRepository refreshTokenRepository;
  private final TokenProvider tokenProvider;
  private final ObjectMapper objectMapper;

  public RefreshToken save(RefreshToken entity) {
    return refreshTokenRepository.save(entity);
  }

  public RefreshToken getRefreshToken(String key) {
    return refreshTokenRepository.findByKey(key).orElseThrow(
        () -> new HttpStatusMessageException(HttpStatus.BAD_REQUEST, "member.logout.complete"));
  }

  @Transactional
  public AccessToken reissue(Request tokenRequest) {
    // 1. Refresh Token 검증
    if (!tokenProvider.validateToken(tokenRequest.getRefreshToken())) {
      throw new HttpStatusMessageException(HttpStatus.BAD_REQUEST, "refresh.token.notValid");
    }

    // 2. Access Token 에서 Member ID 가져오기
    Authentication authentication = tokenProvider.getAuthentication(tokenRequest.getAccessToken());

    // 3. 저장소에서 Member ID 를 기반으로 Refresh Token 값 가져옴
    RefreshToken refreshToken = getRefreshToken(authentication.getName());

    // 4. Refresh Token 일치하는지 검사
    if (!refreshToken.getValue().equals(tokenRequest.getRefreshToken())) {
      throw new HttpStatusMessageException(HttpStatus.BAD_REQUEST, "member.token.notValid");
    }

    // 5. 새로운 토큰 생성
    AccessToken accessToken = tokenProvider.generateToken(authentication);

    // 6. 저장소 정보 업데이트
    RefreshToken newRefreshToken = refreshToken.updateValue(accessToken.getRefreshToken());
    refreshTokenRepository.save(newRefreshToken);

    // 토큰 발급
    return accessToken;
  }

  public AccessToken generateToken(Authentication authentication) {
//    Map<String, Object> claims = Maps.newHashMap();
//    claims.put("user", writeValueAsString(new AuthenticatedMember(member)));
    return new AccessToken(tokenProvider.generateToken(authentication));
  }
//
//  private String writeValueAsString(AuthenticatedMember authenticatedMember) {
//    try {
//      return objectMapper.writeValueAsString(authenticatedMember);
//    } catch (JsonProcessingException e) {
//      throw new RuntimeException(e);
//    }
//  }

}
