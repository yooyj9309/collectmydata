package com.banksalad.collectmydata.efin.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.efin.account.dto.AccountPrepaidTransaction;
import com.banksalad.collectmydata.efin.account.dto.ListAccountPrepaidTransactionsRequest;
import com.banksalad.collectmydata.efin.collect.Executions;
import com.banksalad.collectmydata.efin.common.db.entity.PrepaidTransactionEntity;
import com.banksalad.collectmydata.efin.common.db.repository.PrepaidTransactionRepository;
import com.banksalad.collectmydata.efin.common.service.AccountSummaryService;
import com.banksalad.collectmydata.efin.common.util.TestHelper;
import com.banksalad.collectmydata.efin.summary.dto.AccountSummary;
import com.banksalad.collectmydata.finance.api.transaction.TransactionApiService;
import com.banksalad.collectmydata.finance.api.transaction.TransactionRequestHelper;
import com.banksalad.collectmydata.finance.api.transaction.TransactionResponseHelper;
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
public class AccountPrepaidTransactionServiceTest {

  @Autowired
  private TransactionApiService<AccountSummary, ListAccountPrepaidTransactionsRequest, AccountPrepaidTransaction> transactionApiService;

  @Autowired
  private TransactionRequestHelper<AccountSummary, ListAccountPrepaidTransactionsRequest> transactionRequestHelper;

  @Autowired
  private TransactionResponseHelper<AccountSummary, AccountPrepaidTransaction> transactionResponseHelper;

  @Autowired
  private PrepaidTransactionRepository prepaidTransactionRepository;

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
  @DisplayName("6.6.4 선불 거래내역 조회 : 성공 케이스")
  public void getAccountPrepaidTransactionTest_case1() {
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

    transactionApiService.listTransactions(context, Executions.finance_efin_prepaid_transactions,
        transactionRequestHelper, transactionResponseHelper);

    List<PrepaidTransactionEntity> prepaidTransactionEntities = prepaidTransactionRepository.findAll();
    assertEquals(2, prepaidTransactionEntities.size());
    assertThat(prepaidTransactionEntities.get(0)).usingRecursiveComparison()
        .ignoringFields(ENTITY_IGNORE_FIELD)
        .ignoringFields("uniqueTransNo")
        .isEqualTo(
            PrepaidTransactionEntity.builder()
                .transactionYearMonth(202103)
                .banksaladUserId(BANKSALAD_USER_ID)
                .organizationId(ORGANIZATION_ID)
                .subKey("mykakaoid")
                .fobName("가상계좌")
                .transType("5201")
                .transDtime("20210302091000")
                .transAmt(BigDecimal.valueOf(5000).setScale(3, RoundingMode.CEILING))
                .balanceAmt(BigDecimal.valueOf(10000).setScale(3, RoundingMode.CEILING))
                .transOrgCode("080")
                .transId("000*****")
                .build()
        );
    assertThat(prepaidTransactionEntities.get(1)).usingRecursiveComparison()
        .ignoringFields(ENTITY_IGNORE_FIELD)
        .ignoringFields("uniqueTransNo")
        .isEqualTo(
            PrepaidTransactionEntity.builder()
                .transactionYearMonth(202103)
                .banksaladUserId(BANKSALAD_USER_ID)
                .organizationId(ORGANIZATION_ID)
                .subKey("mykakaoid")
                .fobName("가상계좌")
                .transType("5201")
                .transDtime("20210301091000")
                .transAmt(BigDecimal.valueOf(5000).setScale(3, RoundingMode.CEILING))
                .balanceAmt(BigDecimal.valueOf(15000).setScale(3, RoundingMode.CEILING))
                .transOrgCode("080")
                .transId("000*****")
                .build()
        );
  }

  private static void setupMockServer() {
    wireMockServer.stubFor(post(urlMatching("/accounts.*"))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/EF04_001_single_page_00.json"))));
  }
}
