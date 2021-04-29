package com.banksalad.collectmydata.mock.irp.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class IrpAccountTransactionSearch {

  private long banksaladUserId;
  private String organizationId;
  private String accountNum;
  private String seqno;
  private LocalDateTime updatedAt;
  private LocalDateTime fromCreatedAt;
  private LocalDateTime toCreatedAt;
  private int pageNumber;
  private int pageSize;
}
