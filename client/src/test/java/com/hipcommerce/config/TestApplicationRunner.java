package com.hipcommerce.config;


import static com.hipcommerce.DummyData.ADMIN_EMAIL;
import static com.hipcommerce.DummyData.USER_EMAIL;
import static com.hipcommerce.DummyData.anAdmin;
import static com.hipcommerce.DummyData.anUser;

import com.google.common.collect.Sets;
import com.hipcommerce.members.domain.Authority;
import com.hipcommerce.members.domain.Member;
import com.hipcommerce.members.domain.MemberRepository;
import com.hipcommerce.members.dto.MemberDto.SignUpRequest;
import com.hipcommerce.members.service.MemberSignUpService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class TestApplicationRunner implements ApplicationRunner {

  private final MemberRepository memberRepository;
  private final MemberSignUpService memberSignUpService;

  @Override
  public void run(ApplicationArguments args) {
    log.info("TestApplicationRunner start");

    Optional<Member> optionalAdmin = memberRepository.findByUsername(ADMIN_EMAIL);
    if (optionalAdmin.isEmpty()) {
      SignUpRequest admin = anAdmin().build();
      memberSignUpService.signUp(admin, Sets.newHashSet(Authority.USER));
    }

    Optional<Member> optionalUser = memberRepository.findByUsername(USER_EMAIL);
    if (optionalUser.isEmpty()) {
      memberSignUpService.signUp(anUser().build());
    }
  }
}
