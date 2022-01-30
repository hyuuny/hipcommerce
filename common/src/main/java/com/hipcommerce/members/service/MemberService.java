package com.hipcommerce.members.service;

import com.hipcommerce.config.security.model.AccessToken;
import com.hipcommerce.config.security.model.Credential;
import com.hipcommerce.config.security.provider.TokenProvider;
import com.hipcommerce.members.domain.Member;
import com.hipcommerce.members.dto.MemberDto.Response;
import com.hipcommerce.members.dto.MemberDto.UserWithToken;
import com.hipcommerce.members.port.MemberPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberService {

  private final MemberPort memberPort;
  private final TokenProvider tokenProvider;
  private final AuthenticationManager authenticationManager;

  public Response getMember(final Long id) {
    Member existingMember = memberPort.getMember(id);
    return toResponse(existingMember);
  }

  public UserWithToken signIn(Credential credential) {
    AccessToken accessToken = memberPort.login(credential);
    Member existingMember = memberPort.getMember(credential.getUsername());
    return new UserWithToken(toResponse(existingMember), accessToken);
  }

  private Response toResponse(Member existingMember) {
    return new Response(existingMember);
  }


}
