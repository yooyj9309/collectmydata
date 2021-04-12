package com.banksalad.collectmydata.insu.summary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.exception.CollectmydataRuntimeException;
import com.banksalad.collectmydata.finance.api.summary.SummaryRequestHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryResponseHelper;
import com.banksalad.collectmydata.finance.api.summary.SummaryService;
import com.banksalad.collectmydata.finance.common.db.entity.UserSyncStatusEntity;
import com.banksalad.collectmydata.finance.common.db.repository.UserSyncStatusRepository;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.banksalad.collectmydata.insu.common.db.entity.InsuranceSummaryEntity;
import com.banksalad.collectmydata.insu.common.db.repository.InsuranceSummaryRepository;
import com.banksalad.collectmydata.insu.common.template.ServiceTest;
import com.banksalad.collectmydata.insu.common.template.dto.BareMain;
import com.banksalad.collectmydata.insu.common.template.dto.BareResponse;
import com.banksalad.collectmydata.insu.common.template.dto.TestCase;
import com.banksalad.collectmydata.insu.summary.context.provider.InsuranceSummaryInvocationContextProvider;
import com.banksalad.collectmydata.insu.summary.dto.InsuranceSummary;
import com.banksalad.collectmydata.insu.summary.dto.ListInsuranceSummariesRequest;
import com.github.tomakehurst.wiremock.WireMockServer;
import javax.transaction.Transactional;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Stream;

import static com.banksalad.collectmydata.insu.common.constant.FinanceConstants.ORGANIZATION_CODE;
import static com.banksalad.collectmydata.insu.common.constant.InsuranceConstants.INSURANCE_SUMMARY_URL_REGEX;
import static com.banksalad.collectmydata.insu.common.util.FileUtil.readText;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@DisplayName("보험-001 보험 목록 조회")
public class InsuranceSummaryServiceTest implements
    ServiceTest<InsuranceSummaryEntity, InsuranceSummaryEntity, InsuranceSummaryInvocationContextProvider> {

  @Autowired
  private SummaryService<ListInsuranceSummariesRequest, InsuranceSummary> service;

  @Autowired
  private SummaryRequestHelper<ListInsuranceSummariesRequest> requestHelper;

  @Autowired
  private SummaryResponseHelper<InsuranceSummary> responseHelper;

  @Autowired
  private UserSyncStatusRepository userSyncStatusRepository;

  @Autowired
  private InsuranceSummaryRepository repository;

  private static final WireMockServer wireMockServer = new WireMockServer(WireMockSpring.options().dynamicPort());

  @BeforeAll
  static void setup() {
    wireMockServer.start();
  }

  @AfterAll
  static void tearDown() {
    wireMockServer.resetAll();
    wireMockServer.stop();
  }

  /*
    컴포지션을 이용한 템플릿 패턴을 적용한다.
    이 파일은 summary 테스트를 위한 템플릿 메써드이므로 수정하지 않는다.
    대신 testcase.SummaryTestCase 만을 수정한다.
     */
  @TestTemplate
  @ExtendWith(InsuranceSummaryInvocationContextProvider.class)
  public void unitTests(TestCase testCase) throws ResponseNotOkException {

    // Given
    prepareRepositories(testCase);

    final Execution execution = testCase.getExecution();
    final String apiId = execution.getApi().getId();
    ExecutionContext executionContext = testCase.getExecutionContext();
    final long bankSaladUserId = executionContext.getBanksaladUserId();
    final String organizationId = executionContext.getOrganizationId();
    final int expectedMainSize = (testCase.getExpectedMains() == null) ? 0 : testCase.getExpectedMains().size();
    final String organizationHost = "http://" + executionContext.getOrganizationHost() + ":" + wireMockServer.port();
    final long searchTimestamp = (testCase.getUserSyncStatusEntities() == null) ? 0
        : testCase.getUserSyncStatusEntities().get(0).getSearchTimestamp();

    executionContext.setOrganizationHost(organizationHost);
    stubMockServer(apiId, testCase.getExpectedResponses(), searchTimestamp);

    final Integer status = testCase.getExpectedResponses().get(0).getStatus();
    if (status != null && status != 200) {
      // When
      ResponseNotOkException responseNotOkException = assertThrows(ResponseNotOkException.class,
          () -> service.listAccountSummaries(executionContext, testCase.getExecution(), requestHelper, responseHelper));

      final BareResponse expectedResponse = testCase.getExpectedResponses().get(0);
      assertAll("오류 코드 확인",
          () -> assertEquals(testCase.getUserSyncStatusEntities().size(), userSyncStatusRepository.count()),
          () -> assertEquals(expectedResponse.getStatus(), responseNotOkException.getStatusCode()),
          () -> assertEquals(expectedResponse.getRspCode(), responseNotOkException.getResponseCode())
      );
    } else {
      /* When */
      service.listAccountSummaries(executionContext, execution, requestHelper, responseHelper);

      final UserSyncStatusEntity actualUserSyncStatus = userSyncStatusRepository
          .findByBanksaladUserIdAndOrganizationIdAndApiId(bankSaladUserId, organizationId, apiId)
          .orElseThrow(() -> new CollectmydataRuntimeException("No record found"));
      assertAll("userSyncStatus 확인",
          () -> verifyEquals(testCase.getExpectedUserSyncStatusSyncedAt(), actualUserSyncStatus.getSyncedAt())
      );

      final List<InsuranceSummaryEntity> actualMains = repository.findAll();
      assertAll("main 확인",
          () -> assertEquals(expectedMainSize, actualMains.size()),
          () -> {
            for (int i = 0; i < expectedMainSize; i++) {
              final BareMain expectedMain = testCase.getExpectedMains().get(i);
              final InsuranceSummaryEntity actualMain = actualMains.get(i);
              verifyEquals(expectedMain.getSyncedAt(), actualMain.getSyncedAt());
            }
          }
      );
    }
  }

  private void prepareRepositories(TestCase testCase) {
    if (testCase.getUserSyncStatusEntities() != null) {
      userSyncStatusRepository.saveAll(testCase.getUserSyncStatusEntities());
    }
    if (testCase.getSummaryEntities() != null) {
      testCase.getSummaryEntities().stream().flatMap(Stream::ofNullable)
          .forEach(o -> repository.save(((InsuranceSummaryEntity) o)));
    }
  }

  private void stubMockServer(String apiId, List<BareResponse> expectedResponses, long searchTimestamp) {
    for (final BareResponse response : expectedResponses) {
      final String fileName = apiId + "_" + response.getMockId() + ".json";
      final int status = (response.getStatus() == null) ? 200 : response.getStatus();
      wireMockServer.stubFor(get(urlMatching(INSURANCE_SUMMARY_URL_REGEX))
          .withQueryParam("org_code", equalTo(ORGANIZATION_CODE))
          .withQueryParam("search_timestamp", equalTo(String.valueOf(searchTimestamp)))
          .willReturn(
              aResponse()
                  .withStatus(status)
                  .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                  .withBody(readText("classpath:mock/response/" + fileName))));
    }
  }

  private void verifyEquals(LocalDateTime expected, LocalDateTime actual) {

    // OS에 따라 DB가 microsecond 단위에서 반올림 발생하여 보정한다.
    assertThat(actual).isCloseTo(expected, within(1, ChronoUnit.MICROS));
  }
}
