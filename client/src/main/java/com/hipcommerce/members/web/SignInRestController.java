package com.hipcommerce.members.web;

import com.hipcommerce.config.security.model.Credential;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "로그인 API")
@RequiredArgsConstructor
@RestController
@RequestMapping(produces = MediaTypes.HAL_JSON_VALUE)
public class SignInRestController {

  @Operation(summary = "로그인")
  @PostMapping("/auth")
  public ResponseEntity<?> signIn(@RequestBody Credential credential) {
    return ResponseEntity.noContent().build();
  }

}
