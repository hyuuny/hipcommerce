package com.hipcommerce.members.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hipcommerce.common.BaseIntegrationTest;
import com.hipcommerce.config.security.model.Credential;
import com.hipcommerce.members.domain.Member.Gender;
import com.hipcommerce.members.domain.MemberRepository;
import com.hipcommerce.members.dto.MemberDto.SignUpRequest;
import com.hipcommerce.members.service.MemberService;
import com.hipcommerce.members.service.MemberSignUpService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@Slf4j
class MemberRestControllerTest extends BaseIntegrationTest {

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private MemberSignUpService memberSignUpService;

  @Autowired
  private MemberService memberService;

  @BeforeEach
  void setUp() {
  }

  @AfterEach
  void tearDown() {
    log.info("MemberRestControllerTest.deleteAll");
    memberRepository.deleteAll();
  }

  @DisplayName("회원가입 성공")
  @Test
  void signUp() throws Exception {
    SignUpRequest signUpRequest = SignUpRequest.builder()
        .email("shyune@knou.ac.kr")
        .password("12341234")
        .mobilePhone("01012341234")
        .name("김성현")
        .gender(Gender.MALE)
        .build();

    this.mockMvc.perform(post(MemberRestController.REQUEST_URL)
            .content(this.objectMapper.writeValueAsString(signUpRequest))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaTypes.HAL_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isCreated())
    ;
  }

  @DisplayName("회원 정보 조회")
  @Test
  void getMember() throws Exception {
    SignUpRequest signUpRequest = SignUpRequest.builder()
        .email("shyune@knou.ac.kr")
        .password("12341234")
        .mobilePhone("01012341234")
        .name("김성현")
        .gender(Gender.MALE)
        .build();

    Long signedId = memberSignUpService.signUp(signUpRequest);

    this.mockMvc.perform(get(MemberRestController.REQUEST_URL + "/{id}", signedId)
            .header(
                HttpHeaders.AUTHORIZATION,
                this.getBearerToken(signUpRequest.getEmail(), signUpRequest.getPassword()))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaTypes.HAL_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
    ;
  }


  @DisplayName("토큰")
  @Test
  void getToken() throws Exception {
    SignUpRequest signUpRequest = SignUpRequest.builder()
        .email("shyune@knou.ac.kr")
        .password("12341234")
        .mobilePhone("01012341234")
        .name("김성현")
        .gender(Gender.MALE)
        .build();

    Long savedSignUpId = memberSignUpService.signUp(signUpRequest);
    Credential credential = Credential.builder()
        .username(signUpRequest.getEmail())
        .password(signUpRequest.getPassword())
        .build();

    this.mockMvc.perform(post("/auth")
            .content(this.objectMapper.writeValueAsString(credential))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaTypes.HAL_JSON_VALUE))
        .andDo(print())
    ;
  }
}