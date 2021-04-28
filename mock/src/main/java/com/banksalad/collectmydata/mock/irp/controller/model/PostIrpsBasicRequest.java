package com.banksalad.collectmydata.mock.irp.controller.model;

import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostIrpsBasicRequest {

  @NotEmpty
  private String accountNum;

  private String seqno;

}
