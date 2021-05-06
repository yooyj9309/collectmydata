package com.banksalad.collectmydata.mock.invest.controller.model;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class GetInvestAccountTransactionRequest {

  @NotNull
  @Size(max = 20)
  private String accountNum;

  @NotNull
  @PastOrPresent
  @JsonFormat(pattern = "yyyyMMddHHmmss")
  private LocalDateTime fromDate;

  @NotNull
  @PastOrPresent
  @JsonFormat(pattern = "yyyyMMddHHmmss")
  private LocalDateTime toDate;

  @Size(max = 1000)
  private String nextPage;

  @NotNull
  @Max(500)
  @Min(1)
  private Integer limit;
}
