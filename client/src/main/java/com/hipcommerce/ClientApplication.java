package com.hipcommerce;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class ClientApplication {

  public static final String APPLICATION_LOCATIONS = "spring.config.location="
      + "classpath:application.yml,"
      + "classpath:application-common.yml";

  public static void main(String[] args) {
    new SpringApplicationBuilder(ClientApplication.class)
        .properties(APPLICATION_LOCATIONS)
        .run(args);
  }

}
