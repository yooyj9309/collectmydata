package com.banksalad.collectmydata.card.loan;

import com.banksalad.collectmydata.card.loan.dto.ListLoanLongTermsRequest;
import com.banksalad.collectmydata.card.loan.dto.LoanLongTerm;
import com.banksalad.collectmydata.card.collect.Executions;
import com.banksalad.collectmydata.card.common.db.entity.LoanLongTermEntity;
import com.banksalad.collectmydata.card.common.db.entity.LoanLongTermHistoryEntity;
import com.banksalad.collectmydata.card.common.db.repository.LoanLongTermHistoryRepository;
import com.banksalad.collectmydata.card.common.db.repository.LoanLongTermRepository;
import com.banksalad.collectmydata.card.util.TestHelper;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.userbase.UserBaseRequestHelper;
import com.banksalad.collectmydata.finance.api.userbase.UserBaseResponseHelper;
import com.banksalad.collectmydata.finance.api.userbase.UserBaseService;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;

import com.github.tomakehurst.wiremock.WireMockServer;
import javax.transaction.Transactional;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.banksalad.collectmydata.card.util.FileUtil.readText;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest
public class LoanLongTermServiceTest {

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

  private static WireMockServer wireMockServer;

  @BeforeAll
  static void setup() {

    wireMockServer = new WireMockServer(WireMockSpring.options().dynamicPort());
    wireMockServer.start();
    setupMockServer();
  }

  @AfterAll
  static void tearDown() {
    wireMockServer.shutdown();
  }

  @Test
  @DisplayName("6.3.12 장기대출 정보 조회")
  public void listLoanLongTerms() throws ResponseNotOkException {

    // FIXME: 정해질 테스트 케이스에 따라 작성
    ExecutionContext context = TestHelper.getExecutionContext(wireMockServer.port());

    loanLongTermService.getUserBaseInfo(context, Executions.finance_loan_long_terms, requestHelper, responseHelper);

    List<LoanLongTermEntity> loanLongTermEntities = loanLongTermRepository.findAll();
    List<LoanLongTermHistoryEntity> loanLongTermHistoryEntities = loanLongTermHistoryRepository.findAll();
    assertEquals(2, loanLongTermEntities.size());
    assertEquals(2, loanLongTermHistoryEntities.size());
  }

  private static void setupMockServer() {

    wireMockServer.stubFor(get(urlMatching("/loans/long-term.*"))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/CD34_001_single_page_00.json"))));
  }
}
