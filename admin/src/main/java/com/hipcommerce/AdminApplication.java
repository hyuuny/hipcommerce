package com.hipcommerce;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class AdminApplication {

  public static final String APPLICATION_LOCATIONS = "spring.config.location="
      + "classpath:application.yml,"
      + "classpath:application-common.yml";

  public static void main(String[] args) {
    new SpringApplicationBuilder(AdminApplication.class)
        .properties(APPLICATION_LOCATIONS)
        .run(args);
  }

}
