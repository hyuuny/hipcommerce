package com.hipcommerce.config.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hipcommerce.common.web.model.ErrorResponse;
import com.hipcommerce.config.security.model.Credential;
import com.hipcommerce.config.security.service.AuthenticationService;
import com.hipcommerce.members.dto.MemberDto.Response;
import com.hipcommerce.members.dto.MemberDto.UserWithToken;
import com.hipcommerce.members.port.AuthPort;
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

  private final AuthenticationService authenticationService;
  private final ObjectMapper objectMapper;
  private final AuthPort authPort;

  public JwtAuthenticationFilter(
      String defaultFilterProcessesUrl,
      AuthenticationService authenticationService,
      ObjectMapper objectMapper,
      AuthPort authPort
  ) {
    super(defaultFilterProcessesUrl);
    this.authenticationService = authenticationService;
    this.objectMapper = objectMapper;
    this.authPort = authPort;
  }

  @Override
  public Authentication attemptAuthentication(
      HttpServletRequest request,
      HttpServletResponse response
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
      UserWithToken userWithToken = new UserWithToken(new Response(member),
          authenticationService.generateAccessToken(authentication));
      responseWrite(response, userWithToken);
      authPort.login(userWithToken);
    } catch (Exception e) {
      log.error("cannot create sign in data: {}", e);
      throw new AuthenticationServiceException(e.getMessage());
    }
  }

  private void responseWrite(
      HttpServletResponse response,
      final UserWithToken userWithToken
  ) throws Exception {
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setStatus(HttpStatus.OK.value());
    response.setCharacterEncoding("utf-8");
    objectMapper.writeValue(response.getWriter(), userWithToken);
  }

  /**
   * 인증(Authentication) 실패
   */
  @Override
  protected void unsuccessfulAuthentication(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException exception
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
      result.setCode("member.notFound");
      result.setMessage("User not found");
    }

    objectMapper.writeValue(response.getWriter(), result);
  }
}
