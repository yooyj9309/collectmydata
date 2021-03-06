package com.banksalad.collectmydata.finance.test.template.dto;

import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class BareResponse {

  /* Response */
  // Mandatory: Json mock 이름 목록을 설정한다. 페이지네이션인 경우 2개 이상을 설정한다.
  @NotNull
  private String mockId;

  // Optional: 200인 경우 설정하지 않아도 된다. Summary service의 예외상황을 테스트한 경우에만 설정한다.
  @Builder.Default
  private Integer status = 200;

  // Optional: "00000"인 경우 설정하지 않아도 된다. Summary service의 예외상황을 테스트한 경우에만 설정한다.
  private String rspCode;
}
