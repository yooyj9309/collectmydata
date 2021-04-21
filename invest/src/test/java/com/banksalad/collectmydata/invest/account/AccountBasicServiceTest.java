package com.banksalad.collectmydata.invest.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoRequestHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoService;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;
import com.banksalad.collectmydata.invest.account.dto.AccountBasic;
import com.banksalad.collectmydata.invest.account.dto.GetAccountBasicRequest;
import com.banksalad.collectmydata.invest.common.db.entity.AccountBasicEntity;
import com.banksalad.collectmydata.invest.common.db.entity.AccountBasicHistoryEntity;
import com.banksalad.collectmydata.invest.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.invest.common.db.repository.AccountBasicHistoryRepository;
import com.banksalad.collectmydata.invest.common.db.repository.AccountBasicRepository;
import com.banksalad.collectmydata.invest.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.invest.summary.dto.AccountSummary;
import com.banksalad.collectmydata.invest.template.ServiceTest;
import com.banksalad.collectmydata.invest.template.provider.AccountBasicInvocationContextProvider;
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
@DisplayName("6.4.2 계좌 기본정보 조회 테스트")
class AccountBasicServiceTest extends ServiceTest<Object, AccountSummaryEntity, AccountBasicEntity, Object> {

  private static final WireMockServer wireMockServer = new WireMockServer(WireMockSpring.options().dynamicPort());

  @Autowired
  private AccountInfoService<AccountSummary, GetAccountBasicRequest, AccountBasic> service;

  @Autowired
  private AccountInfoRequestHelper<GetAccountBasicRequest, AccountSummary> requestHelper;

  @Autowired
  private AccountInfoResponseHelper<AccountSummary, AccountBasic> responseHelper;

  @Autowired
  private AccountSummaryRepository accountSummaryRepository;

  @Autowired
  private AccountBasicRepository accountBasicRepository;

  @Autowired
  private AccountBasicHistoryRepository accountBasicHistoryRepository;

  @BeforeAll
  static void setup() {
    wireMockServer.start();
  }

  @AfterEach
  void tearDown() {
    wireMockServer.resetAll();
  }

  @AfterAll
  static void tearDownAll() {
    wireMockServer.shutdown();
  }

  @TestTemplate
  @ExtendWith(AccountBasicInvocationContextProvider.class)
  void unitTests(TestCase<Object, AccountSummaryEntity, AccountBasicEntity, Object> testCase)
      throws ResponseNotOkException {

    prepare(testCase, wireMockServer);

    runMainService(testCase);

    validate(testCase);
  }

  @Override
  protected void saveGParents(List<Object> objects) {

  }

  @Override
  protected void saveParents(List<AccountSummaryEntity> accountSummaryEntities) {
    accountSummaryEntities
        .forEach(accountSummaryEntity -> accountSummaryRepository.save(accountSummaryEntity.toBuilder().build()));
  }

  @Override
  protected void saveMains(List<AccountBasicEntity> accountBasicEntities) {
    accountBasicEntities.forEach(accountBasicEntity -> accountBasicRepository.save(accountBasicEntity.toBuilder().build()));
  }

  @Override
  protected void saveChildren(List<Object> objects) {

  }

  @Override
  protected void runMainService(TestCase<Object, AccountSummaryEntity, AccountBasicEntity, Object> testCase)
      throws ResponseNotOkException {

    service
        .listAccountInfos(testCase.getExecutionContext(), testCase.getExecution(), requestHelper, responseHelper);
  }

  @Override
  protected void validateParents(List<AccountSummaryEntity> expectedParents) {
    final List<AccountSummaryEntity> actualParents = accountSummaryRepository.findAll();

    assertAll("*** AccountSummaryEntity 확인 ***",
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
  protected void validateMains(List<AccountBasicEntity> expectedMains) {
    final List<AccountBasicEntity> actualMains = accountBasicRepository.findAll();

    assertAll("*** AccountBasicEntity 확인 ***",
        () -> assertEquals(expectedMains.size(), actualMains.size()),
        () -> {
          for (int i = 0; i < expectedMains.size(); i++) {
            assertThat(actualMains.get(i)).usingRecursiveComparison().ignoringFields(IGNORING_ENTITY_FIELDS)
                .isEqualTo(expectedMains.get(i));
          }
        }
    );

    final List<AccountBasicHistoryEntity> actualHistories = accountBasicHistoryRepository.findAll();

    if (actualHistories.size() > 0) {
      assertAll("AccountBasicHistoryEntity 확인",
          () -> assertThat(actualMains.get(actualMains.size() - 1)).usingRecursiveComparison()
              .ignoringFields(IGNORING_ENTITY_FIELDS).isEqualTo(actualHistories.get(actualHistories.size() - 1))
      );
    }
  }

  @Override
  protected void validateChildren(List<Object> expectedChildren) {

  }
}
