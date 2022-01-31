package com.hipcommerce.config.security.service;

import com.hipcommerce.config.security.model.TokenDto;
import com.hipcommerce.members.port.AuthPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AuthService {

  private final AuthPort authPort;

  @Transactional
  public TokenDto reissue(TokenDto dto) {
    TokenDto tokenDto = authPort.reissue(dto);
    return tokenDto;
  }

  @Transactional
  public void logout(TokenDto dto) {
    authPort.logout(dto);
  }

}
