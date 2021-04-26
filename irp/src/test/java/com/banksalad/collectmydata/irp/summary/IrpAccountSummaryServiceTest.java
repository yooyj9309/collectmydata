package com.banksalad.collectmydata.irp.summary;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.finance.common.db.entity.ApiLogEntity;
import com.banksalad.collectmydata.finance.common.db.entity.UserSyncStatusEntity;
import com.banksalad.collectmydata.finance.common.db.repository.ApiLogRepository;
import com.banksalad.collectmydata.finance.common.db.repository.UserSyncStatusRepository;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.banksalad.collectmydata.irp.collect.Apis;
import com.banksalad.collectmydata.irp.collect.Executions;
import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountSummaryEntity;
import com.banksalad.collectmydata.irp.common.db.repository.IrpAccountSummaryRepository;
import com.github.tomakehurst.wiremock.WireMockServer;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.http.entity.ContentType;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Diff;
import org.javers.core.diff.ListCompareAlgorithm;
import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.metamodel.clazz.ValueObjectDefinitionBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.CONSENT_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.SYNC_REQUEST_ID;
import static com.banksalad.collectmydata.irp.util.FileUtil.readText;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@RequiredArgsConstructor
@DisplayName("6.1.3 개인형 IRP 계좌 목록 조회")
@Disabled("이전 비즈니스 로직 검증을 위한 테스트 케이스")
class IrpAccountSummaryServiceTest {

  private static final Long BANKSALAD_USER_ID = 1L;
  private static final String ORGANIZATION_ID = "organizationId";
  private static final String ORGANIZATION_HOST = "http://localhost";
  private static final long SEARCH_TIMESTAMP = 100L;

  public static WireMockServer wiremock = new WireMockServer(WireMockSpring.options().dynamicPort());

  private final UserSyncStatusRepository userSyncStatusRepository;

  private final IrpAccountSummaryService accountSummaryService;

  private final IrpAccountSummaryRepository accountSummaryRepository;

  private final ApiLogRepository apiLogRepository;

  private List<IrpAccountSummaryEntity> expectedIrpAccountSummaries;
  private ExecutionContext executionContext;

  @AfterAll
  public static void clean() {
    wiremock.shutdown();
  }

  @BeforeEach
  public void setupClass() {

    String accountNum = "1234123412341234";

    expectedIrpAccountSummaries = List.of(
        IrpAccountSummaryEntity.builder()
            .consentId(CONSENT_ID)
            .syncRequestId(SYNC_REQUEST_ID)
            .syncedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
            .banksaladUserId(BANKSALAD_USER_ID)
            .organizationId(ORGANIZATION_ID)
            .accountNum(accountNum)
            .prodName("상품명1")
            .seqno("1")
            .isConsent(true)
            .accountStatus("01").build(),
        IrpAccountSummaryEntity.builder()
            .consentId(CONSENT_ID)
            .syncRequestId(SYNC_REQUEST_ID)
            .syncedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
            .banksaladUserId(BANKSALAD_USER_ID)
            .organizationId(ORGANIZATION_ID)
            .accountNum(accountNum)
            .prodName("상품명2")
            .seqno("2")
            .isConsent(true)
            .accountStatus("01").build()
    );

    wiremock.start();

    executionContext = ExecutionContext.builder()
        .consentId(CONSENT_ID)
        .syncRequestId(SYNC_REQUEST_ID)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accessToken("test")
        .organizationHost(ORGANIZATION_HOST + ":" + wiremock.port())
        .organizationCode("020")
        .syncStartedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
        .build();
  }

  @AfterEach
  public void after() {
    wiremock.resetAll();
  }

  @Test
  @DisplayName("1. API 실패")
  void apiFailure() {

    /* api mock server */
    setupMockServer(HttpStatus.INTERNAL_SERVER_ERROR, "100", "IR01_001_single_page_00.json");

    saveUserSyncStatus();

    Exception responseException = assertThrows(
        Exception.class,
        () -> accountSummaryService.listAccountSummaries(executionContext)
    );

    List<IrpAccountSummaryEntity> irpAccountSummaryEntities = accountSummaryRepository.findAll();
    assertEquals(0, irpAccountSummaryEntities.size());
    assertThat(responseException).isInstanceOf(ResponseNotOkException.class);

    assertUserSyncStatus(executionContext);

    List<ApiLogEntity> apiLogEntities = apiLogRepository.findAll().stream()
        .filter(it -> it.getSyncRequestId().equals(executionContext.getSyncRequestId()))
        .collect(Collectors.toList());
    assertEquals(1, apiLogEntities.size());
    assertEquals("50001", apiLogEntities.get(0).getResultCode());
  }

