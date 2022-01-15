package com.hipcommerce.orders.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hipcommerce.common.address.domain.Address;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Embeddable
@Schema(description = "배송지 정보")
public class DeliveryInfo {

  @NotNull
  @Embedded
  @Schema(description = "수령인 정보", required = true)
  private Recipient recipient;

  @NotNull
  @Embedded
  @Schema(description = "배송지 주소", required = true)
  private Address address;

  @Column(name = "delivery_message")
  @Schema(description = "배송 메시지", example = "배송메모를 입력해주세요", required = false)
  private String message;

  @JsonIgnore
  public String getZipCode() {
    return address.getZipCode();
  }

  @JsonIgnore
  public String getFullAddress() {
    return address.getAddress() + " " + address.getDetailedAddress();
  }

}
