package com.banksalad.collectmydata.mock.invest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banksalad.collectmydata.mock.common.api.annotation.BanksaladUserId;
import com.banksalad.collectmydata.mock.common.api.annotation.OrgCode;
import com.banksalad.collectmydata.mock.common.api.annotation.SearchTimestamp;
import com.banksalad.collectmydata.mock.invest.controller.model.GetInvestAccountsResponse;
import com.banksalad.collectmydata.mock.invest.dto.InvestAccountSummary;
import com.banksalad.collectmydata.mock.invest.dto.InvestAccountSummarySearch;
import com.banksalad.collectmydata.mock.invest.service.InvestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/invest/accounts")
@RequiredArgsConstructor
public class InvestApiController {

  private final InvestService investService;

  @GetMapping
  public GetInvestAccountsResponse getInvestAccounts(@BanksaladUserId Long banksaladUserId,
      @OrgCode String orgCode,
      @SearchTimestamp LocalDateTime searchTimestamp) {

    InvestAccountSummarySearch investAccountSummarySearch = InvestAccountSummarySearch.builder()
        .banksaladUserId(banksaladUserId)
        .organizationId(orgCode)
        .updatedAt(searchTimestamp)
        .build();

    String regDate = investService.getRegistrationDate(investAccountSummarySearch);
    List<InvestAccountSummary> investAccountSummaryList = investService.getInvestAccountList(investAccountSummarySearch);

    return GetInvestAccountsResponse.builder()
        .regDate(regDate)
        .accountCnt(investAccountSummaryList.size())
        .investAccountSummaryList(investAccountSummaryList)
        .build();
  }
}
