package com.hipcommerce.config.security.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.hipcommerce.config.security.model.AuthenticatedMember;
import com.hipcommerce.members.service.MemberAdapter;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.security.Key;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtUtil {

  @Value("${spring.jwt.issuer}")
  private String issuer;

  @Value("${spring.jwt.expiration.access}")
  private Duration accessExpiration;

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

  public String generateToken(Authentication authentication) {
    try {
      Map<String, Object> headers = Maps.newHashMap();
      headers.put("typ", "JWT");
      headers.put("alg", "HS256");

      MemberAdapter member = (MemberAdapter) authentication.getDetails();
      Map<String, Object> claims = Maps.newHashMap();
      claims.put("user", writeValueAsString(new AuthenticatedMember(member)));
      claims.put("scope", Type.ACCESS.text);

      DateTime issuedAt = new DateTime();
      return Jwts.builder()
          .setHeader(headers)
          .setIssuer(issuer)
          .setSubject(authentication.getName())
          .setAudience("user")
          .setExpiration(issuedAt.plus(accessExpiration.toMillis()).toDate())
          .setIssuedAt(issuedAt.toDate())
          .setId(UUID.randomUUID().toString())
          .addClaims(claims)
          .signWith(key, SignatureAlgorithm.HS256)
          .compact();
    } catch (Exception e) {
      log.error("jwt create error", e);
      throw new RuntimeException(e);
    }
  }

  private String writeValueAsString(AuthenticatedMember authenticatedMember) {
    try {
      return objectMapper.writeValueAsString(authenticatedMember);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public Jws<Claims> verifyAccessToken(@NotEmpty String jwt)
      throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException,
      SignatureException, IllegalArgumentException {
    return verifyToken(jwt, Type.ACCESS);
  }

  private Jws<Claims> verifyToken(@NotEmpty String jwt, Type type)
      throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException,
      SignatureException, IllegalArgumentException {
    // 토큰 검사
    Jws<Claims> claims = Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(jwt);

    String scopeValue =
        Optional.ofNullable((String) claims.getBody().get("scope"))
            .orElseThrow(() -> new MalformedJwtException("not allowed token"));

    log.debug("jwt verify {}: {}", scopeValue, claims);

    // 토큰 유형 검사
    if (!Objects.equals(scopeValue, type.text)) {
      throw new MalformedJwtException("not allowed token");
    }
    return claims;
  }

  public enum Type {
    ACCESS("access");

    private final String text;

    /**
     * @param text
     */
    Type(final String text) {
      this.text = text;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
      return text;
    }
  }

}
