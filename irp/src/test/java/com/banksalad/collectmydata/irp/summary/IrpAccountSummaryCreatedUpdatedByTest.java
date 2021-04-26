package com.banksalad.collectmydata.irp.summary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.banksalad.collectmydata.finance.common.service.UserSyncStatusService;
import com.banksalad.collectmydata.irp.collect.Apis;
import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountSummaryEntity;
import com.banksalad.collectmydata.irp.common.db.repository.IrpAccountSummaryRepository;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountSummary;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.CONSENT_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.SYNC_REQUEST_ID;
import static com.banksalad.collectmydata.irp.util.FileUtil.readText;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@DisplayName("개인형 IRP 계좌 목록 createdBy, updatedBy 테스트")
public class IrpAccountSummaryCreatedUpdatedByTest {

  private static final Long BANKSALAD_USER_ID = 1L;
  private static final String ORGANIZATION_ID = "organizationId";
  private static final String ORGANIZATION_HOST = "http://localhost";
  public static WireMockServer wiremock = new WireMockServer(WireMockSpring.options().dynamicPort());
  @Autowired
  private IrpAccountSummaryService irpAccountSummaryService;
  @Autowired
  private UserSyncStatusService userSyncStatusService;
  private List<IrpAccountSummary> expectedIrpAccountSummaries;
  @Autowired
  private IrpAccountSummaryRepository irpAccountSummaryRepository;

  @AfterAll
  public static void clean() {
    wiremock.shutdown();
  }

  @BeforeEach
  public void setupClass() {

    String accountNum = "1234123412341234";

    expectedIrpAccountSummaries = List.of(
        IrpAccountSummary.builder()
            .prodName("상품명1")
            .accountNum(accountNum)
            .seqno("1")
            .consent(true)
            .accountStatus("01").build(),
        IrpAccountSummary.builder()
            .prodName("상품명2")
            .accountNum(accountNum)
            .seqno("2")
            .consent(true)
            .accountStatus("01").build()
    );

    wiremock.start();
  }

  @AfterEach
  public void after() {
    wiremock.resetAll();
  }

  @Test
  void getAccountSummaries() throws ResponseNotOkException {

    /* summary mock server */
    setupServerAccountsSinglePage();

    /* execution context */
    ExecutionContext executionContext = ExecutionContext.builder()
        .consentId(CONSENT_ID)
        .syncRequestId(SYNC_REQUEST_ID)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accessToken("test")
        .organizationHost(ORGANIZATION_HOST + ":" + wiremock.port())
        .organizationCode("020")
        .syncStartedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
        .build();

    irpAccountSummaryService.listAccountSummaries(executionContext);

    List<IrpAccountSummary> actualIrpAccountSummaries = irpAccountSummaryService
        .listConsentedAccountSummaries(executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId());

    assertThat(actualIrpAccountSummaries.get(0)).usingRecursiveComparison().ignoringFields()
        .isEqualTo(expectedIrpAccountSummaries.get(0));
    assertThat(userSyncStatusService.getSearchTimestamp(BANKSALAD_USER_ID, ORGANIZATION_ID,
        Apis.irp_get_accounts)).isEqualTo(100);

    List<IrpAccountSummaryEntity> irpAccountSummaryEntities = irpAccountSummaryRepository.findAll();

    assertThat(irpAccountSummaryEntities.get(0).getConsentId()).isEqualTo(CONSENT_ID);
    assertThat(irpAccountSummaryEntities.get(0).getSyncRequestId()).isEqualTo(SYNC_REQUEST_ID);
    assertThat(irpAccountSummaryEntities.get(0).getCreatedBy()).isEqualTo(String.valueOf(BANKSALAD_USER_ID));
    assertThat(irpAccountSummaryEntities.get(0).getUpdatedBy()).isEqualTo(String.valueOf(BANKSALAD_USER_ID));
  }

  private void setupServerAccountsSinglePage() {

    // 계좌목록조회 page 01
    wiremock.stubFor(get(urlMatching("/irps.*"))
        .withQueryParam("org_code", equalTo("020"))
        .withQueryParam("search_timestamp", equalTo("0"))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/irp/response/IR01_002_single_page_00.json"))));
  }
}
