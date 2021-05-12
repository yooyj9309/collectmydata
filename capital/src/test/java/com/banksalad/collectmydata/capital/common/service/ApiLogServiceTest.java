package com.banksalad.collectmydata.capital.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;

import com.banksalad.collectmydata.capital.common.dto.ApiLogTestDto;
import com.banksalad.collectmydata.common.collect.api.Api;
import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;
import com.banksalad.collectmydata.common.collect.executor.CollectExecutor;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.common.util.ExecutionUtil;
import com.banksalad.collectmydata.finance.common.db.entity.ApiLogEntity;
import com.banksalad.collectmydata.finance.common.db.repository.ApiLogRepository;
import com.github.tomakehurst.wiremock.WireMockServer;
import javax.transaction.Transactional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ApiLogServiceTest {

  private static Api apiLogTest =
      Api.builder()
          .id("TEST01")
          .name("API LOG 적재 테스트")
          .endpoint("/test")
          .method("POST")
          .build();

  private static final Execution apiLogTestExecution =
      Execution.create()
          .exchange(apiLogTest)
          .as(ApiLogTestDto.class)
          .build();

  private static WireMockServer wireMockServer;

  @Autowired
  private CollectExecutor collectExecutor;

  @Autowired
  private ApiLogRepository apiLogRepository;

  @BeforeAll
  static void setup() {
    wireMockServer = new WireMockServer(WireMockSpring.options().dynamicPort());
    wireMockServer.start();
  }

  @AfterAll
  static void tearDown() {
    wireMockServer.shutdown();
  }

  @Test
  @Transactional
  @DisplayName("Apilog 테이블 적재 테스트")
  public void ApiLogEntitySaveTest() {
    wireMockServer.stubFor(post(urlMatching("/test"))
        .willReturn(
            aResponse()
                .withBody("{\"rsp_code\":\"00000\",\"rsp_msg\":\"성공\",\"field\":\"test\"}")));

    ExecutionContext context = ExecutionContext.builder()
        .syncRequestId("syncRequestId")
        .banksaladUserId(1L)
        .organizationHost("http://localhost:" + wireMockServer.port())
        .syncStartedAt(LocalDateTime.now())
        .organizationId("organizationId")
        .build();

    ExecutionRequest<Object> executionRequest = ExecutionUtil
        .assembleExecutionRequest(Map.of("token", "token"), new Object());
    collectExecutor.execute(context, apiLogTestExecution, executionRequest);

    List<ApiLogEntity> entities = apiLogRepository.findAll();

    assertEquals(1, entities.size());
    assertThat(entities.get(0)).usingRecursiveComparison()
        .ignoringFields("id", "apiRequestId", "responseHeader", "transformedResponseHeader", "requestDtime",
            "responseDtime", "createdAt", "createdBy", "updatedAt", "updatedBy")
        .isEqualTo(ApiLogEntity.builder()
            .syncRequestId(context.getSyncRequestId())
            .executionRequestId(context.getExecutionRequestId())
            .organizationId("organizationId")
            .banksaladUserId(1l)
            .apiId(apiLogTest.getId())
            .organizationApiId(apiLogTest.getName())
            .requestUrl(apiLogTest.getEndpoint())
            .httpMethod(apiLogTest.getMethod())
            .requestHeader("{\"token\":\"token\"}")
            .requestBody("{}")
            .transformedRequestHeader("{\"token\":\"token\"}")
            .transformedRequestBody("{}")
            .resultCode("00000") // DOTO 기본값을 그대로 사용할지, 또는 다른 값을 사용할지에 따라 변경가능성 있음
            .resultMessage("성공")
            .responseCode("200")
            .responseBody("{\"rsp_code\":\"00000\",\"rsp_msg\":\"성공\",\"field\":\"test\"}")
            .transformedResponseBody("{\"rsp_code\":\"00000\",\"rsp_msg\":\"성공\",\"field\":\"test\"}")
            .elapsedTime(DateUtil.kstLocalDateTimeToEpochMilliSecond(entities.get(0).getResponseDtime()) - DateUtil
                .kstLocalDateTimeToEpochMilliSecond(entities.get(0).getRequestDtime()))
            .build());
  }
}
