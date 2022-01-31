package com.hipcommerce.config.security.provider;

import com.hipcommerce.config.security.model.Credential;
import com.hipcommerce.config.security.service.AuthenticationService;
import com.hipcommerce.members.service.MemberAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

  private final UserDetailsService userDetailsService;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationService authenticationService;

  /**
   * 인증 처리 로직
   */
  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    if (authentication == null) {
      log.error("An Authentication object was not found in the SecurityContext");
      throw new AuthenticationServiceException(
          "An Authentication object was not found in the SecurityContext");
    }
    Credential credential = (Credential) authentication.getPrincipal();
    String username = credential.getUsername();
    String rawPassword = credential.getPassword();
    MemberAdapter user = (MemberAdapter) userDetailsService.loadUserByUsername(username);

    authenticationService.authenticate(user);

    if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
      throw new BadCredentialsException("Bad credentials");
    }
    if (user.getAuthorities() == null) {
      throw new BadCredentialsException("Unauthorized user");
    }

    UsernamePasswordAuthenticationToken token =
        new UsernamePasswordAuthenticationToken(credential, null, user.getAuthorities());
    token.setDetails(user);
    return token;
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
  }
}
