package com.banksalad.collectmydata.connect.token.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class OauthToken {

  private String accessToken;
  private String refreshToken;
  private List<String> scopes = new ArrayList<>();
}
