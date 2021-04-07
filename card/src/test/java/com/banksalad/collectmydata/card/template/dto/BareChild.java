package com.banksalad.collectmydata.card.template.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class BareChild {

  private LocalDateTime syncedAt;
}
