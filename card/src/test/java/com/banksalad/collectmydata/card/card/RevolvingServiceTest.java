package com.banksalad.collectmydata.card.card;

import com.banksalad.collectmydata.card.card.dto.ListRevolvingsRequest;
import com.banksalad.collectmydata.card.card.dto.Revolving;
import com.banksalad.collectmydata.card.collect.Executions;
import com.banksalad.collectmydata.card.common.db.entity.RevolvingEntity;
import com.banksalad.collectmydata.card.common.db.entity.RevolvingHistoryEntity;
import com.banksalad.collectmydata.card.common.db.repository.RevolvingHistoryRepository;
import com.banksalad.collectmydata.card.common.db.repository.RevolvingRepository;
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
public class RevolvingServiceTest {

  @Autowired
  private UserBaseService<ListRevolvingsRequest, List<Revolving>> revolvingService;

  @Autowired
  private UserBaseRequestHelper<ListRevolvingsRequest> requestHelper;

  @Autowired
  private UserBaseResponseHelper<List<Revolving>> responseHelper;

  @Autowired
  private RevolvingRepository revolvingRepository;

  @Autowired
  private RevolvingHistoryRepository revolvingHistoryRepository;

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
  @DisplayName("6.3.10 리볼빙 정보 조회")
  public void listRevolvings() throws ResponseNotOkException {

    // FIXME: 정해질 테스트 케이스에 따라 작성
    ExecutionContext context = TestHelper.getExecutionContext(wireMockServer.port());

    revolvingService.getUserBaseInfo(context, Executions.finance_loan_revolvings, requestHelper, responseHelper);

    List<RevolvingEntity> revolvingEntities = revolvingRepository.findAll();
    List<RevolvingHistoryEntity> revolvingHistoryEntities = revolvingHistoryRepository.findAll();
    assertEquals(2, revolvingEntities.size());
    assertEquals(2, revolvingHistoryEntities.size());
  }

  private static void setupMockServer() {

    wireMockServer.stubFor(get(urlMatching("/loans/revolving.*"))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/CD32_001_single_page_00.json"))));
  }
}
