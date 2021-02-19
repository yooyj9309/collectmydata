package com.banksalad.collectmydata.capital.lease.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class OperatingLeaseTransactionRequest {

  private String orgCode;
  private String accountNum;
  private Integer seqno;
  private String fromDtime;
  private String toDtime;
  private String nextPage;
  private int limit;

  public void updateNextPage(String nextPage) {
    this.nextPage = nextPage;
  }
}
