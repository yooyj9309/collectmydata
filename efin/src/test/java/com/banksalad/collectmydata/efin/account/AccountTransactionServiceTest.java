package com.banksalad.collectmydata.efin.account;

import com.banksalad.collectmydata.efin.account.dto.AccountTransaction;
import com.banksalad.collectmydata.efin.account.dto.ListAccountTransactionsRequest;
import com.banksalad.collectmydata.efin.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.efin.common.db.entity.AccountTransactionEntity;
import com.banksalad.collectmydata.efin.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.efin.common.db.repository.AccountTransactionRepository;
import com.banksalad.collectmydata.efin.summary.dto.AccountSummary;
import com.banksalad.collectmydata.efin.template.ServiceTest;
import com.banksalad.collectmydata.efin.template.provider.AccountTransactionInvocationContextProvider;
import com.banksalad.collectmydata.finance.api.transaction.TransactionApiService;
import com.banksalad.collectmydata.finance.api.transaction.TransactionRequestHelper;
import com.banksalad.collectmydata.finance.api.transaction.TransactionResponseHelper;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Transactional
@DisplayName("전금-004 선불 거래내역 조회")
public class AccountTransactionServiceTest extends
    ServiceTest<Object, AccountSummaryEntity, AccountTransactionEntity, Object> {

  private static final WireMockServer wireMockServer = new WireMockServer(WireMockSpring.options().dynamicPort());
  @Autowired
  private TransactionApiService<AccountSummary, ListAccountTransactionsRequest, AccountTransaction> mainService;
  @Autowired
  private TransactionRequestHelper<AccountSummary, ListAccountTransactionsRequest> requestHelper;
  @Autowired
  private TransactionResponseHelper<AccountSummary, AccountTransaction> responseHelper;
  @Autowired
  private AccountSummaryRepository parentRepository;
  @Autowired
  private AccountTransactionRepository mainRepository;

  @BeforeAll
  static void setUp() {

    wireMockServer.start();
  }

  @AfterAll
  static void shutDown() {

    wireMockServer.shutdown();
  }

  @AfterEach
  void tearDown() {

    wireMockServer.resetAll();
  }

  @TestTemplate
  @ExtendWith(AccountTransactionInvocationContextProvider.class)
  public void unitTests(TestCase<Object, AccountSummaryEntity, AccountTransactionEntity, Object> testCase) {

    prepare(testCase, wireMockServer);

    runMainService(testCase);

    validate(testCase);
  }

  @Override
  protected void saveGParents(List<Object> objects) {

  }

  @Override
  protected void saveParents(List<AccountSummaryEntity> accountSummaryEntities) {

    /* DB save()에 의해서 testCase의 summaries가 오염되므로 복제본을 만들어야 한다. */
    accountSummaryEntities
        .forEach(accountSummaryEntity -> parentRepository.save(accountSummaryEntity.toBuilder().build()));
  }

  @Override
  protected void saveMains(List<AccountTransactionEntity> depositAccountBasicEntities) {

    depositAccountBasicEntities
        .forEach(depositAccountBasicEntity -> mainRepository.save(depositAccountBasicEntity.toBuilder().build()));
  }

  @Override
  protected void saveChildren(List<Object> objects) {

  }

  @Override
  protected void runMainService(
      TestCase<Object, AccountSummaryEntity, AccountTransactionEntity, Object> testCase) {

    mainService
        .listTransactions(testCase.getExecutionContext(), testCase.getExecution(), requestHelper, responseHelper);
  }

  @Override
  protected void validateGParents(List<Object> expectedGParents) {
    
  }

  @Override
  protected void validateParents(List<AccountSummaryEntity> expectedParents) {

    final List<AccountSummaryEntity> actualParents = parentRepository.findAll();

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
  protected void validateMains(List<AccountTransactionEntity> expectedMains) {

    final List<AccountTransactionEntity> actualMains = mainRepository.findAll();

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
