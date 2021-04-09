package com.banksalad.collectmydata.finance.test.template.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class BareTransaction {

  private LocalDateTime syncedAt;
}
