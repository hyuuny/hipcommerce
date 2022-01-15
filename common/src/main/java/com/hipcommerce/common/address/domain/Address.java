package com.hipcommerce.common.address.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Embeddable
@Schema(description = "주소")
public class Address {

  @Column(length = 5)
  @Schema(description = "우편번호", example = "12345", required = true)
  private String zipCode;

  @Schema(description = "주소", example = "경기도 광명시 시청로 110", required = true)
  private String address;

  @Schema(description = "상세주소", example = "반포자이 306동 2108호", required = true)
  private String detailedAddress;

}
