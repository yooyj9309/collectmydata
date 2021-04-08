package com.banksalad.collectmydata.invest.template.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class BareMain {

  private LocalDateTime syncedAt;
}

