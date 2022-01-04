package com.hipcommerce.common;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.SpringDocUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.stereotype.Component;

@Component
public class OpenApiConfig {

  static {
    SpringDocUtils.getConfig().addRequestWrapperToIgnore(PagedResourcesAssembler.class);
  }

  @Bean
  public OpenAPI openAPI(@Value("${springdoc.version}") String appVersion) {
    final var title = "hipcommerce API";
    final var description = "Hyuuny의 hipcommerce Open API 입니다.";
    final var termsOfService = "http://localhost:8080";

    var info = new Info().title(title).version(appVersion)
      .description(description)
      .termsOfService(termsOfService)
      .contact(new Contact().name("hipcommerce").url("").email("shyune@knou.ac.kr"))
      .license(new License().name("Apach License Version 2.0")
        .url("https://www.apache.org/licenses/LICENSE-2.0"));

    return new OpenAPI()
      .info(info);
  }

}
