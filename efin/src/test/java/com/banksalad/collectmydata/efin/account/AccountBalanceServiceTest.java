package com.banksalad.collectmydata.efin.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.efin.account.dto.AccountBalance;
import com.banksalad.collectmydata.efin.account.dto.ListAccountBalancesRequest;
import com.banksalad.collectmydata.efin.collect.Executions;
import com.banksalad.collectmydata.efin.common.db.entity.BalanceEntity;
import com.banksalad.collectmydata.efin.common.db.repository.BalanceRepository;
import com.banksalad.collectmydata.efin.common.service.AccountSummaryService;
import com.banksalad.collectmydata.efin.common.util.TestHelper;
import com.banksalad.collectmydata.efin.summary.dto.AccountSummary;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoRequestHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoService;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.github.tomakehurst.wiremock.WireMockServer;
import javax.transaction.Transactional;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static com.banksalad.collectmydata.efin.common.util.FileUtil.readText;
import static com.banksalad.collectmydata.efin.common.util.TestHelper.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.efin.common.util.TestHelper.ENTITY_IGNORE_FIELD;
import static com.banksalad.collectmydata.efin.common.util.TestHelper.ORGANIZATION_ID;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
public class AccountBalanceServiceTest {

  @Autowired
  private AccountInfoService<AccountSummary, ListAccountBalancesRequest, List<AccountBalance>> accountInfoService;

  @Autowired
  private AccountInfoRequestHelper<ListAccountBalancesRequest, AccountSummary> requestHelper;

  @Autowired
  private AccountInfoResponseHelper<AccountSummary, List<AccountBalance>> responseHelper;

  @Autowired
  private BalanceRepository balanceRepository;

  @MockBean
  private AccountSummaryService accountSummaryService;

  private static WireMockServer wireMockServer;

  @BeforeAll
  static void setup() {
    wireMockServer = new WireMockServer(WireMockSpring.options().dynamicPort());
    wireMockServer.start();
    setupMockServer();
  }

  @AfterAll
  static void tearDown() {
    wireMockServer.shutdown();
  }

  @Test
  @Transactional
  @DisplayName("6.6.2 전자지급수단 잔액정보 조회 : 성공 케이스")
  public void getAccountBalancesTest_case1() throws ResponseNotOkException {
    ExecutionContext context = TestHelper.getExecutionContext(wireMockServer.port());

    when(accountSummaryService.listSummariesConsented(BANKSALAD_USER_ID, ORGANIZATION_ID))
        .thenReturn(List.of(
            AccountSummary.builder()
                .subKey("mykakaoid")
                .accountId("11****")
                .consent(true)
                .accountStatus("01")
                .payReg(false)
                .build()
        ));

    accountInfoService
        .listAccountInfos(context, Executions.finance_efin_balances, requestHelper, responseHelper);

    List<BalanceEntity> balanceEntities = balanceRepository.findAll();
    assertEquals(2, balanceEntities.size());
    assertThat(balanceEntities.get(0)).usingRecursiveComparison()
        .ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(
            BalanceEntity.builder()
                .banksaladUserId(BANKSALAD_USER_ID)
                .organizationId(ORGANIZATION_ID)
                .subKey("mykakaoid")
                .fobName("가상계좌")
                .totalBalanceAmt(BigDecimal.valueOf(1000000).setScale(3, RoundingMode.CEILING))
                .chargeBalanceAmt(BigDecimal.valueOf(10000).setScale(3, RoundingMode.CEILING))
                .reserveBalanceAmt(BigDecimal.valueOf(0).setScale(3, RoundingMode.CEILING))
                .reserveDueAmt(BigDecimal.valueOf(0).setScale(3, RoundingMode.CEILING))
                .expDueAmt(BigDecimal.valueOf(0).setScale(3, RoundingMode.CEILING))
                .limitAmt(BigDecimal.valueOf(2000000).setScale(3, RoundingMode.CEILING))
                .build()
        );

    assertThat(balanceEntities.get(1)).usingRecursiveComparison()
        .ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(
            BalanceEntity.builder()
                .banksaladUserId(BANKSALAD_USER_ID)
                .organizationId(ORGANIZATION_ID)
                .subKey("mykakaoid")
                .fobName("비밀금고")
                .totalBalanceAmt(BigDecimal.valueOf(500000).setScale(3, RoundingMode.CEILING))
                .chargeBalanceAmt(BigDecimal.valueOf(10000).setScale(3, RoundingMode.CEILING))
                .reserveBalanceAmt(BigDecimal.valueOf(0).setScale(3, RoundingMode.CEILING))
                .reserveDueAmt(BigDecimal.valueOf(0).setScale(3, RoundingMode.CEILING))
                .expDueAmt(BigDecimal.valueOf(0).setScale(3, RoundingMode.CEILING))
                .limitAmt(BigDecimal.valueOf(2000000).setScale(3, RoundingMode.CEILING))
                .build()
        );
  }

  private static void setupMockServer() {
    wireMockServer.stubFor(post(urlMatching("/accounts.*"))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/EF02_001_single_page_00.json"))));
  }
}
