package com.hipcommerce.members.web;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.hipcommerce.config.security.annotations.CurrentUserId;
import com.hipcommerce.config.security.model.TokenDto;
import com.hipcommerce.config.security.service.AuthService;
import com.hipcommerce.members.dto.MemberDto.ChangePassword;
import com.hipcommerce.members.dto.MemberDto.Response;
import com.hipcommerce.members.dto.MemberDto.SignUpRequest;
import com.hipcommerce.members.dto.MemberDto.Update;
import com.hipcommerce.members.service.MemberService;
import com.hipcommerce.members.service.MemberSignUpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.net.URISyntaxException;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "회원 API")
@RequiredArgsConstructor
@RestController
@RequestMapping(path = MemberRestController.REQUEST_URL, produces = MediaTypes.HAL_JSON_VALUE)
public class MemberRestController {

  static final String REQUEST_URL = "/api/v1/members";

  private final MemberSignUpService memberSignUpService;
  private final MemberService memberService;
  private final AuthService authService;
  private final MemberResourceAssembler memberResourceAssembler;

  @Operation(summary = "회원가입")
  @PostMapping
  public ResponseEntity<EntityModel<Response>> signUp(
      @RequestBody @Valid SignUpRequest dto
  ) throws URISyntaxException {
    final Long signedUpUserId = memberSignUpService.signUp(dto);
    EntityModel<Response> resource = memberResourceAssembler.toModel(
        memberService.getMember(signedUpUserId)
    );

    return ResponseEntity
        .created(new URI(resource.getRequiredLink(IanaLinkRelations.SELF).getHref()))
        .body(resource);
  }

  @Operation(summary = "회원 상세 조회")
  @PreAuthorize("isAuthenticated() and (#id == #currentUserId)")
  @GetMapping("/{id}")
  public ResponseEntity<EntityModel<Response>> getMember(
      @PathVariable final Long id,
      @CurrentUserId final Long currentUserId
  ) {
    Response existingMember = memberService.getMember(currentUserId);
    return ResponseEntity.ok(memberResourceAssembler.toModel(existingMember));
  }

  @Operation(summary = "리프레시 토큰 발급")
  @PostMapping("/reissue")
  public ResponseEntity<TokenDto> reissue(@RequestBody TokenDto dto) {
    TokenDto tokenDto = authService.reissue(dto);
    return ResponseEntity.ok(tokenDto);
  }

  @Operation(summary = "로그아웃")
  @PostMapping("/logout")
  public ResponseEntity<?> logout(@RequestBody TokenDto dto) {
    authService.logout(dto);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "비밀번호 변경")
  @PreAuthorize("isAuthenticated() and (#id == #currentUserId)")
  @PostMapping("/{id}/change-password")
  public ResponseEntity<EntityModel<Response>> resetPassword(
      @PathVariable final Long id,
      @RequestBody @Valid ChangePassword dto,
      @CurrentUserId final Long currentUserId
  ) {
    Long userId = memberService.changePassword(id, dto);
    return ResponseEntity.ok(memberResourceAssembler.toModel(memberService.getMember(userId)));
  }

  @Operation(summary = "회원정보 수정")
  @PreAuthorize("isAuthenticated() and (#id == #currentUserId)")
  @PutMapping("/{id}")
  public ResponseEntity<EntityModel<Response>> updateMember(
      @PathVariable final Long id,
      @RequestBody @Valid Update dto,
      @CurrentUserId final Long currentUserId
  ) {
    Response updatedMember = memberService.updateMember(id, dto);
    return ResponseEntity.ok(memberResourceAssembler.toModel(updatedMember));
  }

  @Operation(summary = "회원탈퇴")
  @PreAuthorize("isAuthenticated() and (#id == #currentUserId)")
  @DeleteMapping("/{id}")
  public ResponseEntity<?> leaveMember(
      @PathVariable final Long id,
      @CurrentUserId final Long currentUserId
  ) {
    memberService.leaveMember(id);
    return ResponseEntity.noContent().build();
  }

  @Component
  static class MemberResourceAssembler implements
      RepresentationModelAssembler<Response, EntityModel<Response>> {

    @Override
    public EntityModel<Response> toModel(Response entity) {
      return EntityModel.of(
          entity,
          linkTo(methodOn(MemberRestController.class)
              .getMember(entity.getId(), entity.getId()))
              .withSelfRel()
      );
    }
  }

}
