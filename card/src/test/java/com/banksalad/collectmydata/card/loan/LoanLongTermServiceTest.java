package com.banksalad.collectmydata.card.loan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;

import com.banksalad.collectmydata.card.common.db.entity.LoanLongTermEntity;
import com.banksalad.collectmydata.card.common.db.repository.LoanLongTermHistoryRepository;
import com.banksalad.collectmydata.card.common.db.repository.LoanLongTermRepository;
import com.banksalad.collectmydata.card.loan.dto.ListLoanLongTermsRequest;
import com.banksalad.collectmydata.card.loan.dto.LoanLongTerm;
import com.banksalad.collectmydata.card.template.ServiceTest;
import com.banksalad.collectmydata.card.template.provider.LoanLongTermInvocationContextProvider;
import com.banksalad.collectmydata.finance.api.userbase.UserBaseRequestHelper;
import com.banksalad.collectmydata.finance.api.userbase.UserBaseResponseHelper;
import com.banksalad.collectmydata.finance.api.userbase.UserBaseService;
import com.banksalad.collectmydata.finance.common.db.entity.UserSyncStatusEntity;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;
import com.github.tomakehurst.wiremock.WireMockServer;
import javax.transaction.Transactional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

@SpringBootTest
@Transactional
@DisplayName("카드-012 장기대출 정보 조회")
public class LoanLongTermServiceTest extends ServiceTest<Object, UserSyncStatusEntity, LoanLongTermEntity, Object> {

  @Autowired
  private UserBaseService<ListLoanLongTermsRequest, List<LoanLongTerm>> loanLongTermService;

  @Autowired
  private UserBaseRequestHelper<ListLoanLongTermsRequest> requestHelper;

  @Autowired
  private UserBaseResponseHelper<List<LoanLongTerm>> responseHelper;

  @Autowired
  private LoanLongTermRepository loanLongTermRepository;

  @Autowired
  private LoanLongTermHistoryRepository loanLongTermHistoryRepository;

  private static WireMockServer wireMockServer = new WireMockServer(WireMockSpring.options().dynamicPort());

  @BeforeAll
  static void setup() {
    wireMockServer.start();
  }

  @AfterEach
  void tearDown() {
    wireMockServer.resetAll();
  }

  @AfterAll
  static void shutDown() {
    wireMockServer.shutdown();
  }

  @TestTemplate
  @ExtendWith(LoanLongTermInvocationContextProvider.class)
  public void unitTests(TestCase<Object, UserSyncStatusEntity, LoanLongTermEntity, Object> testCase) throws ResponseNotOkException {

    prepare(testCase, wireMockServer);

    final Integer status = testCase.getExpectedResponses().get(testCase.getExpectedResponses().size() - 1).getStatus();

    if (status != null && status != 200) {
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

  }

  @Override
  protected void saveMains(List<LoanLongTermEntity> loanLongTermEntities) {

  }

  @Override
  protected void saveChildren(List<Object> objects) {

  }

  @Override
  protected void runMainService(TestCase<Object, UserSyncStatusEntity, LoanLongTermEntity, Object> testCase)
      throws ResponseNotOkException {
    throw new ResponseNotOkException(500, "50001", "responseMessage");


  }

  @Override
  protected void validateParents(List<UserSyncStatusEntity> expectedParents) {

  }

  @Override
  protected void validateMains(List<LoanLongTermEntity> expectedMains) {

  }

  @Override
  protected void validateChildren(List<Object> expectedChildren) {

  }
}
