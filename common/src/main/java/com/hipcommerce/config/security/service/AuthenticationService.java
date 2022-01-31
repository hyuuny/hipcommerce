package com.hipcommerce.config.security.service;

import com.hipcommerce.config.security.model.TokenDto;
import com.hipcommerce.config.security.utils.JwtUtil;
import com.hipcommerce.members.service.MemberAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AuthenticationService {

  private final JwtUtil jwtUtil;

  public void authenticate(MemberAdapter user) {
    if (!user.isEnabled()) {
      throw new BadCredentialsException("Unauthorized user");
    }
  }

  public TokenDto generateAccessToken(Authentication authentication) {
    return new TokenDto(jwtUtil.generateToken(authentication));
  }

}
