package com.banksalad.collectmydata.connect.common.service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;
import com.banksalad.collectmydata.connect.common.collect.Executions;
import com.banksalad.collectmydata.connect.support.dto.FinanceOrganizationInfo;
import com.banksalad.collectmydata.connect.support.dto.FinanceOrganizationRequest;
import com.banksalad.collectmydata.connect.support.dto.FinanceOrganizationResponse;
import com.banksalad.collectmydata.connect.support.service.SupportService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.apache.http.entity.ContentType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.banksalad.collectmydata.connect.util.FileUtil.readText;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DisplayName("SupportExecutionTest Test")
public class ExecutionServiceTest {
  public static WireMockServer wiremock = new WireMockServer(WireMockSpring.options().port(9090));
  private static final String ORGANIZATION_HOST = "http://localhost:9090";

  @BeforeEach
  public void setupClass() {
    wiremock.start();
  }

  @AfterEach
  public void after() {
    wiremock.resetAll();
  }

  @AfterAll
  public static void clean() {
    wiremock.shutdown();
  }

  @Autowired
  private ExecutionService executionService;

  @Test
  @DisplayName("기관정보 조회 execution test")
  public void getOrganizationInfoTest(){
    setupServer();

    /* execution context */
    ExecutionContext executionContext = ExecutionContext.builder()
        .accessToken("test")
        .organizationHost(ORGANIZATION_HOST)
        .build();


    ExecutionRequest<FinanceOrganizationRequest> executionRequest =
        ExecutionRequest.<FinanceOrganizationRequest>builder()
            .headers(new HashMap<>())
            .request(FinanceOrganizationRequest.builder().searchTimestamp(0L).build())
            .build();


    // 7.1.2 기관 정보 조회 및 적재
    FinanceOrganizationResponse financeOrganizationResponse = (FinanceOrganizationResponse) executionService.execute(
        executionContext,
        Executions.support_get_organization_info,
        executionRequest
    );

    assertEquals(2, financeOrganizationResponse.getOrgList().size());
    assertThat(financeOrganizationResponse).usingRecursiveComparison().isEqualTo(
        financeOrganizationResponse.builder()
            .rspCode("000")
            .rspMsg("rsp_msg")
            .searchTimestamp(1000L)
            .orgCnt(2)
            .orgList(List.of(
                FinanceOrganizationInfo.builder()
                    .opType("I")
                    .orgCode("020")
                    .orgType("01")
                    .orgName("기관1")
                    .orgRegno("1234567890")
                    .corpRegno("1234567890")
                    .address("address1")
                    .domain("domain1")
                    .relayOrgCode("relay_org_code1")
                    .build(),
                FinanceOrganizationInfo.builder()
                    .opType("I")
                    .orgCode("030")
                    .orgType("02")
                    .orgName("기관2")
                    .orgRegno("1234567890")
                    .corpRegno("1234567890")
                    .address("address2")
                    .domain("domain2")
                    .relayOrgCode("relay_org_code2")
                    .build()
            ))
            .build()
    );

  }

  private void setupServer() {
    // 계좌목록조회 page 01
    wiremock.stubFor(get(urlMatching("/mgmts/orgs.*"))
        .withQueryParam("search_timestamp", equalTo("0"))
        .willReturn(
            aResponse()
                .withFixedDelay(1000)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/api7/SU02_001.json"))));
  }
}
