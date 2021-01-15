package com.banksalad.collectmydata.oauth.dto;

import javax.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IssueTokenRequestDto {

  @NotEmpty(message = "state는 필수 값 입니다.")
  private String state;
}
