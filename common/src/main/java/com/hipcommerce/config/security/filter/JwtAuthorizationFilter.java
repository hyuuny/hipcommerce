package com.hipcommerce.config.security.filter;

import static org.springframework.util.ObjectUtils.isEmpty;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hipcommerce.config.security.model.AuthenticatedMember;
import com.hipcommerce.config.security.utils.JwtUtil;
import com.hipcommerce.members.service.MemberAdapter;
import io.jsonwebtoken.Claims;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

  public static final String AUTHORIZATION_HEADER = "Authorization";
  public static final String BEARER_PREFIX = "Bearer ";

  private final JwtUtil jwtUtil;
  private final ObjectMapper objectMapper;

  // 실제 필터링 로직은 doFilterInternal 에 들어감
  // JWT 토큰의 인증 정보를 현재 쓰레드의 SecurityContext 에 저장하는 역할 수행
  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain
  ) throws IOException, ServletException {

    // 1. Request Header 에서 토큰을 꺼냄
    String jwt = resolveToken(request);

    // 2. validateToken 으로 토큰 유효성 검사
    if (StringUtils.hasText(jwt) && jwtUtil.validateToken(jwt)) {
      Claims claims = jwtUtil.parseClaims(jwt);
      String userData = (String) claims.get("user");
      if (!isEmpty(userData)) {
        AuthenticatedMember authenticatedMember = getReadValue(userData);
        MemberAdapter user = authenticatedMember.toUser();
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user,
            null, user.getAuthorities());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
      }

    }

    filterChain.doFilter(request, response);
  }

  private AuthenticatedMember getReadValue(String userData) {
    try {
      return objectMapper.readValue(userData, AuthenticatedMember.class);
    } catch (JsonProcessingException e) {
      log.error("JwtAuthorizationFilter Error={}", e.getMessage());
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  // Request Header 에서 토큰 정보를 꺼내오기
  private String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
      return bearerToken.substring(7);
    }
    return null;
  }

}