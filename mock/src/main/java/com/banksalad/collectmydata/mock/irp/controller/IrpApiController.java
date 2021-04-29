package com.banksalad.collectmydata.mock.irp.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
import com.banksalad.collectmydata.mock.irp.controller.model.PostIrpsDetailRequest;
import com.banksalad.collectmydata.mock.irp.controller.model.PostIrpsDetailResponse;
import com.banksalad.collectmydata.mock.irp.dto.IrpAccountBasic;
import com.banksalad.collectmydata.mock.irp.dto.IrpAccountBasicSearch;
import com.banksalad.collectmydata.mock.irp.dto.IrpAccountDetail;
import com.banksalad.collectmydata.mock.irp.dto.IrpAccountDetailSearch;
import com.banksalad.collectmydata.mock.irp.dto.IrpAccountSummary;
import com.banksalad.collectmydata.mock.irp.dto.IrpAccountSummarySearch;
import com.banksalad.collectmydata.mock.irp.service.IrpService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/{industry:(?:bank|invest)}/irps")
@RequiredArgsConstructor
public class IrpApiController {

  @Qualifier("bankIrpService")
  private final IrpService bankIrpService;

  @Qualifier("investIrpService")
  private final IrpService investIrpService;

  @GetMapping("")
  public GetIrpsResponse getIrps(
      @PathVariable("industry") String industry,
      @BanksaladUserId Long banksaladUserId,
      @SearchTimestamp LocalDateTime searchTimestamp,
      @OrgCode String orgCode) {

    List<IrpAccountSummary> irpAccountSummaryList = getIrpServiceByIndustry(industry).getIrpAccountSummaryList(
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
  public PostIrpsBasicResponse getIrpsBasic(
      @PathVariable("industry") String industry,
      @BanksaladUserId Long banksaladUserId,
      @OrgCode String orgCode,
      @Valid @RequestBody PostIrpsBasicRequest postIrpsBasicRequest) {

    IrpAccountBasic irpAccountBasic = getIrpServiceByIndustry(industry)
        .getIrpAccountBasic(IrpAccountBasicSearch.builder()
            .banksaladUserId(banksaladUserId)
            .organizationId(orgCode)
            .accountNum(postIrpsBasicRequest.getAccountNum())
            .seqno(postIrpsBasicRequest.getSeqno())
            .build());

    return PostIrpsBasicResponse.builder()
        .irpAccountBasic(irpAccountBasic)
        .build();
  }

  @PostMapping("/detail")
  public PostIrpsDetailResponse getIrpsDetail(
      @PathVariable("industry") String industry,
      @BanksaladUserId Long banksaladUserId,
      @OrgCode String orgCode,
      @SearchTimestamp LocalDateTime searchTimestamp,
      @Valid @RequestBody PostIrpsDetailRequest postIrpsDetailRequest) {

    int irpCnt = getIrpServiceByIndustry(industry)
        .getIrpAccountDetailCount(IrpAccountDetailSearch.builder()
            .banksaladUserId(banksaladUserId)
            .organizationId(orgCode)
            .accountNum(postIrpsDetailRequest.getAccountNum())
            .seqno(postIrpsDetailRequest.getSeqno())
            .updatedAt(searchTimestamp)
            .build());

    int pageNumber = NumberUtils.toInt(postIrpsDetailRequest.getNextPage());
    int pageSize = postIrpsDetailRequest.getLimit();
    List<IrpAccountDetail> irpAccountDetailList = getIrpServiceByIndustry(industry)
        .getIrpAccountDetailList(IrpAccountDetailSearch.builder()
            .banksaladUserId(banksaladUserId)
            .organizationId(orgCode)
            .accountNum(postIrpsDetailRequest.getAccountNum())
            .seqno(postIrpsDetailRequest.getSeqno())
            .updatedAt(searchTimestamp)
            .pageNumber(pageNumber)
            .pageSize(pageSize)
            .build());

    boolean hasNextPage = ((pageNumber * pageSize) + irpAccountDetailList.size()) < irpCnt;
    String nextPage = hasNextPage ? String.valueOf(pageNumber + 1) : null;

    return PostIrpsDetailResponse.builder()
        .irpCnt(irpCnt)
        .nextPage(nextPage)
        .irpList(irpAccountDetailList)
        .build();
  }

  private IrpService getIrpServiceByIndustry(String industry) {
    if ("bank".equals(industry)) {
      return bankIrpService;
    } else {
      return investIrpService;
    }
  }
}
