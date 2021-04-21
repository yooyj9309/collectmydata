package com.banksalad.collectmydata.bank.deposit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.bank.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.bank.common.db.repository.DepositAccountDetailRepository;
import com.banksalad.collectmydata.bank.deposit.dto.DepositAccountDetail;
import com.banksalad.collectmydata.bank.deposit.dto.GetDepositAccountDetailRequest;
import com.banksalad.collectmydata.bank.summary.dto.AccountSummary;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoRequestHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoService;
import com.banksalad.collectmydata.finance.common.db.repository.UserSyncStatusRepository;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

@SpringBootTest
@DisplayName("6.2.3 수신계좌 추가정보 조회")
@Transactional
public class DepositAccountDetailServiceTest {

  @Autowired
  private AccountInfoService<AccountSummary, GetDepositAccountDetailRequest, List<DepositAccountDetail>> depositAccountDetailApiService;

  @Autowired
  private AccountInfoRequestHelper<GetDepositAccountDetailRequest, AccountSummary> depositAccountDetailInfoRequestHelper;

  @Autowired
  private AccountInfoResponseHelper<AccountSummary, List<DepositAccountDetail>> depositAccountDetailInfoResponseHelper;

  @Autowired
  private AccountSummaryRepository accountSummaryRepository;

  @Autowired
  private DepositAccountDetailRepository depositAccountDetailRepository;

  @Autowired
  private UserSyncStatusRepository userSyncStatusRepository;

  private static WireMockServer wireMockServer;

  @BeforeAll
  static void setup() {
    wireMockServer = new WireMockServer(WireMockSpring.options().dynamicPort());
    wireMockServer.start();
  }

  @AfterAll
  static void tearDown() {
    wireMockServer.shutdown();
  }
}
