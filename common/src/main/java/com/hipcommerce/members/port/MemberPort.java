package com.hipcommerce.members.port;

import com.google.common.collect.Sets;
import com.hipcommerce.common.exceptions.UserNotFoundException;
import com.hipcommerce.common.web.model.HttpStatusMessageException;
import com.hipcommerce.config.security.model.AccessToken;
import com.hipcommerce.config.security.model.Credential;
import com.hipcommerce.config.security.provider.TokenProvider;
import com.hipcommerce.members.domain.Authority;
import com.hipcommerce.members.domain.Member;
import com.hipcommerce.members.domain.MemberRepository;
import com.hipcommerce.members.domain.RefreshToken;
import java.util.Set;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MemberPort {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManagerBuilder authenticationManagerBuilder;
  private final TokenProvider tokenProvider;
  private final AuthPort authPort;

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

  @Transactional
  public AccessToken login(Credential credential) {
    // 1. Login ID/PW 를 기반으로 AuthenticationToken 생성
    UsernamePasswordAuthenticationToken authenticationToken = credential.toAuthentication();

    // 2. 실제로 검증 (사용자 비밀번호 체크) 이 이루어지는 부분
    //    authenticate 메서드가 실행이 될 때 CustomUserDetailsService 에서 만들었던 loadUserByUsername 메서드가 실행됨
    Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

    // 3. 인증 정보를 기반으로 JWT 토큰 생성
    AccessToken accessToken = tokenProvider.generateToken(authentication);

    // 4. RefreshToken 저장
    RefreshToken refreshToken = RefreshToken.builder()
        .key(authentication.getName())
        .value(accessToken.getRefreshToken())
        .build();

    authPort.save(refreshToken);

    // 5. 토큰 발급
    return accessToken;
  }

}
