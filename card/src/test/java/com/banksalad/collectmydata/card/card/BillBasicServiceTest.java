package com.banksalad.collectmydata.card.card;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;

import com.banksalad.collectmydata.card.card.dto.BillBasic;
import com.banksalad.collectmydata.card.card.dto.BillDetail;
import com.banksalad.collectmydata.card.card.dto.ListBillBasicRequest;
import com.banksalad.collectmydata.card.collect.Executions;
import com.banksalad.collectmydata.card.common.db.entity.BillEntity;
import com.banksalad.collectmydata.card.common.db.repository.BillRepository;
import com.banksalad.collectmydata.card.util.TestHelper;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.bill.BillRequestHelper;
import com.banksalad.collectmydata.finance.api.bill.BillResponseHelper;
import com.banksalad.collectmydata.finance.api.bill.BillService;
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

import static com.banksalad.collectmydata.card.util.FileUtil.readText;
import static com.banksalad.collectmydata.card.util.TestHelper.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.card.util.TestHelper.ENTITY_IGNORE_FIELD;
import static com.banksalad.collectmydata.card.util.TestHelper.ORGANIZATION_ID;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class BillBasicServiceTest {

  @Autowired
  private BillService<ListBillBasicRequest, BillBasic, BillDetail> billService;

  @Autowired
  private BillRequestHelper<ListBillBasicRequest> requestHelper;

  @Autowired
  private BillResponseHelper<BillBasic> responseHelper;

  @Autowired
  private BillRepository billRepository;

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
  @DisplayName("6.3.4 청구 기본정보 조회 : 성공 케이스")
  public void getCardBillsBasicTest_case1() throws ResponseNotOkException {
    ExecutionContext context = TestHelper.getExecutionContext(wireMockServer.port());

    billService.listBills(context, Executions.finance_card_bills, requestHelper, responseHelper);

    List<BillEntity> billEntities = billRepository.findAll();
    assertEquals(2, billEntities.size());
    assertThat(billEntities.get(0)).usingRecursiveComparison()
        .ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(
            BillEntity.builder()
                .banksaladUserId(BANKSALAD_USER_ID)
                .organizationId(ORGANIZATION_ID)
                .chargeMonth(202103)
                .cardType("01")
                .chargeAmt(BigDecimal.valueOf(100000).setScale(3, RoundingMode.CEILING))
                .chargeDay(14)
                .paidOutDate("20210314")
                .build()
        );

    assertThat(billEntities.get(1)).usingRecursiveComparison()
        .ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(
            BillEntity.builder()
                .banksaladUserId(BANKSALAD_USER_ID)
                .organizationId(ORGANIZATION_ID)
                .chargeMonth(202102)
                .cardType("01")
                .chargeAmt(BigDecimal.valueOf(120000).setScale(3, RoundingMode.CEILING))
                .chargeDay(14)
                .paidOutDate("20210214")
                .build()
        );
  }

  private static void setupMockServer() {
    wireMockServer.stubFor(get(urlMatching("/cards/bills.*"))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/CD21_001_single_page_00.json"))));
  }
}
