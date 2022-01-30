package com.hipcommerce.members.service;

import com.hipcommerce.members.domain.Authority;
import com.hipcommerce.members.domain.Member;
import com.hipcommerce.members.dto.MemberDto.SignUpRequest;
import com.hipcommerce.members.port.MemberPort;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberSignUpService {

  private final MemberPort memberPort;

  @Transactional
  public Long signUp(SignUpRequest dto) {
    Member newMember = dto.toEntity();
    return memberPort.signUp(newMember).getId();
  }

  @Transactional
  public Long signUp(SignUpRequest dto, Set<Authority> roles) {
    Member newMember = dto.toEntity();
    return memberPort.signUp(newMember, roles).getId();
  }


}
