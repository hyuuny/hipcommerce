package com.hipcommerce.members.web;

import static com.hipcommerce.DummyData.aSignUp;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hipcommerce.common.BaseIntegrationTest;
import com.hipcommerce.config.security.model.Credential;
import com.hipcommerce.config.security.model.TokenDto;
import com.hipcommerce.config.security.service.AuthService;
import com.hipcommerce.members.domain.Member.Gender;
import com.hipcommerce.members.domain.MemberRepository;
import com.hipcommerce.members.domain.RefreshToken;
import com.hipcommerce.members.domain.RefreshTokenRepository;
import com.hipcommerce.members.dto.MemberDto.ChangePassword;
import com.hipcommerce.members.dto.MemberDto.SignUpRequest;
import com.hipcommerce.members.dto.MemberDto.Update;
import com.hipcommerce.members.dto.MemberDto.UserWithToken;
import com.hipcommerce.members.service.MemberService;
import com.hipcommerce.members.service.MemberSignUpService;
import java.util.Optional;
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

  @Autowired
  private AuthService authService;

  @Autowired
  private RefreshTokenRepository refreshTokenRepository;

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
    SignUpRequest signUpRequest = aSignUp().build();

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

    SignUpRequest signUpRequest = aSignUp().build();
    Long signedId = memberSignUpService.signUp(signUpRequest);

    this.mockMvc.perform(get(MemberRestController.REQUEST_URL + "/{id}", signedId)
            .header(HttpHeaders.AUTHORIZATION,
                this.getBearerToken(signUpRequest.getEmail(), signUpRequest.getPassword()))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaTypes.HAL_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.email").exists())
        .andExpect(jsonPath("$.email").value(aSignUp().build().getEmail()))
        .andExpect(jsonPath("$.mobilePhone").exists())
        .andExpect(jsonPath("$.mobilePhone").value(aSignUp().build().getMobilePhone()))
        .andExpect(jsonPath("$.name").exists())
        .andExpect(jsonPath("$.name").value(aSignUp().build().getName()))
        .andExpect(jsonPath("$.gender").exists())
        .andExpect(jsonPath("$.gender").value(aSignUp().build().getGender().toString()))
    ;
  }

  @DisplayName("로그인")
  @Test
  void login() throws Exception {
    SignUpRequest signUpRequest = aSignUp().build();
    memberSignUpService.signUp(signUpRequest);

    Credential credential = Credential.builder()
        .username(signUpRequest.getEmail())
        .password(signUpRequest.getPassword())
        .build();

    this.mockMvc.perform(post("/auth")
            .content(this.objectMapper.writeValueAsString(credential))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaTypes.HAL_JSON_VALUE))
        .andDo(print())
        .andExpect(jsonPath("$.user").exists())
        .andExpect(jsonPath("$.token").exists())
    ;
  }

  @DisplayName("비밀번호 변경")
  @Test
  void resetPassword() throws Exception {
    SignUpRequest signUpRequest = SignUpRequest.builder()
        .email("shyune@knou.ac.kr")
        .password("12341234")
        .mobilePhone("01012341234")
        .name("김성현")
        .gender(Gender.MALE)
        .build();
    Long signedId = memberSignUpService.signUp(signUpRequest);

    ChangePassword changePassword = ChangePassword.builder()
        .password("45674567")
        .build();

    this.mockMvc.perform(post(MemberRestController.REQUEST_URL + "/{id}/change-password", signedId)
            .header(HttpHeaders.AUTHORIZATION,
                getBearerToken(signUpRequest.getEmail(), signUpRequest.getPassword()))
            .content(this.objectMapper.writeValueAsString(changePassword))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaTypes.HAL_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.email").exists())
        .andExpect(jsonPath("$.mobilePhone").exists())
        .andExpect(jsonPath("$.name").exists())
        .andExpect(jsonPath("$.gender").exists())
    ;

  }

  @DisplayName("회원정보 수정")
  @Test
  void updateMember() throws Exception {

    SignUpRequest signUpRequest = aSignUp().build();
    Long signedId = memberSignUpService.signUp(signUpRequest);

    Update update = Update.builder()
        .name("이름 바꿀래")
        .mobilePhone("01045652152")
        .gender(Gender.FEMALE)
        .build();

    this.mockMvc.perform(put(MemberRestController.REQUEST_URL + "/{id}", signedId)
            .header(HttpHeaders.AUTHORIZATION,
                getBearerToken(signUpRequest.getEmail(), signUpRequest.getPassword()))
            .content(this.objectMapper.writeValueAsString(update))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaTypes.HAL_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.email").exists())
        .andExpect(jsonPath("$.mobilePhone").exists())
        .andExpect(jsonPath("$.mobilePhone").value(update.getMobilePhone()))
        .andExpect(jsonPath("$.name").exists())
        .andExpect(jsonPath("$.name").value(update.getName()))
        .andExpect(jsonPath("$.gender").exists())
        .andExpect(jsonPath("$.gender").value(update.getGender().toString()))
        ;
  }

  @DisplayName("회원탈퇴")
  @Test
  void leaveMember() throws Exception {
    SignUpRequest signUpRequest = aSignUp().build();
    Long signedId = memberSignUpService.signUp(signUpRequest);

    this.mockMvc.perform(delete(MemberRestController.REQUEST_URL + "/{id}", signedId)
        .header(HttpHeaders.AUTHORIZATION,
            getBearerToken(signUpRequest.getEmail(), signUpRequest.getPassword()))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaTypes.HAL_JSON_VALUE))
        .andDo(print())
        .andExpect(status().isNoContent())
        ;
  }
}