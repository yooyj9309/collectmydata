package com.banksalad.collectmydata.invest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.spec.internal.HttpStatus;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.finance.api.summary.SummaryRequestHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryResponseHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryService;
import com.banksalad.collectmydata.finance.common.db.entity.UserSyncStatusEntity;
import com.banksalad.collectmydata.finance.common.db.repository.UserSyncStatusRepository;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;
import com.banksalad.collectmydata.invest.collect.Executions;
import com.banksalad.collectmydata.invest.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.invest.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.invest.summary.dto.AccountSummary;
import com.banksalad.collectmydata.invest.summary.dto.ListAccountSummariesRequest;
import com.banksalad.collectmydata.invest.template.ServiceTest;
import com.banksalad.collectmydata.invest.template.provider.AccountSummaryInvocationContextProvider;
import com.github.tomakehurst.wiremock.WireMockServer;
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

@SpringBootTest
@Transactional
@DisplayName("6.4.1 계좌 목록 조회")
class AccountSummaryServiceTest extends ServiceTest<Object, UserSyncStatusEntity, AccountSummaryEntity, Object> {

  @Autowired
  private SummaryService<ListAccountSummariesRequest, AccountSummary> service;

  @Autowired
  private SummaryRequestHelper<ListAccountSummariesRequest> requestHelper;

  @Autowired
  private SummaryResponseHelper<AccountSummary> responseHelper;

  @Autowired
  private AccountSummaryRepository mainRepository;

  @Autowired
  private UserSyncStatusRepository parentRepository;

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
  @ExtendWith(AccountSummaryInvocationContextProvider.class)
  void unitTests(TestCase<Object, UserSyncStatusEntity, AccountSummaryEntity, Object> testCase) throws ResponseNotOkException {
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
  protected void saveMains(List<AccountSummaryEntity> accountSummaryEntities) {
    accountSummaryEntities
        .forEach(accountSummaryEntity -> mainRepository.save(accountSummaryEntity.toBuilder().build()));
  }

  @Override
  protected void saveChildren(List<Object> objects) {

  }

  @Override
  protected void runMainService(TestCase<Object, UserSyncStatusEntity, AccountSummaryEntity, Object> testCase)
      throws ResponseNotOkException {

    service
        .listAccountSummaries(testCase.getExecutionContext(), Executions.finance_invest_accounts, requestHelper,
            responseHelper);
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
  protected void validateMains(List<AccountSummaryEntity> expectedMains) {
    final List<AccountSummaryEntity> actualMains = mainRepository.findAll();

    assertAll("*** AccountSummaryEntity 확인 ***",
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
