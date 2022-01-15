package com.hipcommerce.orders.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Embeddable
@Schema(description = "주문자 정보")
public class Orderer {

  @NotEmpty
  @Column(name = "orderer_name")
  @Schema(description = "주문자 이름", example = "나구매", required = true)
  private String name;

  @NotEmpty
  @Column(name = "orderer_mobile_phone")
  @Schema(description = "주문자 휴대전화", example = "01045674567", required = true)
  private String mobilePhone;

  @NotEmpty
  @Column(name = "orderer_email")
  @Schema(description = "주문자 이메일", example = "gumea@naver.com", required = true)
  private String email;

}

