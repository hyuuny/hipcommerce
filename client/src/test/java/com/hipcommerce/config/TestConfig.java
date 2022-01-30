package com.hipcommerce.config;

import com.hipcommerce.members.domain.MemberRepository;
import com.hipcommerce.members.service.MemberSignUpService;
import java.time.Duration;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {

  @Bean
  public RestTemplateBuilder restTemplateBuilder() {
    return new RestTemplateBuilder().setConnectTimeout(Duration.ofSeconds(1))
      .setReadTimeout(Duration.ofSeconds(1));
  }

  @Bean
  public ApplicationRunner applicationRunner(
    MemberRepository memberRepository,
    MemberSignUpService memberSignUpService
  ) {
    return new TestApplicationRunner(
      memberRepository,
      memberSignUpService
    );
  }

}
