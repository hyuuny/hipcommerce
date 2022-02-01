package com.hipcommerce.common;

import static com.hipcommerce.DummyData.ADMIN_EMAIL;
import static com.hipcommerce.DummyData.USER_EMAIL;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hipcommerce.config.security.model.Credential;
import com.hipcommerce.members.domain.MemberRepository;
import com.hipcommerce.members.dto.MemberDto.UserWithToken;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@ExtendWith(SpringExtension.class)
@MockMvcCustomConfig
@ActiveProfiles("test")
@SpringBootTest(
  webEnvironment = WebEnvironment.RANDOM_PORT,
  properties = "spring.config.location="
      + "classpath:application.yml,"
      + "classpath:application-common.yml,"
      + "classpath:application-test.yml"
)
public abstract class BaseIntegrationTest {

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected ObjectMapper objectMapper;

  @Autowired
  protected ModelMapper modelMapper;

  @Autowired
  protected MemberRepository memberRepository;

  @Autowired
  protected EntityManager em;

  protected String getBearerToken(final String username, final String password) throws Exception {
    Credential credential = Credential.builder()
        .username(username)
        .password(password)
        .build();
    return getBearerToken(credential);
  }

  protected String getBearerToken(Credential credential) throws Exception {
    return "Bearer " + getAccessToken(credential);
  }

  protected String getAccessToken(Credential credential) throws Exception {
    ResultActions perform = this.mockMvc.perform(
        post("/auth")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaTypes.HAL_JSON_VALUE)
            .content(this.objectMapper.writeValueAsString(credential)));

    String responseBody = perform.andReturn().getResponse().getContentAsString();
    return objectMapper.readValue(responseBody, UserWithToken.class).getToken()
        .getAccessToken();
  }

  protected void flushWithClearEntityContext() {
    em.flush();
    em.clear();
  }

  protected void deleteMembers() throws Exception {
    memberRepository.findAll().stream()
        .filter(account -> !account.getUsername().equals(ADMIN_EMAIL))
        .filter(account -> !account.getUsername().equals(USER_EMAIL))
        .forEach(member -> memberRepository.delete(member));
  }

}
