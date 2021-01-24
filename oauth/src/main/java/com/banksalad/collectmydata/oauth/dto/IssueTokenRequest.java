package com.banksalad.collectmydata.oauth.dto;

import javax.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IssueTokenRequest {

  @NotEmpty(message = "state는 필수 값 입니다.")
  private String state;

  private String code;

  //TODO 필수값이며 별첨 1-1을 참고하라하지만, 성공의 경우 별도 메시지가없어서 우선 null허용 추후 수정.
  private String error;

  private String errorDescription;
}
