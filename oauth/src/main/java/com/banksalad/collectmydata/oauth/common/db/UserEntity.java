package com.banksalad.collectmydata.oauth.common.db;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;


@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserEntity implements Serializable {

  private Long banksaladUserId;

  private String organizationId;

  private String organizationCode;

  private String os;

  private LocalDateTime createdAt;
}
