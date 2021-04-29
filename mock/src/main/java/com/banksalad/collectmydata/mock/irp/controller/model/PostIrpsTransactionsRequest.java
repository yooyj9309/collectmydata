package com.banksalad.collectmydata.mock.irp.controller.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class PostIrpsTransactionsRequest {

  @NotEmpty
  private String accountNum;

  private String seqno;

  @NotNull
  @PastOrPresent
  @JsonFormat(pattern = "yyyyMMdd")
  private LocalDate fromDate;

  @NotNull
  @PastOrPresent
  @JsonFormat(pattern = "yyyyMMdd")
  private LocalDate toDate;

  private String nextPage;

  @NotNull
  @Max(500)
  @Min(1)
  private Integer limit;

}
