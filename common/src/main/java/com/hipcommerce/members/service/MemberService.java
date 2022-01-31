package com.hipcommerce.members.service;

import com.hipcommerce.members.domain.Member;
import com.hipcommerce.members.dto.MemberDto.ChangePassword;
import com.hipcommerce.members.dto.MemberDto.DetailedSearchCondition;
import com.hipcommerce.members.dto.MemberDto.Response;
import com.hipcommerce.members.dto.MemberDto.Update;
import com.hipcommerce.members.port.MemberPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberService {

  private final MemberPort memberPort;

  public Response getMember(final Long id) {
    Member existingMember = memberPort.getMember(id);
    return toResponse(existingMember);
  }

  public Member getMember(final String username) {
    return memberPort.getMember(username);
  }

  private Response toResponse(Member existingMember) {
    return new Response(existingMember);
  }

  public Page<Response> retrieveMember(DetailedSearchCondition searchCondition, Pageable pageable) {
    Page<Response> members = memberPort.retrieveMember(searchCondition, pageable);
    return members;
  }

  @Transactional
  public Long changePassword(final Long id, ChangePassword dto) {
    return memberPort.changePassword(id, dto);
  }

  @Transactional
  public Response updateMember(final Long id, Update dto) {
    Member updatedMember = memberPort.updateMember(id, dto);
    return toResponse(updatedMember);
  }

  @Transactional
  public void leaveMember(final Long id) {
    memberPort.leave(id);
  }


}
