package com.banksalad.collectmydata.card.card;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;

import com.banksalad.collectmydata.card.card.dto.BillBasic;
import com.banksalad.collectmydata.card.card.dto.BillDetail;
import com.banksalad.collectmydata.card.card.dto.ListBillBasicRequest;
import com.banksalad.collectmydata.card.card.dto.ListBillDetailRequest;
import com.banksalad.collectmydata.card.collect.Executions;
import com.banksalad.collectmydata.card.common.db.entity.BillDetailEntity;
import com.banksalad.collectmydata.card.common.db.repository.BillDetailRepository;
import com.banksalad.collectmydata.card.util.TestHelper;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.bill.BillService;
import com.banksalad.collectmydata.finance.api.bill.BillTransactionRequestHelper;
import com.banksalad.collectmydata.finance.api.bill.BillTransactionResponseHelper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.google.common.collect.Lists;
import javax.transaction.Transactional;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static com.banksalad.collectmydata.card.util.FileUtil.readText;
import static com.banksalad.collectmydata.card.util.TestHelper.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.card.util.TestHelper.ENTITY_IGNORE_FIELD;
import static com.banksalad.collectmydata.card.util.TestHelper.ORGANIZATION_ID;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class BillDetailServiceTest {

  @Autowired
  private BillService<ListBillBasicRequest, BillBasic, ListBillDetailRequest, BillDetail> billService;

  @Autowired
  private BillTransactionRequestHelper<ListBillDetailRequest, BillBasic> transactionRequestHelper;

  @Autowired
  private BillTransactionResponseHelper<BillBasic, BillDetail> transactionResponseHelper;

  @Autowired
  private BillDetailRepository billDetailRepository;

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
  @DisplayName("6.3.5 청구 추가정보 조회 : 성공 케이스")
  public void getCardBillsBasicTest_case1() {
    ExecutionContext context = TestHelper.getExecutionContext(wireMockServer.port());

    List<BillBasic> billBasics = Lists.newArrayList(
        BillBasic.builder()
            .seqno("0")
            .chargeAmt(BigDecimal.valueOf(100000).setScale(3, RoundingMode.CEILING))
            .chargeDay(14)
            .chargeMonth(202103)
            .paidOutDate("20210314")
            .cardType("01")
            .build(),
        BillBasic.builder()
            .seqno("1")
            .chargeAmt(BigDecimal.valueOf(150000).setScale(3, RoundingMode.CEILING))
            .chargeDay(14)
            .chargeMonth(202103)
            .paidOutDate("20210314")
            .cardType("01")
            .build()
    );
    billService
        .listBillTransactions(context, Executions.finance_card_bills_detail, billBasics, transactionRequestHelper,
            transactionResponseHelper);

    List<BillDetailEntity> billDetailEntities = billDetailRepository.findAll();
    assertEquals(4, billDetailEntities.size());
    assertThat(billDetailEntities.get(0)).usingRecursiveComparison()
        .ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(
            BillDetailEntity.builder()
                .banksaladUserId(BANKSALAD_USER_ID)
                .organizationId(ORGANIZATION_ID)
                .chargeMonth(202103)
                .seqno("0")
                .billDetailNo((short) 0)
                .cardId("card-001")
                .paidDtime("20210302090000")
                .paidAmt(BigDecimal.valueOf(50000).setScale(3, RoundingMode.CEILING))
                .currencyCode("KRW")
                .merchantName("스타벅스")
                .creditFeeAmt(BigDecimal.valueOf(500).setScale(3, RoundingMode.CEILING))
                .prodType("01")
                .build()
        );
    assertThat(billDetailEntities.get(1)).usingRecursiveComparison()
        .ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(
            BillDetailEntity.builder()
                .banksaladUserId(BANKSALAD_USER_ID)
                .organizationId(ORGANIZATION_ID)
                .chargeMonth(202103)
                .seqno("0")
                .billDetailNo((short) 1)
                .cardId("card-001")
                .paidDtime("20210301010000")
                .paidAmt(BigDecimal.valueOf(30000).setScale(3, RoundingMode.CEILING))
                .currencyCode("KRW")
                .merchantName("메가커피")
                .creditFeeAmt(BigDecimal.valueOf(300).setScale(3, RoundingMode.CEILING))
                .prodType("01")
                .build()
        );
    assertThat(billDetailEntities.get(2)).usingRecursiveComparison()
        .ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(
            BillDetailEntity.builder()
                .banksaladUserId(BANKSALAD_USER_ID)
                .organizationId(ORGANIZATION_ID)
                .chargeMonth(202103)
                .seqno("1")
                .billDetailNo((short) 0)
                .cardId("card-002")
                .paidDtime("20210302090000")
                .paidAmt(BigDecimal.valueOf(50000).setScale(3, RoundingMode.CEILING))
                .currencyCode("KRW")
                .merchantName("스타벅스")
                .creditFeeAmt(BigDecimal.valueOf(500).setScale(3, RoundingMode.CEILING))
                .prodType("01")
                .build()
        );
    assertThat(billDetailEntities.get(3)).usingRecursiveComparison()
        .ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(
            BillDetailEntity.builder()
                .banksaladUserId(BANKSALAD_USER_ID)
                .organizationId(ORGANIZATION_ID)
                .chargeMonth(202103)
                .seqno("1")
                .billDetailNo((short) 1)
                .cardId("card-002")
                .paidDtime("20210301010000")
                .paidAmt(BigDecimal.valueOf(30000).setScale(3, RoundingMode.CEILING))
                .currencyCode("KRW")
                .merchantName("메가커피")
                .creditFeeAmt(BigDecimal.valueOf(300).setScale(3, RoundingMode.CEILING))
                .prodType("01")
                .build()
        );
  }

  private static void setupMockServer() {
    wireMockServer.stubFor(get(urlMatching("/cards/bills/detail.*")).withQueryParam("seqno", matching("0"))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/CD22_001_single_page_00.json"))));
    wireMockServer.stubFor(get(urlMatching("/cards/bills/detail.*")).withQueryParam("seqno", matching("1"))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/CD22_001_single_page_01.json"))));
  }
}
