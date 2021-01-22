package com.banksalad.collectmydata.oauth.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OauthPageRequest {

  @NotEmpty(message = "organizationObjectId값 필수입니다.")
  private String organizationObjectId;

  @NotNull(message = "유저Id(userId)값은 필수입니다.")
  private Long userId;
}
