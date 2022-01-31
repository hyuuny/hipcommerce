package com.hipcommerce.members.port;

import com.google.common.collect.Sets;
import com.hipcommerce.common.exceptions.UserNotFoundException;
import com.hipcommerce.common.web.model.HttpStatusMessageException;
import com.hipcommerce.members.domain.Authority;
import com.hipcommerce.members.domain.Member;
import com.hipcommerce.members.domain.MemberRepository;
import com.hipcommerce.members.dto.MemberDto.ChangePassword;
import com.hipcommerce.members.dto.MemberDto.DetailedSearchCondition;
import com.hipcommerce.members.dto.MemberDto.Response;
import com.hipcommerce.members.dto.MemberDto.Update;
import com.hipcommerce.members.dto.MemberSearchDto;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MemberPort {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;

  public Member signUp(Member newMember) {
    return signUp(newMember, Sets.newHashSet(Authority.USER));
  }

  public Member signUp(Member newMember, Set<Authority> authority) {
    newMember.signUp(authority, passwordEncoder);
    return memberRepository.save(newMember);
  }

  public Member getMember(final Long id) {
    return memberRepository.findById(id).orElseThrow(() -> new UserNotFoundException("ID: " + id));
  }

  public Member getMember(final String email) {
    return memberRepository.findByEmail(email).orElseThrow(
        () -> new HttpStatusMessageException(HttpStatus.BAD_REQUEST, "member.email.notFound", email)
    );
  }

  public Page<Response> retrieveMember(DetailedSearchCondition searchCondition, Pageable pageable) {
    Page<MemberSearchDto> pages = memberRepository.retrieveMember(searchCondition, pageable);
    List<Response> members = toResponses(pages);
    return new PageImpl<>(members, pageable, pages.getTotalElements());
  }

  private List<Response> toResponses(Page<MemberSearchDto> pages) {
    return pages.getContent().stream()
        .map(Response::new)
        .collect(Collectors.toList());
  }

  private String passwordEncode(final String password) {
    return passwordEncoder.encode(password);
  }

  public Long changePassword(final Long id, ChangePassword dto) {
    Member existingMember = getMember(id);
    existingMember.changePassword(passwordEncode(dto.getPassword()));
    return existingMember.getId();
  }

  public Member updateMember(final Long id, Update dto) {
    Member existingMember = getMember(id);
    dto.update(existingMember);
    return existingMember;
  }

  public void leave(final Long id) {
    Member existingMember = getMember(id);
    existingMember.leave();
  }

}
