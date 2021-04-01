package com.banksalad.collectmydata.card.loan;

import com.banksalad.collectmydata.card.loan.dto.ListLoanShortTermsRequest;
import com.banksalad.collectmydata.card.loan.dto.LoanShortTerm;
import com.banksalad.collectmydata.card.collect.Executions;
import com.banksalad.collectmydata.card.common.db.entity.LoanShortTermEntity;
import com.banksalad.collectmydata.card.common.db.entity.LoanShortTermHistoryEntity;
import com.banksalad.collectmydata.card.common.db.repository.LoanShortTermHistoryRepository;
import com.banksalad.collectmydata.card.common.db.repository.LoanShortTermRepository;
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
public class LoanShortTermServiceTest {

  @Autowired
  private UserBaseService<ListLoanShortTermsRequest, List<LoanShortTerm>> loanShortTermService;

  @Autowired
  private UserBaseRequestHelper<ListLoanShortTermsRequest> requestHelper;

  @Autowired
  private UserBaseResponseHelper<List<LoanShortTerm>> responseHelper;

  @Autowired
  private LoanShortTermRepository loanShortTermRepository;

  @Autowired
  private LoanShortTermHistoryRepository loanShortTermHistoryRepository;

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
  @DisplayName("6.3.11 단기대출 정보 조회")
  public void listLoanShortTerms() throws ResponseNotOkException {

    // FIXME: 정해질 테스트 케이스에 따라 작성
    ExecutionContext context = TestHelper.getExecutionContext(wireMockServer.port());

    loanShortTermService.getUserBaseInfo(context, Executions.finance_loan_short_terms, requestHelper, responseHelper);

    List<LoanShortTermEntity> loanShortTermEntities = loanShortTermRepository.findAll();
    List<LoanShortTermHistoryEntity> loanShortTermHistoryEntities = loanShortTermHistoryRepository.findAll();
    assertEquals(2, loanShortTermEntities.size());
    assertEquals(2, loanShortTermHistoryEntities.size());
  }

  private static void setupMockServer() {

    wireMockServer.stubFor(get(urlMatching("/loans/short-term.*"))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/CD33_001_single_page_00.json"))));
  }
}
