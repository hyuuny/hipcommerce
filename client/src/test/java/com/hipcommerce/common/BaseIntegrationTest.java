package com.hipcommerce.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

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
  protected EntityManager em;


  protected void flushWithClearEntityContext() {
    em.flush();
    em.clear();
  }

}
