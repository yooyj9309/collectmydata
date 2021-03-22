package com.banksalad.collectmydata.telecom.telecom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.executor.CollectExecutor;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoRequestHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoService;
import com.banksalad.collectmydata.finance.common.db.entity.UserSyncStatusEntity;
import com.banksalad.collectmydata.finance.common.db.repository.UserSyncStatusRepository;
import com.banksalad.collectmydata.telecom.collect.Apis;
import com.banksalad.collectmydata.telecom.collect.Executions;
import com.banksalad.collectmydata.telecom.common.db.entity.BillEntity;
import com.banksalad.collectmydata.telecom.common.db.entity.BillHistoryEntity;
import com.banksalad.collectmydata.telecom.common.db.repository.BillHistoryRepository;
import com.banksalad.collectmydata.telecom.common.db.repository.BillRepository;
import com.banksalad.collectmydata.telecom.telecom.dto.ListTelecomBillsRequest;
import com.banksalad.collectmydata.telecom.telecom.dto.TelecomBill;
import com.banksalad.collectmydata.telecom.telecom.dto.TelecomBillRequestSupporter;
import com.github.tomakehurst.wiremock.WireMockServer;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static com.banksalad.collectmydata.telecom.common.util.FileUtil.readText;
import static com.banksalad.collectmydata.telecom.common.util.TestHelper.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.telecom.common.util.TestHelper.ENTITY_IGNORE_FIELD;
import static com.banksalad.collectmydata.telecom.common.util.TestHelper.ORGANIZATION_CODE;
import static com.banksalad.collectmydata.telecom.common.util.TestHelper.ORGANIZATION_ID;
import static com.banksalad.collectmydata.telecom.common.util.TestHelper.getExecutionContext;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest
@DisplayName("6.9.2 청구 정보 조회 테스트")
public class TelecomBillServiceTest {

  @Autowired
  private AccountInfoService<TelecomBillRequestSupporter, ListTelecomBillsRequest, List<TelecomBill>> telecomBillService;
  @Autowired
  private AccountInfoRequestHelper<ListTelecomBillsRequest, TelecomBillRequestSupporter> telecomBillRequestHelper;
  @Autowired
  private AccountInfoResponseHelper<TelecomBillRequestSupporter, List<TelecomBill>> telecomBillResponseHelper;
  @Autowired
  private UserSyncStatusRepository UserSyncStatusRepository;
  @Autowired
  private BillRepository billRepository;
  @Autowired
  private BillHistoryRepository billHistoryRepository;

  private static WireMockServer wireMockServer;

  @Autowired
  private CollectExecutor collectExecutor;

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
  @DisplayName("6.9.2 조회 성공케이스")
  public void telecomBill_success() {
    ExecutionContext context = getExecutionContext(wireMockServer.port());
    UserSyncStatusRepository.save(
        UserSyncStatusEntity.builder()
            .syncedAt(context.getSyncStartedAt().minusMonths(1).plusDays(1))
            .banksaladUserId(context.getBanksaladUserId())
            .organizationId(context.getOrganizationId())
            .apiId(Apis.finance_telecom_bills.getId())
            .build()
    );

    telecomBillService.listAccountInfos(context, Executions.finance_telecom_bills, telecomBillRequestHelper,
        telecomBillResponseHelper);

    List<BillEntity> billEntities = billRepository.findAll();
    List<BillHistoryEntity> billHistoryEntities = billHistoryRepository.findAll();

    assertEquals(2, billEntities.size());
    assertEquals(2, billHistoryEntities.size());

    assertThat(billEntities.get(0)).usingRecursiveComparison()
        .ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(
            BillEntity.builder()
                .banksaladUserId(BANKSALAD_USER_ID)
                .organizationId(ORGANIZATION_ID)
                .chargeMonth(Integer.valueOf(DateUtil
                    .utcLocalDateTimeToKstYearMonthString(context.getSyncStartedAt().minusMonths(1).plusDays(1))))
                .mgmtId("11")
                .chargeAmt(new BigDecimal("123456789"))
                .chargeDate("20210301")
                .build()
        );

    assertThat(billHistoryEntities.get(0)).usingRecursiveComparison()
        .ignoringFields(ENTITY_IGNORE_FIELD)
        .isEqualTo(
            BillEntity.builder()
                .banksaladUserId(BANKSALAD_USER_ID)
                .organizationId(ORGANIZATION_ID)
                .chargeMonth(Integer.valueOf(DateUtil
                    .utcLocalDateTimeToKstYearMonthString(context.getSyncStartedAt().minusMonths(1).plusDays(1))))
                .mgmtId("11")
                .chargeAmt(new BigDecimal("123456789"))
                .chargeDate("20210301")
                .build()
        );
  }

  private static void setupMockServer() {
    // 6.9.2 청구 정보 조회 테스트
    wireMockServer.stubFor(get(urlMatching("/telecoms/bills.*"))
        .withQueryParam("org_code", equalTo(ORGANIZATION_CODE))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/TC02_001_single_page_00.json"))));
  }
}
