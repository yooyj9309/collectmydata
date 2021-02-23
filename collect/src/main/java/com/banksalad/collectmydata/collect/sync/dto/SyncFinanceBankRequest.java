package com.banksalad.collectmydata.collect.sync.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SyncFinanceBankRequest {

  // TODO : proto는 왜 안되는지?
  private long banksaladUserId;
  private String organizationObjectid;

}
