package com.banksalad.collectmydata.insu.loan;

import com.banksalad.collectmydata.finance.api.transaction.TransactionApiService;
import com.banksalad.collectmydata.finance.api.transaction.TransactionRequestHelper;
import com.banksalad.collectmydata.finance.api.transaction.TransactionResponseHelper;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;
import com.banksalad.collectmydata.insu.common.db.entity.LoanSummaryEntity;
import com.banksalad.collectmydata.insu.common.db.entity.LoanTransactionEntity;
import com.banksalad.collectmydata.insu.common.db.repository.LoanSummaryRepository;
import com.banksalad.collectmydata.insu.common.db.repository.LoanTransactionRepository;
import com.banksalad.collectmydata.insu.loan.dto.ListLoanTransactionRequest;
import com.banksalad.collectmydata.insu.loan.dto.LoanTransaction;
import com.banksalad.collectmydata.insu.summary.dto.LoanSummary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.insu.template.ServiceTest;
import com.banksalad.collectmydata.insu.template.provider.LoanTransactionInvocationContextProvider;
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

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Transactional
@DisplayName("??????-011 ???????????? ???????????? ??????")
class LoanTransactionServiceTest extends ServiceTest<Object, LoanSummaryEntity, LoanTransactionEntity, Object> {

  @Autowired
  private TransactionApiService<LoanSummary, ListLoanTransactionRequest, LoanTransaction> mainService;
  @Autowired
  private TransactionRequestHelper<LoanSummary, ListLoanTransactionRequest> requestHelper;
  @Autowired
  private TransactionResponseHelper<LoanSummary, LoanTransaction> responseHelper;
  @Autowired
  private LoanSummaryRepository parentRepository;
  @Autowired
  private LoanTransactionRepository mainRepository;

  private static final WireMockServer wireMockServer = new WireMockServer(WireMockSpring.options().dynamicPort());

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
  @ExtendWith(LoanTransactionInvocationContextProvider.class)
  public void unitTests(TestCase<Object, LoanSummaryEntity, LoanTransactionEntity, Object> testCase) {

    prepare(testCase, wireMockServer);

    runMainService(testCase);

    validate(testCase);
  }

  @Override
  protected void saveGParents(List<Object> objects) {

  }

  @Override
  protected void saveParents(List<LoanSummaryEntity> loanSummaryEntities) {

    /* DB save()??? ????????? testCase??? summaries??? ??????????????? ???????????? ???????????? ??????. */
    loanSummaryEntities
        .forEach(loanSummaryEntity -> parentRepository.save(loanSummaryEntity.toBuilder().build()));
  }

  @Override
  protected void saveMains(List<LoanTransactionEntity> loanTransactionEntities) {

    loanTransactionEntities
        .forEach(loanTransactionEntity -> mainRepository.save(loanTransactionEntity.toBuilder().build()));
  }

  @Override
  protected void saveChildren(List<Object> objects) {

  }

  @Override
  protected void runMainService(
      TestCase<Object, LoanSummaryEntity, LoanTransactionEntity, Object> testCase) {

    mainService
        .listTransactions(testCase.getExecutionContext(), testCase.getExecution(), requestHelper, responseHelper);
  }

  @Override
  protected void validateParents(List<LoanSummaryEntity> expectedParents) {

    final List<LoanSummaryEntity> actualParents = parentRepository.findAll();

    assertAll("*** Parent ?????? ***",
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
  protected void validateMains(List<LoanTransactionEntity> expectedMains) {

    final List<LoanTransactionEntity> actualMains = mainRepository.findAll();

    assertAll("*** Main ?????? ***",
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
