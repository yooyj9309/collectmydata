package com.banksalad.collectmydata.bank.template;

import com.banksalad.collectmydata.common.collect.api.Api;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.common.exception.ResponseNotOkException;
import com.banksalad.collectmydata.finance.test.template.dto.BareRequest;
import com.banksalad.collectmydata.finance.test.template.dto.BareResponse;
import com.banksalad.collectmydata.finance.test.template.dto.TestCase;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.apache.http.entity.ContentType;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static com.banksalad.collectmydata.bank.testutil.FileUtil.readText;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.ACCESS_TOKEN;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.CONSENT_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.NEW_SYNCED_AT;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.ORGANIZATION_CODE;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.ORGANIZATION_HOST;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.ORGANIZATION_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.SYNC_REQUEST_ID;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class ServiceTest<GParent, Parent, Main, Child> {

  protected void prepare(TestCase<GParent, Parent, Main, Child> testCase, WireMockServer wireMockServer) {
    final String httpMethod = testCase.getExecution().getApi().getMethod();

    testCase.setExecutionContext(generateExecutionContext(wireMockServer.port()));

    prepareRepositories(testCase);

    if (httpMethod.equals("GET")) {
      stubMockServerForGet(testCase, testCase.getExecutionContext().getOrganizationCode(), wireMockServer);
    } else if (httpMethod.equals("POST")) {
      stubMockServerForPost(testCase, wireMockServer);
    }
  }

  protected void prepareRepositories(TestCase<GParent, Parent, Main, Child> testCase) {

    final List<GParent> gParents = testCase.getGParentEntities();
    final List<Parent> parents = testCase.getParentEntities();
    final List<Main> mains = testCase.getMainEntities();
    final List<Child> children = testCase.getChildEntities();

    if (gParents != null && gParents.size() > 0) {
      saveGParents(gParents);
    }
    if (parents != null && parents.size() > 0) {
      saveParents(parents);
    }
    if (mains != null && mains.size() > 0) {
      saveMains(mains);
    }
    if (children != null && children.size() > 0) {
      saveChildren(children);
    }
  }

  protected abstract void saveGParents(List<GParent> gParents);

  protected abstract void saveParents(List<Parent> parents);

  protected abstract void saveMains(List<Main> mains);

  protected abstract void saveChildren(List<Child> children);

  protected void stubMockServerForGet(TestCase<GParent, Parent, Main, Child> testCase, String organizationCode,
      WireMockServer wireMockServer) {

    final Api api = testCase.getExecution().getApi();
    final String urlRegex = api.getEndpoint().substring(0, api.getEndpoint().indexOf("?")) + ".*";

    for (int i = 0; i < testCase.getExpectedResponses().size(); i++) {
      final BareRequest request = testCase.getRequestParams().get(i);
      final BareResponse response = testCase.getExpectedResponses().get(i);
      final Long searchTimestamp = request.getSearchTimestamp();
      final String next_page = request.getNextPage();
      final int status = (response.getStatus() == null) ? 200 : response.getStatus();
      final String fileName = api.getId() + "_" + response.getMockId() + ".json";

      // TODO : json file 위치 재조정 필요. 다른 industry 와 동일하게 classpath:mock/request,response 로.
      if (searchTimestamp == null) {
        if (next_page == null) {
          wireMockServer.stubFor(get(urlMatching(urlRegex))
              .withQueryParam("org_code", equalTo(organizationCode))
              .willReturn(
                  aResponse()
                      .withStatus(status)
                      .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                      .withBody(readText("classpath:mock/bank/response/" + fileName))));
        } else {
          wireMockServer.stubFor(get(urlMatching(urlRegex))
              .withQueryParam("org_code", equalTo(organizationCode))
              .withQueryParam("next_page", equalTo(next_page))
              .willReturn(
                  aResponse()
                      .withStatus(status)
                      .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                      .withBody(readText("classpath:mock/bank/response/" + fileName))));
        }
      } else {
        if (next_page == null) {
          wireMockServer.stubFor(get(urlMatching(urlRegex))
              .withQueryParam("org_code", equalTo(organizationCode))
              .withQueryParam("search_timestamp", equalTo(String.valueOf(searchTimestamp)))
              .willReturn(
                  aResponse()
                      .withStatus(status)
                      .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                      .withBody(readText("classpath:mock/bank/response/" + fileName))));
        } else {
          wireMockServer.stubFor(get(urlMatching(urlRegex))
              .withQueryParam("org_code", equalTo(organizationCode))
              .withQueryParam("search_timestamp", equalTo(String.valueOf(searchTimestamp)))
              .withQueryParam("next_page", equalTo(next_page))
              .willReturn(
                  aResponse()
                      .withStatus(status)
                      .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                      .withBody(readText("classpath:mock/bank/response/" + fileName))));
        }
      }
    }
  }

  protected void stubMockServerForPost(TestCase<GParent, Parent, Main, Child> testCase, WireMockServer wireMockServer) {

    wireMockServer.resetAll();

    final Api api = testCase.getExecution().getApi();
    final String urlRegex = api.getEndpoint();

    for (int i = 0; i < testCase.getExpectedResponses().size(); i++) {
      final BareResponse response = testCase.getExpectedResponses().get(i);
      final int status = (response.getStatus() == null) ? 200 : response.getStatus();
      final String fileName = api.getId() + "_" + response.getMockId() + ".json";

      wireMockServer.stubFor(post(urlMatching(urlRegex))
          .withRequestBody(equalToJson(readText("classpath:mock/bank/request/" + fileName)))
          .willReturn(
              aResponse()
                  .withStatus(status)
                  .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                  .withBody(readText("classpath:mock/bank/response/" + fileName))));
    }
  }

  protected abstract void runMainService(TestCase<GParent, Parent, Main, Child> testCase) throws ResponseNotOkException;

  protected void runAndTestException(TestCase<GParent, Parent, Main, Child> testCase) {
    /* When */
    ResponseNotOkException responseNotOkException = assertThrows(ResponseNotOkException.class,
        () -> runMainService(testCase));

    final BareResponse lastResponse = testCase.getExpectedResponses().get(testCase.getExpectedResponses().size() - 1);

    assertAll("오류 코드 확인",
        () -> assertEquals(lastResponse.getStatus(), responseNotOkException.getStatusCode()),
        () -> assertEquals(lastResponse.getRspCode(), responseNotOkException.getResponseCode())
    );
  }

  protected void validate(TestCase<GParent, Parent, Main, Child> testCase) {
    final List<Parent> expectedParents = testCase.getExpectedParentEntities();
    final List<Main> expectedMains = testCase.getExpectedMainEntities();
    final List<Child> expectedChildren = testCase.getExpectedChildEntities();

    if (expectedParents != null && expectedParents.size() > 0) {
      validateParents(expectedParents);
    }
    if (expectedMains != null && expectedMains.size() > 0) {
      validateMains(expectedMains);
    }
    if (expectedChildren != null && expectedChildren.size() > 0) {
      validateChildren(expectedChildren);
    }
  }

  protected abstract void validateParents(List<Parent> expectedParents);

  protected abstract void validateMains(List<Main> expectedMains);

  protected abstract void validateChildren(List<Child> expectedChildren);

  protected ExecutionContext generateExecutionContext(int port) {

    return ExecutionContext.builder()
        .consentId(CONSENT_ID)
        .syncRequestId(SYNC_REQUEST_ID)
        .executionRequestId(UUID.randomUUID().toString())
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .organizationCode(ORGANIZATION_CODE)
        .organizationHost("http://" + ORGANIZATION_HOST + ":" + port)
        .accessToken(ACCESS_TOKEN)
        .syncStartedAt(NEW_SYNCED_AT)
        .build();
  }

  protected void verifyEquals(LocalDateTime expected, LocalDateTime actual) {

    // OS에 따라 DB가 microsecond 단위에서 반올림 발생하여 보정한다.
    assertThat(actual).isCloseTo(expected, within(1, ChronoUnit.MICROS));
  }
}
