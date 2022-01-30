package com.hipcommerce.members.port;

import static org.springframework.util.ObjectUtils.isEmpty;

import com.hipcommerce.common.web.model.HttpStatusMessageException;
import com.hipcommerce.config.security.model.Credential;
import com.hipcommerce.config.security.model.TokenDto;
import com.hipcommerce.config.security.utils.JwtUtil;
import com.hipcommerce.members.domain.RefreshToken;
import com.hipcommerce.members.domain.RefreshTokenRepository;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class AuthPort {

  private final RefreshTokenRepository refreshTokenRepository;
  private final JwtUtil jwtUtil;
  private final AuthenticationManagerBuilder managerBuilder;
  private final RedisTemplate redisTemplate;

  public RefreshToken save(RefreshToken entity) {
    return refreshTokenRepository.save(entity);
  }

  @Transactional
  public TokenDto login(Credential credential) {
    UsernamePasswordAuthenticationToken authenticationToken = credential.toAuthentication();
    Authentication authentication = managerBuilder.getObject().authenticate(authenticationToken);
    TokenDto newToken = jwtUtil.generateToken(authentication);
    generate(authentication.getName(), newToken.getRefreshToken());
    return newToken;
  }

  private void generate(final String key, final String value) {
    save(RefreshToken.builder().tokenKey(key).tokenValue(value).build());
    saveRedisToken(key, value);
  }

  private void saveRedisToken(final String name, final String refreshToken) {
    redisTemplate.opsForValue().set(name, refreshToken);
  }

  @Transactional
  public TokenDto reissue(TokenDto dto) {
    if (!jwtUtil.validateToken(dto.getRefreshToken())) {
      throw new HttpStatusMessageException(HttpStatus.BAD_REQUEST, "refresh.token.notValid");
    }

    Authentication authentication = jwtUtil.getAuthentication(dto.getAccessToken());
    RefreshToken refreshToken = getRefreshToken(authentication.getName());
    verifyRefreshToken(dto.getRefreshToken(), refreshToken.getTokenValue());
    TokenDto newToken = jwtUtil.generateToken(authentication);
    save(refreshToken.updateValue(newToken.getRefreshToken()));
    saveRedisToken(authentication.getName(), newToken.getRefreshToken());

    // 토큰 발급
    return newToken;
  }

  private void verifyRefreshToken(final String refreshToken, final String existingRefreshToken) {
    if (!Objects.equals(refreshToken, existingRefreshToken)) {
      throw new HttpStatusMessageException(HttpStatus.BAD_REQUEST, "member.token.notValid");
    }
  }

  public RefreshToken getRefreshToken(String key) {
    return refreshTokenRepository.findByTokenKey(key).orElseThrow(
        () -> new HttpStatusMessageException(HttpStatus.BAD_REQUEST, "member.logout.complete"));
  }

  @Transactional
  public void logout(TokenDto dto) {
    jwtUtil.validateToken(dto.getAccessToken());
    Authentication authentication = jwtUtil.getAuthentication(dto.getAccessToken());

    if (isEmpty(redisTemplate.opsForValue().get(authentication.getName()))) {
      redisTemplate.delete(authentication.getName());
    }

    saveBlackList(dto.getAccessToken());
  }

  private void saveBlackList(final String accessToken) {
    Long expiration = jwtUtil.getExpiration(accessToken);
    redisTemplate.opsForValue().set(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);
  }

}
