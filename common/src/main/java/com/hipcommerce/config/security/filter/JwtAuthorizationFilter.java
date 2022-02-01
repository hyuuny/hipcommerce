package com.hipcommerce.config.security.filter;

import static org.springframework.util.ObjectUtils.isEmpty;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hipcommerce.config.security.model.AuthenticatedMember;
import com.hipcommerce.config.security.service.AuthenticationService;
import com.hipcommerce.members.service.MemberAdapter;
import io.jsonwebtoken.Claims;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

  public static final String BEARER_PREFIX = "Bearer ";

  private final AuthenticationService authenticationService;
  private final ObjectMapper objectMapper;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain
  ) throws IOException, ServletException {

    String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (isEmpty(authorizationHeader) || !authorizationHeader.startsWith(BEARER_PREFIX)) {
      filterChain.doFilter(request, response);
      return;
    }

    final String accessToken = authorizationHeader.replace(BEARER_PREFIX, "");
    try {
      Claims claims = authenticationService.verifyAccessToken(accessToken);
      String userData = (String) claims.get("user");
      if (!isEmpty(userData)) {
        AuthenticatedMember authenticatedMember = objectMapper
            .readValue(userData, AuthenticatedMember.class);
        MemberAdapter member = authenticatedMember.toMember();
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(member,
            null, member.getAuthorities());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
      }
    } catch (Exception e) {
      log.error("JwtAuthorization Error", e);
      SecurityContextHolder.clearContext();
    }

    filterChain.doFilter(request, response);
  }

}