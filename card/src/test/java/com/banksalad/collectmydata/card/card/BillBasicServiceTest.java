package com.banksalad.collectmydata.card.card;

import com.banksalad.collectmydata.card.card.context.provider.BillBasicInvocationContextProvider;
import com.banksalad.collectmydata.card.card.dto.BillBasic;
import com.banksalad.collectmydata.card.card.dto.BillDetail;
import com.banksalad.collectmydata.card.card.dto.ListBillBasicRequest;
import com.banksalad.collectmydata.card.card.dto.ListBillDetailRequest;
import com.banksalad.collectmydata.card.common.db.entity.BillEntity;
import com.banksalad.collectmydata.card.common.db.repository.BillRepository;
import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.exception.CollectmydataRuntimeException;
import com.banksalad.collectmydata.finance.api.bill.BillRequestHelper;
import com.banksalad.collectmydata.finance.api.bill.BillResponseHelper;
import com.banksalad.collectmydata.finance.api.bill.BillService;
import com.banksalad.collectmydata.finance.common.db.entity.UserSyncStatusEntity;
import com.banksalad.collectmydata.finance.common.db.repository.UserSyncStatusRepository;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.banksalad.collectmydata.finance.test.template.dto.BareMain;
import com.banksalad.collectmydata.finance.test.template.dto.BareRequest;
import com.banksalad.collectmydata.finance.test.template.dto.BareResponse;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;

import com.github.tomakehurst.wiremock.WireMockServer;
import javax.transaction.Transactional;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Stream;

import static com.banksalad.collectmydata.card.common.constant.CardConstants.CARD_BILL_URL_REGEX;
import static com.banksalad.collectmydata.card.util.FileUtil.readText;
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
@DisplayName("카드-004 청구 기본정보 조회")
public class BillBasicServiceTest {

  @Autowired
  private BillService<ListBillBasicRequest, BillBasic, ListBillDetailRequest, BillDetail> service;

  @Autowired
  private BillRequestHelper<ListBillBasicRequest> requestHelper;

  @Autowired
  private BillResponseHelper<BillBasic> responseHelper;

  @Autowired
  private UserSyncStatusRepository userSyncStatusRepository;

  @Autowired
  private BillRepository repository;

  private static WireMockServer wireMockServer = new WireMockServer(WireMockSpring.options().dynamicPort());

  @BeforeAll
  static void setUp() {

    wireMockServer.start();
  }

  @AfterEach
  void tearDown() {

    wireMockServer.resetAll();
  }

  @AfterAll
  static void shutDown() {

    wireMockServer.stop();
  }

  /*
  컴포지션을 이용한 템플릿 패턴을 적용한다.
  이 파일은 summary 테스트를 위한 템플릿 메써드이므로 수정하지 않는다.
   */
  @TestTemplate
  @ExtendWith(BillBasicInvocationContextProvider.class)
  public void listSummariesTest(TestCase testCase) {

    /* Given */
    prepareRepositories(testCase);

    final Execution execution = testCase.getExecution();
    final String apiId = execution.getApi().getId();
    ExecutionContext executionContext = testCase.getExecutionContext();
    final long bankSaladUserId = executionContext.getBanksaladUserId();
    final String organizationId = executionContext.getOrganizationId();
    final int expectedMainSize = (testCase.getExpectedMains() == null) ? 0 : testCase.getExpectedMains().size();
    final String organizationHost = "http://" + executionContext.getOrganizationHost() + ":" + wireMockServer.port();

    executionContext.setOrganizationHost(organizationHost);
    stubMockServer(apiId, testCase.getRequestParams(), testCase.getExpectedResponses());

    final Integer status = testCase.getExpectedResponses().get(0).getStatus();
    if (status != null && status != 200) {
      // When
      ResponseNotOkException responseNotOkException = assertThrows(ResponseNotOkException.class,
          () -> service.listBills(executionContext, testCase.getExecution(), requestHelper, responseHelper));

      // Then
      final UserSyncStatusEntity actualUserSyncStatus = userSyncStatusRepository
          .findByBanksaladUserIdAndOrganizationIdAndApiId(bankSaladUserId, organizationId, apiId)
          .orElseThrow(() -> new CollectmydataRuntimeException("No record found"));
      final BareResponse expectedResponse = testCase.getExpectedResponses().get(0);
      assertAll("오류 코드 확인",
          () -> assertEquals(testCase.getUserSyncStatusEntities().size(), userSyncStatusRepository.count()),
          () -> assertEquals(testCase.getExpectedUserSyncStatusSyncedAt(), actualUserSyncStatus.getSyncedAt()),
          () -> assertEquals(expectedResponse.getStatus(), responseNotOkException.getStatusCode()),
          () -> assertEquals(expectedResponse.getRspCode(), responseNotOkException.getResponseCode())
      );
    } else {
      /* When */
      try {
        service.listBills(executionContext, execution, requestHelper, responseHelper);
      } catch (ResponseNotOkException e) {
        e.printStackTrace();
      }

      /* Then */
      final UserSyncStatusEntity actualUserSyncStatus = userSyncStatusRepository
          .findByBanksaladUserIdAndOrganizationIdAndApiId(bankSaladUserId, organizationId, apiId)
          .orElseThrow(() -> new CollectmydataRuntimeException("No record found"));
      assertAll("userSyncStatus 확인",
          () -> verifyEquals(testCase.getExpectedUserSyncStatusSyncedAt(), actualUserSyncStatus.getSyncedAt())
      );

      final List<BillEntity> actualMains = repository.findAll();
      assertAll("main 확인",
          () -> assertEquals(expectedMainSize, actualMains.size()),
          () -> {
            for (int i = 0; i < expectedMainSize; i++) {
              final BareMain expectedMain = testCase.getExpectedMains().get(i);
              final BillEntity actualMain = actualMains.get(i);
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
    if (testCase.getMainEntities() != null) {
      testCase.getMainEntities().stream().flatMap(Stream::ofNullable)
          .forEach(o -> repository.save((BillEntity) o));
    }
  }

  private void stubMockServer(String apiId, List<BareRequest> requests, List<BareResponse> expectedResponses) {

    for (int i = 0; i < expectedResponses.size(); i++) {
      final String next_page = requests.get(i).getNextPage();
      final BareResponse response = expectedResponses.get(i);
      final String fileName = apiId + "_" + response.getMockId() + ".json";
      final int status = (response.getStatus() == null) ? 200 : response.getStatus();

      if (next_page == null) {
        wireMockServer.stubFor(get(urlMatching(CARD_BILL_URL_REGEX))
            .willReturn(
                aResponse()
                    .withStatus(status)
                    .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                    .withBody(readText("classpath:mock/response/" + fileName))));
      } else {
        wireMockServer.stubFor(get(urlMatching(CARD_BILL_URL_REGEX))
            .withQueryParam("next_page", equalTo(next_page))
            .willReturn(
                aResponse()
                    .withStatus(status)
                    .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                    .withBody(readText("classpath:mock/response/" + fileName))));
      }
    }
  }

  private void verifyEquals(LocalDateTime expected, LocalDateTime actual) {

    // OS에 따라 DB가 microsecond 단위에서 반올림 발생하여 보정한다.
    assertThat(actual).isCloseTo(expected, within(1, ChronoUnit.MICROS));
  }
}
