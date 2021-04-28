package com.banksalad.collectmydata.mock.irp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banksalad.collectmydata.mock.common.api.annotation.BanksaladUserId;
import com.banksalad.collectmydata.mock.common.api.annotation.OrgCode;
import com.banksalad.collectmydata.mock.common.api.annotation.SearchTimestamp;
import com.banksalad.collectmydata.mock.irp.controller.model.GetIrpsResponse;
import com.banksalad.collectmydata.mock.irp.controller.model.PostIrpsBasicRequest;
import com.banksalad.collectmydata.mock.irp.controller.model.PostIrpsBasicResponse;
import com.banksalad.collectmydata.mock.irp.dto.IrpAccountBasic;
import com.banksalad.collectmydata.mock.irp.dto.IrpAccountBasicSearch;
import com.banksalad.collectmydata.mock.irp.dto.IrpAccountSummary;
import com.banksalad.collectmydata.mock.irp.dto.IrpAccountSummarySearch;
import com.banksalad.collectmydata.mock.irp.service.IrpService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/irps")
@RequiredArgsConstructor
public class IrpApiController {

  private final IrpService irpService;

  @GetMapping("")
  public GetIrpsResponse getIrps(@BanksaladUserId Long banksaladUserId,
      @SearchTimestamp LocalDateTime searchTimestamp,
      @OrgCode String orgCode) {

    List<IrpAccountSummary> irpAccountSummaryList = irpService.getIrpAccountSummaryList(
        IrpAccountSummarySearch.builder()
            .banksaladUserId(banksaladUserId)
            .organizationId(orgCode)
            .updatedAt(searchTimestamp)
            .build());

    return GetIrpsResponse.builder()
        .irpCnt(irpAccountSummaryList.size())
        .irpList(irpAccountSummaryList)
        .build();
  }

  @PostMapping("/basic")
  public PostIrpsBasicResponse getIrpsBasic(@BanksaladUserId Long banksaladUserId,
      @OrgCode String orgCode,
      @Valid @RequestBody PostIrpsBasicRequest postIrpsBasicRequest) {

    IrpAccountBasic irpAccountBasic = irpService.getIrpAccountBasic(IrpAccountBasicSearch.builder()
        .banksaladUserId(banksaladUserId)
        .organizationId(orgCode)
        .accountNum(postIrpsBasicRequest.getAccountNum())
        .seqno(postIrpsBasicRequest.getSeqno())
        .build());

    return PostIrpsBasicResponse.builder()
        .irpAccountBasic(irpAccountBasic)
        .build();
  }
}
