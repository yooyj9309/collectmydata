package com.banksalad.collectmydata.insu.common.template.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class BareMain {

  private LocalDateTime syncedAt;
}
