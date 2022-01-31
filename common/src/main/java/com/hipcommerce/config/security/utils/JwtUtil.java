package com.hipcommerce.config.security.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.hipcommerce.config.security.model.AuthenticatedMember;
import com.hipcommerce.config.security.model.TokenDto;
import com.hipcommerce.members.service.MemberAdapter;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtUtil {

  private static final String AUTHORITIES_KEY = "auth";

  @Value("${spring.jwt.issuer}")
  private String issuer;

  @Value("${spring.jwt.expiration.access}")
  private long accessExpiration;

  @Value("${spring.jwt.expiration.refresh}")
  private Duration refreshExpiration;

  private final Key key;

  private final ObjectMapper objectMapper;

  public JwtUtil(
      @Value("${spring.jwt.secret}") String secretKey,
      ObjectMapper objectMapper
  ) {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    this.key = Keys.hmacShaKeyFor(keyBytes);
    this.objectMapper = objectMapper;
  }

  public TokenDto generateToken(Authentication authentication) {
    Map<String, Object> headers = Maps.newHashMap();
    headers.put("typ", "JWT");
    headers.put("alg", "HS256");

    MemberAdapter member = (MemberAdapter) authentication.getDetails();
    Map<String, Object> claims = Maps.newHashMap();
    claims.put("user", writeValueAsString(new AuthenticatedMember(member)));

    // 권한들 가져오기
    String authorities = authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.joining(","));

    // Access Token 생성
    long now = new Date().getTime();
    String accessToken = Jwts.builder()
        .setHeader(headers)
        .setIssuer(issuer)
        .setSubject(authentication.getName())
        .claim(AUTHORITIES_KEY, authorities)
        .setExpiration(new Date(now + accessExpiration))
        .signWith(key, SignatureAlgorithm.HS256)
        .setAudience("user")
        .addClaims(claims)
        .compact();

    // Refresh Token 생성
    String refreshToken = Jwts.builder()
        .setExpiration(new DateTime().plus(refreshExpiration.toMillis()).toDate())
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();

    return TokenDto.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .expireDate(accessExpiration)
        .build();
  }

  private String writeValueAsString(AuthenticatedMember authenticatedMember) {
    try {
      return objectMapper.writeValueAsString(authenticatedMember);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public Authentication getAuthentication(String accessToken) {
    // 토큰 복호화
    Claims claims = parseClaims(accessToken);

    if (claims.get(AUTHORITIES_KEY) == null) {
      throw new RuntimeException("not authority token");
    }

    Collection<? extends GrantedAuthority> authorities =
        Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());

    UserDetails principal = new User(claims.getSubject(), "", authorities);
    return new UsernamePasswordAuthenticationToken(principal, null, authorities);
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      log.error(e.getMessage());
      return false;
    }
  }

  public Claims parseClaims(final String accessToken) {
    try {
      return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
    } catch (ExpiredJwtException e) {
      return e.getClaims();
    }
  }

  public Long getExpiration(String accessToken) {
    Long now = new Date().getTime();
    Date expiration = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody().getExpiration();
    return (expiration.getTime() - now);
  }

}
