package com.banksalad.collectmydata.common.message;

import com.banksalad.collectmydata.common.enums.SyncRequestType;

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
public class SyncRequestedMessage {

  private long banksaladUserId;
  private String organizationId;
  private String syncRequestId;
  private SyncRequestType syncRequestType;
}
