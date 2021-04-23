package com.banksalad.collectmydata.efin.summary;

import com.banksalad.collectmydata.efin.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.efin.common.db.entity.AccountSummaryPayEntity;
import com.banksalad.collectmydata.efin.common.db.entity.OrganizationUserEntity;
import com.banksalad.collectmydata.efin.common.db.repository.AccountSummaryPayRepository;
import com.banksalad.collectmydata.efin.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.efin.common.db.repository.OrganizationUserRepository;
import com.banksalad.collectmydata.efin.summary.dto.AccountSummary;
import com.banksalad.collectmydata.efin.summary.dto.ListAccountSummariesRequest;
import com.banksalad.collectmydata.efin.template.ServiceTest;
import com.banksalad.collectmydata.efin.template.provider.AccountSummaryInvocationContextProvider;
import com.banksalad.collectmydata.finance.api.summary.SummaryRequestHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryResponseHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryService;
import com.banksalad.collectmydata.finance.common.db.entity.UserSyncStatusEntity;
import com.banksalad.collectmydata.finance.common.db.repository.UserSyncStatusRepository;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;

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
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.STATUS_OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Transactional
@DisplayName("전금-001 전자지급수단 목록 조회")
public class AccountSummaryServiceTest extends
    ServiceTest<UserSyncStatusEntity, OrganizationUserEntity, AccountSummaryEntity, AccountSummaryPayEntity> {

  private static final WireMockServer wireMockServer = new WireMockServer(WireMockSpring.options().dynamicPort());

  @Autowired
  private SummaryService<ListAccountSummariesRequest, AccountSummary> mainService;

  @Autowired
  private SummaryRequestHelper<ListAccountSummariesRequest> requestHelper;

  @Autowired
  private SummaryResponseHelper<AccountSummary> responseHelper;

  @Autowired
  private UserSyncStatusRepository gParentRepository;

  @Autowired
  private OrganizationUserRepository parentRepository;

  @Autowired
  private AccountSummaryRepository mainRepository;

  @Autowired
  private AccountSummaryPayRepository childRepository;

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
  public void unitTests(
      TestCase<UserSyncStatusEntity, OrganizationUserEntity, AccountSummaryEntity, AccountSummaryPayEntity> testCase)
      throws ResponseNotOkException {

    prepare(testCase, wireMockServer);

    final Integer status = testCase.getExpectedResponses().get(testCase.getExpectedResponses().size() - 1).getStatus();

    if (status != null && status != STATUS_OK) { // if (mainService instanceof SummaryService)
      runAndTestException(testCase);
    } else {
      runMainService(testCase);
    }
    validate(testCase);
  }

  @Override
  protected void saveGParents(List<UserSyncStatusEntity> userSyncStatusEntities) {

    userSyncStatusEntities
        .forEach(userSyncStatusEntity -> gParentRepository.save(userSyncStatusEntity.toBuilder().build()));
  }

  @Override
  protected void saveParents(List<OrganizationUserEntity> organizationUserEntities) {

    organizationUserEntities
        .forEach(organizationUserEntity -> parentRepository.save(organizationUserEntity.toBuilder().build()));
  }

  @Override
  protected void saveMains(List<AccountSummaryEntity> accountSummaryEntities) {

    accountSummaryEntities
        .forEach(accountSummaryEntity -> mainRepository.save(accountSummaryEntity.toBuilder().build()));
  }

  @Override
  protected void saveChildren(List<AccountSummaryPayEntity> accountSummaryPayEntities) {

    accountSummaryPayEntities
        .forEach(accountSummaryPayEntity -> childRepository.save(accountSummaryPayEntity.toBuilder().build()));
  }

  @Override
  protected void runMainService(
      TestCase<UserSyncStatusEntity, OrganizationUserEntity, AccountSummaryEntity, AccountSummaryPayEntity> testCase)
      throws ResponseNotOkException {

    mainService
        .listAccountSummaries(testCase.getExecutionContext(), testCase.getExecution(), requestHelper, responseHelper);
  }

  @Override
  protected void validateGParents(List<UserSyncStatusEntity> expectedGParents) {

    final List<UserSyncStatusEntity> actualGParents = gParentRepository.findAll();

    assertAll("*** GParent 확인 ***",
        () -> assertEquals(expectedGParents.size(), actualGParents.size()),
        () -> {
          for (int i = 0; i < expectedGParents.size(); i++) {
            assertThat(actualGParents.get(i)).usingRecursiveComparison().ignoringFields(IGNORING_ENTITY_FIELDS)
                .isEqualTo(expectedGParents.get(i));
          }
        }
    );
  }

  @Override
  protected void validateParents(List<OrganizationUserEntity> expectedParents) {

    final List<OrganizationUserEntity> actualParents = parentRepository.findAll();

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
  protected void validateChildren(List<AccountSummaryPayEntity> expectedChildren) {

    final List<AccountSummaryPayEntity> actualChildren = childRepository.findAll();

    assertAll("*** Child 확인 ***",
        () -> assertEquals(expectedChildren.size(), actualChildren.size()),
        () -> {
          for (int i = 0; i < expectedChildren.size(); i++) {
            assertThat(actualChildren.get(i)).usingRecursiveComparison().ignoringFields(IGNORING_ENTITY_FIELDS)
                .isEqualTo(expectedChildren.get(i));
          }
        }
    );
  }
}
