package com.banksalad.collectmydata.irp.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountSummaryEntity;
import com.banksalad.collectmydata.irp.common.db.entity.mapper.IrpAccountSummaryMapper;
import com.banksalad.collectmydata.irp.common.db.repository.IrpAccountSummaryRepository;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountBasicResponse;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountDetail;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountSummary;
import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.banksalad.collectmydata.irp.util.FileUtil.readText;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DisplayName("계좌 기본 및 추가정보 조회 테스트")
@Transactional
class IrpAccountServiceImplTest {

  private static final Long BANKSALAD_USER_ID = 1L;
  private static final String ORGANIZATION_ID = "organizationId";
  private static final String ORGANIZATION_HOST = "http://localhost";

  @Autowired
  private IrpAccountService irpAccountService;

  @Autowired
  private IrpAccountSummaryRepository irpAccountSummaryRepository;

  private IrpAccountSummaryMapper irpAccountSummaryMapper = Mappers.getMapper(IrpAccountSummaryMapper.class);

  public static WireMockServer wiremock = new WireMockServer(WireMockSpring.options().dynamicPort());

  @BeforeEach
  public void setupClass() {
    wiremock.start();
  }

  @AfterEach
  public void after() {
    wiremock.resetAll();
  }

  @AfterAll
  public static void clean() {
    wiremock.shutdown();
  }

  @Test
  void getIrpAccountBasics() {

    /* irp account basic mock server */
    setupServerIrpAccountBasic();

    /* save mock account summaries */
    List<IrpAccountSummaryEntity> irpAccountSummaryEntities = getIrpAccountSummaryEntities();
    irpAccountSummaryRepository.saveAll(irpAccountSummaryEntities);

    List<IrpAccountSummary> irpAccountSummaries = irpAccountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndIsConsent(BANKSALAD_USER_ID, ORGANIZATION_ID, true)
        .stream()
        .map(irpAccountSummaryMapper::entityToDto)
        .collect(Collectors.toList());

    /* execution context */
    ExecutionContext executionContext = ExecutionContext.builder()
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accessToken("test")
        .organizationHost(ORGANIZATION_HOST + ":" + wiremock.port())
        .executionRequestId(UUID.randomUUID().toString())
        .syncStartedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
        .build();

    List<IrpAccountBasicResponse> irpAccountBasics = irpAccountService
        .getIrpAccountBasics(executionContext, irpAccountSummaries);

    assertThat(irpAccountBasics.size()).isEqualTo(1);
  }

  @Test
  @DisplayName("IRP계좌 추가정보 조회")
  public void listIrpAccountDetails() {

    /* irp account detail mock server */
    setupServerIrpAccountDetailMultiPage();

    /* save mock account summaries */
    List<IrpAccountSummaryEntity> irpAccountSummaryEntities = getIrpAccountSummaryEntities();
    irpAccountSummaryRepository.saveAll(irpAccountSummaryEntities);

    List<IrpAccountSummary> irpAccountSummaries = irpAccountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndIsConsent(BANKSALAD_USER_ID, ORGANIZATION_ID, true)
        .stream()
        .map(irpAccountSummaryMapper::entityToDto)
        .collect(Collectors.toList());

    /* execution context */
    ExecutionContext executionContext = ExecutionContext.builder()
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accessToken("test")
        .organizationHost(ORGANIZATION_HOST + ":" + wiremock.port())
        .executionRequestId(UUID.randomUUID().toString())
        .syncStartedAt(LocalDateTime.now(DateUtil.KST_ZONE_ID))
        .build();

    List<IrpAccountDetail> irpAccountDetails = irpAccountService
        .listIrpAccountDetails(executionContext, irpAccountSummaries);

    Assertions.assertThat(irpAccountDetails.size()).isEqualTo(4);
  }

  private void setupServerIrpAccountBasic() {
    wiremock.stubFor(post(urlMatching("/irps/basic"))
        .withRequestBody(equalToJson(readText("classpath:mock/irp/request/IR02_001_single_page_00.json")))
        .willReturn(
            aResponse()
                .withFixedDelay(1000)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/irp/response/IR02_001_single_page_00.json"))));
  }

  private void setupServerIrpAccountDetailMultiPage() {

    // 추가정보조회 page 01
    wiremock.stubFor(post(urlMatching("/irps/detail"))
        .withRequestBody(equalToJson(readText("classpath:mock/irp/request/IR03_001_multi_page_00.json")))
        .willReturn(
            aResponse()
                .withFixedDelay(1000)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/irp/response/IR03_001_multi_page_00.json"))));

    // 추가정보조회 page 02
    wiremock.stubFor(post(urlMatching("/irps/detail"))
        .withRequestBody(equalToJson(readText("classpath:mock/irp/request/IR03_001_multi_page_01.json")))
        .willReturn(
            aResponse()
                .withFixedDelay(1000)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/irp/response/IR03_001_multi_page_01.json"))));
  }

  private List<IrpAccountSummaryEntity> getIrpAccountSummaryEntities() {
    return List.of(
        IrpAccountSummaryEntity.builder()
            .banksaladUserId(BANKSALAD_USER_ID)
            .organizationId(ORGANIZATION_ID)
            .syncedAt(LocalDateTime.now(DateUtil.KST_ZONE_ID))
            .accountNum("100246541123")
            .accountStatus("01")
            .basicSearchTimestamp(0L)
            .detailSearchTimestamp(0L)
            .transactionSyncedAt(null)
            .isConsent(true)
            .prodName("개인형 IRP 계좌1")
            .seqno("a123")
            .build(),
        IrpAccountSummaryEntity.builder()
            .banksaladUserId(BANKSALAD_USER_ID)
            .organizationId(ORGANIZATION_ID)
            .syncedAt(LocalDateTime.now(DateUtil.KST_ZONE_ID))
            .accountNum("234246541143")
            .accountStatus("01")
            .basicSearchTimestamp(0L)
            .detailSearchTimestamp(0L)
            .transactionSyncedAt(null)
            .isConsent(false)
            .prodName("개인형 IRP 계좌2")
            .build()
    );
  }
}