  @Test
  @DisplayName("2. 기존 0건 + 추가 2건")
  void listAccountSummaries_TwoSummaries() throws ResponseNotOkException {

    /* api mock server */
    setupMockServer(HttpStatus.OK, "0", "IR01_002_single_page_00.json");

    List<IrpAccountSummaryEntity> actualIrpAccountSummaries = assertIrpAccountSummaryEntities(2);

    assertUserSyncStatus(executionContext, SEARCH_TIMESTAMP);

    Javers javers = JaversBuilder.javers()
        .registerValueObject(
            ValueObjectDefinitionBuilder.valueObjectDefinition(IrpAccountSummaryEntity.class)
                .withIgnoredProperties("id", "syncedAt",
                    "basicSearchTimestamp", "detailSearchTimestamp",
                    "createdAt", "createdBy",
                    "updatedAt", "updatedBy")
                .build())
        .withListCompareAlgorithm(ListCompareAlgorithm.LEVENSHTEIN_DISTANCE).build();
    Diff diff = javers
        .compareCollections(expectedIrpAccountSummaries, actualIrpAccountSummaries, IrpAccountSummaryEntity.class);

    assertThat(diff.getChanges().size()).isEqualTo(0);
  }

  @Test
  @DisplayName("3. 기존 1건 + 추가 0건")
  void listAccountSummaries_NoAddedSummaries() throws ResponseNotOkException {

    /* api mock server */
    setupMockServer(HttpStatus.OK, String.valueOf(SEARCH_TIMESTAMP), "IR01_003_single_page_00.json");
    setUpPreEnvironment();

    List<IrpAccountSummaryEntity> actualIrpAccountSummaries = assertIrpAccountSummaryEntities(1);

    assertUserSyncStatus(executionContext, SEARCH_TIMESTAMP);

    Javers javers = JaversBuilder.javers()
        .registerValueObject(
            ValueObjectDefinitionBuilder.valueObjectDefinition(IrpAccountSummaryEntity.class)
                .withIgnoredProperties("id",
                    "basicSearchTimestamp", "detailSearchTimestamp",
                    "createdAt", "createdBy",
                    "updatedAt", "updatedBy")
                .build())
        .withListCompareAlgorithm(ListCompareAlgorithm.LEVENSHTEIN_DISTANCE).build();
    Diff diff = javers.compare(expectedIrpAccountSummaries.get(0), actualIrpAccountSummaries.get(0));

    assertThat(diff.getChanges().size()).isEqualTo(0);
  }

  @Test
  @DisplayName("4. 기존 1건 + 추가 1건")
  void listAccountSummaries_OneAddedSummaries() throws ResponseNotOkException {

    int response_searchTimestamp = 200;

    /* api mock server */
    setupMockServer(HttpStatus.OK, String.valueOf(SEARCH_TIMESTAMP), "IR01_004_single_page_00.json");
    setUpPreEnvironment();

    List<IrpAccountSummaryEntity> actualIrpAccountSummaries = assertIrpAccountSummaryEntities(2);

    assertUserSyncStatus(executionContext, response_searchTimestamp);

    Javers javers = JaversBuilder.javers()
        .registerValueObject(
            ValueObjectDefinitionBuilder.valueObjectDefinition(IrpAccountSummaryEntity.class)
                .withIgnoredProperties("id", "syncedAt",
                    "basicSearchTimestamp", "detailSearchTimestamp",
                    "createdAt", "createdBy",
                    "updatedAt", "updatedBy")
                .build())
        .withListCompareAlgorithm(ListCompareAlgorithm.LEVENSHTEIN_DISTANCE).build();
    Diff diff = javers
        .compareCollections(expectedIrpAccountSummaries, actualIrpAccountSummaries, IrpAccountSummaryEntity.class);

    assertThat(diff.getChanges().size()).isEqualTo(0);
  }

  @Test
  @DisplayName("4. 기존 1건 + 동일 1건")
  void listAccountSummaries_SameSummaries() throws ResponseNotOkException {

    int response_searchTimestamp = 200;

    /* api mock server */
    setupMockServer(HttpStatus.OK, String.valueOf(SEARCH_TIMESTAMP), "IR01_005_single_page_00.json");
    setUpPreEnvironment();

    List<IrpAccountSummaryEntity> actualIrpAccountSummaries = assertIrpAccountSummaryEntities(1);

    assertUserSyncStatus(executionContext, response_searchTimestamp);

    Javers javers = JaversBuilder.javers()
        .registerValueObject(
            ValueObjectDefinitionBuilder.valueObjectDefinition(IrpAccountSummaryEntity.class)
                .withIgnoredProperties("id", "syncedAt",
                    "basicSearchTimestamp", "detailSearchTimestamp",
                    "createdAt", "createdBy",
                    "updatedAt", "updatedBy")
                .build())
        .withListCompareAlgorithm(ListCompareAlgorithm.LEVENSHTEIN_DISTANCE).build();
    Diff diff = javers.compare(expectedIrpAccountSummaries.get(0), actualIrpAccountSummaries.get(0));

    assertThat(diff.getChanges().size()).isEqualTo(0);
  }

