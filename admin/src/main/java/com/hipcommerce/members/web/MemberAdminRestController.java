package com.hipcommerce.members.web;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.hipcommerce.members.dto.MemberDto.DetailedSearchCondition;
import com.hipcommerce.members.dto.MemberDto.Response;
import com.hipcommerce.members.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Secured("ROLE_ADMIN")
@Tag(name = "회원 API")
@RequiredArgsConstructor
@RestController
@RequestMapping(path = MemberAdminRestController.REQUEST_URL, produces = MediaTypes.HAL_JSON_VALUE)
public class MemberAdminRestController {

  static final String REQUEST_URL = "/api/v1/members";

  private final MemberService memberService;
  private final MemberResourceAssembler memberResourceAssembler;

  @Operation(summary = "회원 조회 및 검색", description = "정렬: \n"
      + "최신가입순(기본): createdDate,desc")
  @GetMapping
  public ResponseEntity<PagedModel<EntityModel<Response>>> retrieveMember(
      @ParameterObject @Valid DetailedSearchCondition searchCondition,
      @ParameterObject @PageableDefault(sort = "createdDate", direction = Direction.DESC) Pageable pageable,
      PagedResourcesAssembler<Response> pagedResourcesAssembler
  ) {
    Page<Response> page = memberService.retrieveMember(searchCondition, pageable);
    return ResponseEntity
        .ok(pagedResourcesAssembler.toModel(page, memberResourceAssembler));
  }

  @Operation(summary = "회원 상세 조회")
  @GetMapping("/{id}")
  public ResponseEntity<EntityModel<Response>> getMember(@PathVariable final Long id) {
    Response existingMember = memberService.getMember(id);
    return ResponseEntity.ok(memberResourceAssembler.toModel(existingMember));
  }

  @Component
  static class MemberResourceAssembler implements
      RepresentationModelAssembler<Response, EntityModel<Response>> {

    @Override
    public EntityModel<Response> toModel(Response entity) {
      return EntityModel.of(
          entity,
          linkTo(methodOn(MemberAdminRestController.class)
              .getMember(entity.getId()))
              .withSelfRel()
      );
    }
  }
}
