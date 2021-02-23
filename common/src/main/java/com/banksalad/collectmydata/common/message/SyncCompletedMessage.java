package com.banksalad.collectmydata.common.message;

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
public class SyncCompletedMessage {

  private long banksaladUserId;
  private String organizationId;
  private String syncRequestId;
  private String syncResponseBody;
}
