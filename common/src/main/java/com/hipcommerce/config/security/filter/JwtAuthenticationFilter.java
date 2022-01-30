package com.hipcommerce.config.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hipcommerce.common.web.model.ErrorResponse;
import com.hipcommerce.config.security.model.Credential;
import com.hipcommerce.config.security.service.AuthService;
import com.hipcommerce.members.dto.MemberDto.Response;
import com.hipcommerce.members.dto.MemberDto.UserWithToken;
import com.hipcommerce.members.service.MemberAdapter;
import java.io.IOException;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

@Slf4j
public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

  private final AuthService authService;
  private final ObjectMapper objectMapper;

  public JwtAuthenticationFilter(
    String defaultFilterProcessesUrl,
    AuthService authService,
    ObjectMapper objectMapper
  ) {
    super(defaultFilterProcessesUrl);
    this.authService = authService;
    this.objectMapper = objectMapper;
  }

  /**
   * post로 온 요청에서 username, password를 받아 토큰 생성 후 AuthenticationManager에게 전달함.
   *
   * @param request
   * @param response
   * @return
   * @throws AuthenticationException
   * @throws IOException
   */
  @Override
  public Authentication attemptAuthentication(
    HttpServletRequest request, HttpServletResponse response
  ) throws AuthenticationException, IOException {
    if (!HttpMethod.POST.name().equals(request.getMethod())) {
      throw new AuthenticationServiceException("not supported method: " + request.getMethod());
    }
    Credential loginCredential =
      Optional.of(objectMapper.readValue(request.getReader(), Credential.class))
        .orElseThrow(() -> new AuthenticationServiceException("invalid_data"));

    log.debug(
      "sign-in request: {}, {}, {}",
      request.getMethod(),
      request.getRequestURI(),
      loginCredential
    );

    UsernamePasswordAuthenticationToken token =
      new UsernamePasswordAuthenticationToken(loginCredential, null);
    return this.getAuthenticationManager().authenticate(token);
  }

  /**
   * 인증(Authentication) 성공
   *
   * @param request
   * @param response
   * @param chain
   * @param authentication
   */
  @Override
  protected void successfulAuthentication(
    HttpServletRequest request,
    HttpServletResponse response,
    FilterChain chain,
    Authentication authentication
  ) {
    try {
      Credential credential = (Credential) authentication.getPrincipal();
      MemberAdapter member = (MemberAdapter) authentication.getDetails();
      responseWrite(response, new UserWithToken(new Response(member),
          authService.generateToken(authentication)));
    } catch (Exception e) {
      log.error("cannot create sign in data: {}", e);
      throw new AuthenticationServiceException(e.getMessage());
    }
  }

  private void responseWrite(HttpServletResponse response, final UserWithToken userWithToken)
    throws Exception {
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setStatus(HttpStatus.OK.value());
    response.setCharacterEncoding("utf-8");
    objectMapper
      .writeValue(response.getWriter(), userWithToken);
  }

  /**
   * 인증(Authentication) 실패
   *
   * @param request
   * @param response
   * @param exception
   * @throws IOException
   */
  @Override
  protected void unsuccessfulAuthentication(
    HttpServletRequest request, HttpServletResponse response, AuthenticationException exception
  ) throws IOException {
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");

    ErrorResponse result = ErrorResponse.builder()
      .status(HttpStatus.UNAUTHORIZED)
      .message(exception.getMessage())
      .code("authentication.failed")
      .build();

    if (UsernameNotFoundException.class.isAssignableFrom(exception.getClass())) {
      result.setCode("user.notFound");
      result.setMessage("User not found");
    }

    objectMapper.writeValue(response.getWriter(), result);
  }
}
