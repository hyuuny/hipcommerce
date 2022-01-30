package com.hipcommerce.config.security.service;

import com.hipcommerce.config.security.model.TokenDto;
import com.hipcommerce.config.security.model.Credential;
import com.hipcommerce.members.domain.Member;
import com.hipcommerce.members.dto.MemberDto.Response;
import com.hipcommerce.members.dto.MemberDto.UserWithToken;
import com.hipcommerce.members.port.AuthPort;
import com.hipcommerce.members.service.MemberService;
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
  private final MemberService memberService;

  @Transactional
  public UserWithToken signIn(Credential credential) {
    TokenDto tokenDto = authPort.login(credential);
    Member existingMember = memberService.getMember(credential.getUsername());
    return new UserWithToken(toResponse(existingMember), tokenDto);
  }

  private Response toResponse(Member existingMember) {
    return new Response(existingMember);
  }

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
