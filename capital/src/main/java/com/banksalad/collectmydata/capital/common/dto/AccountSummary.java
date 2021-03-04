package com.banksalad.collectmydata.capital.common.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
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
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AccountSummary {

  private String accountNum;
  private String seqno; // seqno는 null 필드이나, int 일 경우, mapping 과정에서 디폴트값 적재될 가능성있음.
  private Boolean isConsent;
  private String prodName;
  private String accountType;
  private String accountStatus;
  private long basicSearchTimestamp; // account -> entity mapping 과정에서 제거해야될 필드
  private long detailSearchTimestamp; // account -> entity mapping 과정에서 제거해야될 필드
  private long operatingLeaseBasicSearchTimestamp; // account -> entity mapping 과정에서 제거해야될 필드

  // TODO 6.7.2 6.7.3 response도 아래에 이어서 관리하는게 맞지않나 고려, (dto -> entity로 변환될 구조이니)
}
