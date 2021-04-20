package com.banksalad.collectmydata.card.card;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;

import com.banksalad.collectmydata.card.card.dto.ListPointsRequest;
import com.banksalad.collectmydata.card.card.dto.Point;
import com.banksalad.collectmydata.card.collect.Executions;
import com.banksalad.collectmydata.card.common.db.entity.PointEntity;
import com.banksalad.collectmydata.card.common.db.entity.PointHistoryEntity;
import com.banksalad.collectmydata.card.common.db.repository.PointHistoryRepository;
import com.banksalad.collectmydata.card.common.db.repository.PointRepository;
import com.banksalad.collectmydata.card.util.TestHelper;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.userbase.UserBaseRequestHelper;
import com.banksalad.collectmydata.finance.api.userbase.UserBaseResponseHelper;
import com.banksalad.collectmydata.finance.api.userbase.UserBaseService;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
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

@SpringBootTest
public class PointServiceTest {

  @Autowired
  private UserBaseService<ListPointsRequest, List<Point>> pointService;

  @Autowired
  private UserBaseRequestHelper<ListPointsRequest> requestHelper;

  @Autowired
  private UserBaseResponseHelper<List<Point>> responseHelper;

  @Autowired
  private PointRepository pointRepository;

  @Autowired
  private PointHistoryRepository pointHistoryRepository;

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
  @Transactional
  @DisplayName("6.3.3 포인트 정보 조회")
  public void listPoints() throws ResponseNotOkException {
    // FIXME: 정해질 테스트 케이스에 따라 작성
    ExecutionContext context = TestHelper.getExecutionContext(wireMockServer.port());

    pointService.getUserBaseInfo(context, Executions.finance_card_point, requestHelper, responseHelper);

    List<PointEntity> pointEntities = pointRepository.findAll();
    List<PointHistoryEntity> pointHistoryEntities = pointHistoryRepository.findAll();
    assertEquals(2, pointEntities.size());
    assertEquals(2, pointHistoryEntities.size());
  }

  private static void setupMockServer() {
    wireMockServer.stubFor(get(urlMatching("/cards/point.*"))
        .willReturn(
            aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/response/CD11_001_single_page_00.json"))));
  }
}
