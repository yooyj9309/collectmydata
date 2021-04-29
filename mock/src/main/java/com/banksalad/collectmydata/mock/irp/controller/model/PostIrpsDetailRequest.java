package com.banksalad.collectmydata.mock.irp.controller.model;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostIrpsDetailRequest {

  @NotEmpty
  private String accountNum;

  private String seqno;

  private String nextPage;

  @NotNull
  @Max(500)
  @Min(1)
  private Integer limit;

}
