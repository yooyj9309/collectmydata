package com.banksalad.collectmydata.capital.summary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.capital.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.capital.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.capital.summary.dto.AccountSummary;
import com.banksalad.collectmydata.capital.summary.dto.ListAccountSummariesRequest;
import com.banksalad.collectmydata.capital.template.CapitalSummaryInvocationContextProvider;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.summary.SummaryRequestHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryResponseHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryService;
import com.banksalad.collectmydata.finance.common.db.repository.ApiLogRepository;
import com.banksalad.collectmydata.finance.common.db.repository.UserSyncStatusRepository;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.banksalad.collectmydata.finance.test.template.dto.BareResponse;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static com.banksalad.collectmydata.capital.common.TestHelper.ENTITY_IGNORE_FIELD;
import static com.banksalad.collectmydata.capital.common.TestHelper.ORGANIZATION_CODE;
import static com.banksalad.collectmydata.capital.common.TestHelper.getExecutionContext;
import static com.banksalad.collectmydata.capital.util.FileUtil.readText;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
public class AccountSummaryServiceTest {

  @Autowired
  private SummaryService<ListAccountSummariesRequest, AccountSummary> accountSummaryService;

  @Autowired
  private SummaryRequestHelper<ListAccountSummariesRequest> accountSummaryRequestHelper;

  @Autowired
  private SummaryResponseHelper<AccountSummary> accountSummaryResponseHelper;

  @Autowired
  private AccountSummaryRepository accountSummaryRepository;

  @Autowired
  private UserSyncStatusRepository userSyncStatusRepository;

  @Autowired
  private ApiLogRepository apiLogRepository;

  private static WireMockServer wireMockServer;


  @BeforeAll
  static void setup() {
    wireMockServer = new WireMockServer(WireMockSpring.options().dynamicPort());
    wireMockServer.start();
  }

  @AfterEach
  void cleanBefore() {
    accountSummaryRepository.deleteAll(); // dusang, 지워야하나 다른 테스트에서 save가 도중에 되는경우가 존재, 추후 삭제
    wireMockServer.resetAll();
  }

  @AfterAll
  static void tearDown() {
    wireMockServer.shutdown();
  }

  @TestTemplate
  @ExtendWith(CapitalSummaryInvocationContextProvider.class)
  public void unitTest(TestCase testCase) throws ResponseNotOkException {
    // executionContext생성
    ExecutionContext executionContext = getExecutionContext(wireMockServer.port());

    // searchTimestamp, userSyncStatus 적재
    long searchTimestamp = 0l;
    if (testCase.getUserSyncStatusEntities() != null && testCase.getUserSyncStatusEntities().size() != 0) {
      userSyncStatusRepository.save(testCase.getUserSyncStatusEntities().get(0));
      searchTimestamp = testCase.getUserSyncStatusEntities().get(0).getSearchTimestamp();
    }

    if (testCase.getSummaryEntities() != null) {
      for (Object accountSummaryEntity : testCase.getSummaryEntities()) {
        accountSummaryRepository.save((AccountSummaryEntity) accountSummaryEntity);
      }
    }

    stubForListSummary(searchTimestamp, testCase.getExpectedResponses());

    if (testCase.isErrorOccurred()) {
      Exception responseException = assertThrows(
          Exception.class,
          () -> accountSummaryService
              .listAccountSummaries(executionContext, testCase.getExecution(), accountSummaryRequestHelper,
                  accountSummaryResponseHelper)
      );
      assertThat(responseException).isInstanceOf(testCase.getExpectedExceptionClazz());
    } else {
      accountSummaryService.listAccountSummaries(executionContext, testCase.getExecution(), accountSummaryRequestHelper,
          accountSummaryResponseHelper);

      List<AccountSummaryEntity> accountSummaryEntities = accountSummaryRepository.findAll();
      for (int idx = 0; idx < accountSummaryEntities.size(); idx++) {
        assertThat(accountSummaryEntities.get(idx)).usingRecursiveComparison()
            .ignoringFields(ENTITY_IGNORE_FIELD)
            .isEqualTo(testCase.getExpectedMainEntities().get(idx));
      }
    }
  }

  private void stubForListSummary(long searchTimestamp, List<BareResponse> bareResponses) {
    for (BareResponse bareResponse : bareResponses) {
      wireMockServer.stubFor(get(urlMatching("/loans.*"))
          .withQueryParam("org_code", equalTo(ORGANIZATION_CODE))
          .withQueryParam("search_timestamp", equalTo(String.valueOf(searchTimestamp)))
          .willReturn(
              aResponse()
                  .withStatus(bareResponse.getStatus())
                  .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                  .withBody(readText("classpath:mock/response/" + bareResponse.getMockId() + ".json"))));

    }
  }

  private static void setupMockServer(HttpStatus httpStatus, String searchTimestamp, String fileName) {
    // 6.7.1 계좌목록 조회
    wireMockServer.stubFor(get(urlMatching("/loans.*"))
        .withQueryParam("org_code", equalTo(ORGANIZATION_CODE))
        .withQueryParam("search_timestamp", equalTo(searchTimestamp))
        .willReturn(
            aResponse()
                .withStatus(httpStatus.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
//                .withBody(readText("classpath:mock/response/CP01_001_single_page_01.json"))));
                .withBody(readText("classpath:mock/response/" + fileName))));

  }
}
