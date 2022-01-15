package com.hipcommerce.orders.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Embeddable
@Schema(description = "수령인 정보")
public class Recipient {

  @NotNull
  @Column(name = "recipient_name")
  @Schema(description = "수령인 이름", example = "나수신", required = true)
  private String name;

  @NotNull
  @Column(name = "recipient_phone")
  @Schema(description = "수령인 연락처", example = "01012341234", required = true)
  private String phone;

}
