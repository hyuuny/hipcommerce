package com.hipcommerce.config.security.service;

import com.hipcommerce.config.security.model.AccessToken;
import com.hipcommerce.members.dto.TokenDto.Request;
import com.hipcommerce.members.port.AuthPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AuthService {

  private final AuthPort authPort;

  @Transactional
  public AccessToken reissue(Request dto) {
    AccessToken accessToken = authPort.reissue(dto);
    return accessToken;
  }

  public AccessToken generateToken(Authentication authentication) {
    return authPort.generateToken(authentication);
  }

}
