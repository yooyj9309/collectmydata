package com.banksalad.collectmydata.irp.summary;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.spec.internal.HttpStatus;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.finance.api.summary.SummaryRequestHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryResponseHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryService;
import com.banksalad.collectmydata.finance.common.db.entity.UserSyncStatusEntity;
import com.banksalad.collectmydata.finance.common.db.repository.UserSyncStatusRepository;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;
import com.banksalad.collectmydata.irp.collect.Executions;
import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountSummaryEntity;
import com.banksalad.collectmydata.irp.common.db.repository.IrpAccountSummaryRepository;
import com.banksalad.collectmydata.irp.common.dto.IrpAccountSummary;
import com.banksalad.collectmydata.irp.common.dto.ListIrpAccountSummariesRequest;
import com.banksalad.collectmydata.irp.template.ServiceTest;
import com.banksalad.collectmydata.irp.template.provider.IrpAccountSummaryInvocationContextProvider;
import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.IGNORING_ENTITY_FIELDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@DisplayName("6.1.3 개인형 IRP 계좌 목록 조회")
@RequiredArgsConstructor
class IrpAccountSummaryServiceTestTemplateTest extends
    ServiceTest<Object, UserSyncStatusEntity, IrpAccountSummaryEntity, Object> {

  private final SummaryService<ListIrpAccountSummariesRequest, IrpAccountSummary> service;

  private final SummaryRequestHelper<ListIrpAccountSummariesRequest> requestHelper;

  private final SummaryResponseHelper<IrpAccountSummary> responseHelper;

  private final IrpAccountSummaryRepository mainRepository;

  private final UserSyncStatusRepository parentRepository;

  private static final WireMockServer wireMockServer = new WireMockServer(WireMockSpring.options().dynamicPort());

  @BeforeAll
  static void setUp() {
    wireMockServer.start();
  }

  @AfterEach
  void tearDown() {
    wireMockServer.resetAll();
  }

  @AfterAll
  static void tearDownAll() {
    wireMockServer.stop();
  }

  @TestTemplate
  @ExtendWith(IrpAccountSummaryInvocationContextProvider.class)
  void unitTests(TestCase<Object, UserSyncStatusEntity, IrpAccountSummaryEntity, Object> testCase)
      throws ResponseNotOkException {

    prepare(testCase, wireMockServer);

    final Integer status = testCase.getExpectedResponses().get(testCase.getExpectedResponses().size() - 1).getStatus();

    if (status != null && status != HttpStatus.OK) {
      runAndTestException(testCase);
    } else {
      runMainService(testCase);
    }
    validate(testCase);
  }

  @Override
  protected void saveGParents(List<Object> objects) {

  }

  @Override
  protected void saveParents(List<UserSyncStatusEntity> userSyncStatusEntities) {
    userSyncStatusEntities
        .forEach(userSyncStatusEntity -> parentRepository.save(userSyncStatusEntity.toBuilder().build()));
  }

  @Override
  protected void saveMains(List<IrpAccountSummaryEntity> accountSummaryEntities) {
    accountSummaryEntities
        .forEach(accountSummaryEntity -> mainRepository.save(accountSummaryEntity.toBuilder().build()));
  }

  @Override
  protected void saveChildren(List<Object> objects) {

  }

  @Override
  protected void runMainService(TestCase<Object, UserSyncStatusEntity, IrpAccountSummaryEntity, Object> testCase)
      throws ResponseNotOkException {

    service
        .listAccountSummaries(testCase.getExecutionContext(), Executions.irp_get_accounts, requestHelper,
            responseHelper);
  }

  @Override
  protected void validateGParents(List<Object> expectedGParents) {

  }

  @Override
  protected void validateParents(List<UserSyncStatusEntity> expectedParents) {

    final List<UserSyncStatusEntity> actualParents = parentRepository.findAll();

    assertAll("*** UserSyncStatusEntity 확인 ***",
        () -> assertEquals(expectedParents.size(), actualParents.size()),
        () -> {
          for (int i = 0; i < expectedParents.size(); i++) {
            assertThat(actualParents.get(i)).usingRecursiveComparison().ignoringFields(IGNORING_ENTITY_FIELDS)
                .isEqualTo(expectedParents.get(i));
          }
        }
    );
  }

  @Override
  protected void validateMains(List<IrpAccountSummaryEntity> expectedMains) {

    final List<IrpAccountSummaryEntity> actualMains = mainRepository.findAll();

    assertAll("*** IrpAccountSummaryEntity 확인 ***",
        () -> assertEquals(expectedMains.size(), actualMains.size()),
        () -> {
          for (int i = 0; i < expectedMains.size(); i++) {
            assertThat(actualMains.get(i)).usingRecursiveComparison().ignoringFields(IGNORING_ENTITY_FIELDS)
                .isEqualTo(expectedMains.get(i));
          }
        }
    );
  }

  @Override
  protected void validateChildren(List<Object> expectedChildren) {

  }
}
