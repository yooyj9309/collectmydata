package com.banksalad.collectmydata.referencebank.deposit;

import com.banksalad.collectmydata.finance.api.summary.SummaryRequestHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryResponseHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryService;
import com.banksalad.collectmydata.finance.common.db.entity.UserSyncStatusEntity;
import com.banksalad.collectmydata.finance.common.db.repository.UserSyncStatusRepository;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;
import com.banksalad.collectmydata.referencebank.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.referencebank.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.referencebank.summary.dto.AccountSummary;
import com.banksalad.collectmydata.referencebank.summary.dto.ListAccountSummariesRequest;
import com.banksalad.collectmydata.referencebank.template.ServiceTest;
import com.banksalad.collectmydata.referencebank.template.provider.AccountSummaryInvocationContextProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.cloud.contract.wiremock.WireMockSpring;

import com.github.tomakehurst.wiremock.WireMockServer;
import javax.transaction.Transactional;
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

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Transactional
@DisplayName("레퍼런스은행-001 계좌 목록 조회")
public class AccountSummaryServiceTest extends
    ServiceTest<Object, UserSyncStatusEntity, AccountSummaryEntity, Object> {

  private static final WireMockServer wireMockServer = new WireMockServer(WireMockSpring.options().dynamicPort());
  @Autowired
  private SummaryService<ListAccountSummariesRequest, AccountSummary> mainService;
  @Autowired
  private SummaryRequestHelper<ListAccountSummariesRequest> requestHelper;
  @Autowired
  private SummaryResponseHelper<AccountSummary> responseHelper;
  @Autowired
  private UserSyncStatusRepository parentRepository;
  @Autowired
  private AccountSummaryRepository mainRepository;

  @BeforeAll
  static void setUp() {

    wireMockServer.start();
  }

  @AfterAll
  static void shutDown() {

    wireMockServer.stop();
  }

  @AfterEach
  void tearDown() {

    wireMockServer.resetAll();
  }

  @TestTemplate
  @ExtendWith(AccountSummaryInvocationContextProvider.class)
  public void unitTests(TestCase<Object, UserSyncStatusEntity, AccountSummaryEntity, Object> testCase)
      throws ResponseNotOkException {

    prepare(testCase, wireMockServer);

    final Integer status = testCase.getExpectedResponses().get(testCase.getExpectedResponses().size() - 1).getStatus();

    if (status != null && status != 200) { // if (mainService instanceof SummaryService)
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

    mainService
        .listAccountSummaries(testCase.getExecutionContext(), testCase.getExecution(), requestHelper, responseHelper);
  }

  @Override
  protected void validateGParents(List<Object> expectedGParents) {
    
  }

  @Override
  protected void validateParents(List<UserSyncStatusEntity> expectedParents) {

    final List<UserSyncStatusEntity> actualParents = parentRepository.findAll();

    assertAll("*** Parent 확인 ***",
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

    assertAll("*** Main 확인 ***",
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
