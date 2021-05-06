package com.banksalad.collectmydata.mock.invest.controller;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banksalad.collectmydata.mock.common.api.annotation.BanksaladUserId;
import com.banksalad.collectmydata.mock.common.api.annotation.OrgCode;
import com.banksalad.collectmydata.mock.common.api.annotation.SearchTimestamp;
import com.banksalad.collectmydata.mock.invest.controller.model.GetInvestAccountBasicRequest;
import com.banksalad.collectmydata.mock.invest.controller.model.GetInvestAccountBasicResponse;
import com.banksalad.collectmydata.mock.invest.controller.model.GetInvestAccountTransactionRequest;
import com.banksalad.collectmydata.mock.invest.controller.model.GetInvestAccountTransactionResponse;
import com.banksalad.collectmydata.mock.invest.controller.model.GetInvestAccountsResponse;
import com.banksalad.collectmydata.mock.invest.dto.InvestAccountBasic;
import com.banksalad.collectmydata.mock.invest.dto.InvestAccountBasicSearch;
import com.banksalad.collectmydata.mock.invest.dto.InvestAccountSummary;
import com.banksalad.collectmydata.mock.invest.dto.InvestAccountSummarySearch;
import com.banksalad.collectmydata.mock.invest.dto.InvestAccountTransactionPage;
import com.banksalad.collectmydata.mock.invest.dto.InvestAccountTransactionPageSearch;
import com.banksalad.collectmydata.mock.invest.service.InvestService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;

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
    List<InvestAccountSummary> investAccountSummaryList = investService
        .getInvestAccountList(investAccountSummarySearch);

    return GetInvestAccountsResponse.builder()
        .regDate(regDate)
        .accountCnt(investAccountSummaryList.size())
        .investAccountSummaryList(investAccountSummaryList)
        .build();
  }

  @PostMapping("/basic")
  public GetInvestAccountBasicResponse getInvestAccountBasic(@BanksaladUserId Long banksaladUserId,
      @OrgCode String orgCode,
      @SearchTimestamp LocalDateTime searchTimestamp,
      @Valid @RequestBody GetInvestAccountBasicRequest getInvestAccountBasicRequest) {

    InvestAccountBasic investAccountBasic = investService.getInvestAccountBasic(InvestAccountBasicSearch.builder()
        .banksaladUserId(banksaladUserId)
        .organizationId(orgCode)
        .accountNum(getInvestAccountBasicRequest.getAccountNum())
        .build());

    return GetInvestAccountBasicResponse.builder()
        .investAccountBasic(investAccountBasic)
        .build();
  }

  @PostMapping("/transactions")
  public GetInvestAccountTransactionResponse getInvestAccountTransactions(@BanksaladUserId Long banksaladUserId,
      @OrgCode String orgCode,
      @Valid @RequestBody GetInvestAccountTransactionRequest investAccountTransactionRequest) {

    InvestAccountTransactionPage investAccountTransactionPage = investService.getInvestAccountTransactionPage(
        InvestAccountTransactionPageSearch.builder()
            .banksaladUserId(banksaladUserId)
            .orgCode(orgCode)
            .accountNum(investAccountTransactionRequest.getAccountNum())
            .fromDate(investAccountTransactionRequest.getFromDate())
            .toDate(investAccountTransactionRequest.getToDate())
            .pageNumber(NumberUtils.toInt(investAccountTransactionRequest.getNextPage()))
            .pageSize(investAccountTransactionRequest.getLimit())
            .build()
    );

    int nextPageNumber = investAccountTransactionPage.getPageNumber() + 1;
    return GetInvestAccountTransactionResponse.builder()
        .nextPage(investAccountTransactionPage.isLast() ? null : String.valueOf(nextPageNumber))
        .transCnt(investAccountTransactionPage.getTotalElements())
        .investAccountTransactionList(investAccountTransactionPage.getInvestAccountTransaction())
        .build();
  }
}
