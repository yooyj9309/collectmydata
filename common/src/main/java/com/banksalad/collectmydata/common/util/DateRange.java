package com.banksalad.collectmydata.common.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class DateRange {

  private LocalDate startDate;
  private LocalDate endDate;
}