  @Test
  @DisplayName("5. 기존 1건 + 변경 1건")
  void listAccountSummaries_ModifiedSummaries() throws ResponseNotOkException {

    int response_searchTimestamp = 200;

    /* api mock server */
    setupMockServer(HttpStatus.OK, String.valueOf(SEARCH_TIMESTAMP), "IR01_006_single_page_00.json");
    setUpPreEnvironment();

    List<IrpAccountSummaryEntity> actualIrpAccountSummaries = assertIrpAccountSummaryEntities(1);

    assertUserSyncStatus(executionContext, response_searchTimestamp);

    Javers javers = JaversBuilder.javers()
        .registerValueObject(
            ValueObjectDefinitionBuilder.valueObjectDefinition(IrpAccountSummaryEntity.class)
                .withIgnoredProperties("id", "syncedAt",
                    "basicSearchTimestamp", "detailSearchTimestamp",
                    "createdAt", "createdBy",
                    "updatedAt", "updatedBy")
                .build())
        .withListCompareAlgorithm(ListCompareAlgorithm.LEVENSHTEIN_DISTANCE).build();
    Diff diff = javers.compare(IrpAccountSummaryEntity.builder()
        .consentId(CONSENT_ID)
        .syncRequestId(SYNC_REQUEST_ID)
        .syncedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum("1234123412341234")
        .prodName("상품명1")
        .seqno("1")
        .isConsent(true)
        .accountStatus("01").build(), actualIrpAccountSummaries.get(0));

    assertThat(diff.getChanges().size()).isNotEqualTo(0);
    assertThat(diff.getChanges().getChangesByType(ValueChange.class))
        .extracting(PropertyChange::getPropertyName)
        .containsExactlyInAnyOrder("prodName", "accountStatus");
  }

  private void setUpPreEnvironment() {

    saveUserSyncStatus();

    accountSummaryRepository.save(expectedIrpAccountSummaries.get(0));
  }

  private void saveUserSyncStatus() {
    userSyncStatusRepository.save(
        UserSyncStatusEntity.builder()
            .syncedAt(LocalDateTime.now())
            .searchTimestamp(SEARCH_TIMESTAMP)
            .banksaladUserId(BANKSALAD_USER_ID)
            .organizationId(ORGANIZATION_ID)
            .apiId(Apis.irp_get_accounts.getId())
            .build()
    );
  }

  private List<IrpAccountSummaryEntity> assertIrpAccountSummaryEntities(int irpAccountSummariesCount)
      throws ResponseNotOkException {

    accountSummaryService.listAccountSummaries(executionContext);
    List<IrpAccountSummaryEntity> actualIrpAccountSummaries = accountSummaryRepository.findAll();
    assertThat(actualIrpAccountSummaries.size()).isEqualTo(irpAccountSummariesCount);

    return actualIrpAccountSummaries;
  }

  private void assertUserSyncStatus(ExecutionContext executionContext) {
    assertAccountSummarySearchTimestamp(executionContext, SEARCH_TIMESTAMP);
  }

  private void assertUserSyncStatus(ExecutionContext executionContext, long searchTimestamp) {

    UserSyncStatusEntity userSyncStatusEntity = assertAccountSummarySearchTimestamp(executionContext, searchTimestamp);

    assertThat(userSyncStatusEntity.getSyncedAt()).isEqualTo(executionContext.getSyncStartedAt());
  }

  private UserSyncStatusEntity assertAccountSummarySearchTimestamp(ExecutionContext executionContext,
      long searchTimestamp) {

    UserSyncStatusEntity userSyncStatusEntity = userSyncStatusRepository
        .findByBanksaladUserIdAndOrganizationIdAndApiId(executionContext.getBanksaladUserId(),
            executionContext.getOrganizationId(), Executions.irp_get_accounts.getApi().getId()).orElse(null);

    assertEquals(searchTimestamp, userSyncStatusEntity.getSearchTimestamp());

    return userSyncStatusEntity;
  }

  private void setupMockServer(HttpStatus httpStatus, String searchTimestamp, String fileName) {

    wiremock.stubFor(get(urlMatching("/irps.*"))
        .withQueryParam("org_code", equalTo("020"))
        .withQueryParam("search_timestamp", equalTo(searchTimestamp))
        .willReturn(
            aResponse()
                .withStatus(httpStatus.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/irp/response/" + fileName))));
  }
}
