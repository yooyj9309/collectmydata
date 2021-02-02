# collectmydata


# API 송수신 개발 가이드
API 전송 레이어를 구현하였습니다 (Pagination 코드는 좀더 간결하게 변경할 예정입니다)
따라서, 리팩토링을 대비하여 API 송수신 코드와 response를 받아 비지니스 로직 처리를 하는 코드를 분리해 주세요.

bank 모듈에 sample code를 작성하였으며 가급적 빨리 가이드를 작성하겠습니다.

# module에서 구현해야 하는 서비스
- BankSyncService.sync()
- BankPublishService.publish()

# API 송수신 : BankSyncService.sync()

**0. API 및 Execution 정의**

**0.1 request DTO**
GET api의 url은 요청시 request 객체의 필드로 build 됩니다. request객체의 JsonNaming 정책을 SnakeCaseStragegy로 지정해 주세요>
```java
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AccountsRequest {

  private String orgCode;
  private long searchTimestamp;
  private String nextPage;
  private int limit;
}
```

**0.2 API**
```java
  public static Api finance_bank_accounts =
      Api.builder()
          .id("BA01")
          .name("계좌목록조회")
          .endpoint(
              "/accounts?org_code={org_code}&search_timestamp={search_timestamp}&next_page={next_page}&limit={limit}")
          .method(HttpMethod.GET.name())
          .pagination(Pagination.builder()
              .nextPage("next_page")
              .build())
          .build();

  public static Api finance_bank_accounts_deposit_basic =
      Api.builder()
          .id("BA02")
          .name("수신계좌기본정조회")
          .endpoint("/accounts/deposit/basic")
          .method(HttpMethod.POST.name())
          .build();
```

**0.3 Execution**
```java
  public static final Execution finance_bank_accounts =
      Execution.create()
          .exchange(Apis.finance_bank_accounts)
          .as(AccountsResponse.class)
          .build();
```

**1. ExecutionContext 생성**
`BankSyncServiceImpl`
```java
    ExecutionContext executionContext = ExecutionContext.builder()
        .banksaladUserId(banksaladUserId)
        .organizationId(organizationId)
        .accessToken("fixme")
        .organizationHost("http://whatever")
        .executionRequestId(UUID.randomUUID().toString())
        .syncStartedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
        .build();
```
**2. sync를 위한 개별 API 호출 (AccountServiceImpl)**

2.1. non-pagination
```java
  public List<Account> getAccounts(ExecutionContext executionContext) throws CollectException {
    /* request header */
    //Map<String, String> header = headerService.makeHeader(banksaladUserId, organizationId);
    Map<String, String> header = Map.of("Authorization", executionContext.getAccessToken());

    /* request body & execution */
    ExecutionRequest<AccountsRequest> executionRequest =
      ExecutionRequest.<AccountsRequest>builder()
          .headers(header)
          .request(
             AccountsRequest.builder()
                  .orgCode("020")
                  .searchTimestamp(0L)
                  .limit(500)
                  .nextPage(executionResponse != null ? executionResponse.getNextPage() : null)
                  .build())
          .build();

    executionResponse = collectExecutor.execute(executionContext, Executions.finance_bank_accounts, executionRequest);

    if (executionResponse.getHttpStatusCode() != HttpStatus.OK.value()) {
      throw new CollectException("getAccounts Statue is not OK");
    }

    return executionResponse.getResponse().getAccountList();
  }
```

2.1.2 pagination **(pagination code는 리펙토링 예정입니다)**
```java
  @Override
  public List<Account> getAccounts(ExecutionContext executionContext) throws CollectException {

    /* request header */
    //Map<String, String> header = headerService.makeHeader(banksaladUserId, organizationId);
    Map<String, String> header = Map.of("Authorization", executionContext.getAccessToken());

    List<Account> accounts = new ArrayList<>();
    ExecutionResponse<AccountsResponse> executionResponse = null;

    do {
      /* request body & execution */
      ExecutionRequest<AccountsRequest> executionRequest =
          ExecutionRequest.<AccountsRequest>builder()
              .headers(header)
              .request(
                  AccountsRequest.builder()
                      .orgCode("020")
                      .searchTimestamp(0L)
                      .limit(500)
                      .nextPage(executionResponse != null ? executionResponse.getNextPage() : null)
                      .build())
              .build();

      executionResponse = collectExecutor.execute(executionContext, Executions.finance_bank_accounts, executionRequest);

      if (executionResponse.getHttpStatusCode() != HttpStatus.OK.value()) {
        throw new CollectException("getAccounts Statue is not OK");
      }

      accounts.addAll(saveAccounts(executionResponse.getResponse()));

    } while (executionResponse.getNextPage() != null);

    return accounts;
  }
```


**3. Testcase**
아래 dependency 를 작성해 주세요.

- mavenBom spring cloug 추가
- spring-cloud-starter-contract-stub-runner 추가

```
dependencyManagement {
    imports {
        mavenBom "software.amazon.awssdk:bom:${awsSdkVersion}"
        mavenBom "org.springframework.cloud:spring-cloud-dependencies:${springCloudVersion}"
    }
}

testImplementation("org.springframework.cloud:spring-cloud-starter-contract-stub-runner")

```

MockRestServiceServer를 더이상 사용할 수 없고 WireMock를 사용하여야 합니다. 형식은 기존과 최대한 유사하게 구성 하였습니다. 3.1. mock reponse 생성
test/resources/mock/bank 폴더 하위세 API ID를 prefix로 생성하여 주세요.

```
mock/bank/BA01_001_single_page_01.json
mock/bank/BA01_002_multi_page_01.json
mock/bank/BA01_002_multi_page_02.json
mock/bank/BA01_002_multi_page_03.json
```

```json
{
  "rsp_code": "000",
  "rsp_msg": "rsp_msg",
  "search_timestamp": 1000,
  "reg_date": "20200101",
  "account_cnt": 1,
  "account_list": [
    {
      "account_num": "1234567890",
      "is_consent": true,
      "seqno": 1,
      "currency_code": "KRW",
      "prod_name": "뱅크샐러드 대박 적금",
      "account_type": "1003",
      "account_status": "01"
    }
  ]
}
```

3.2 test code 작성
아래의 순서로 동작합니다.
mock server 설정 -> API request 실행 -> service response 및 DB 입력 검증

3.2.1 mock server 설정 (paging response의 예)
- Query 파라미터 설정에 따라 응답을 분기할 수 있습니다.
- BodyRequest에 따라서도 분기할 수 있습니다.
- Query 파라미터의 경우 Url이 달라지므로 UrlMatching("/accounts.*")와 같이 regex를 활용해야 합니다. asterisk앞에 dot을 잊지 마세요.

```java
  private void setupServerAccountsMultiPage() throws Exception {
    // 계좌목록조회 page 01
    wiremock.stubFor(get(urlMatching("/accounts.*"))
        .withQueryParam("org_code", equalTo("020"))
        .withQueryParam("search_timestamp", equalTo("0"))
        .withQueryParam("limit", equalTo("500"))
        .willReturn(
            aResponse()
                .withFixedDelay(1000)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/bank/BA01_001_single_page_01.json"))));

    // 계좌목록조회 page 02
    wiremock.stubFor(get(urlMatching("/accounts.*"))
        .withQueryParam("org_code", equalTo("020"))
        .withQueryParam("search_timestamp", equalTo("0"))
        .withQueryParam("limit", equalTo("500"))
        .withQueryParam("next_page", equalTo("02"))
        .willReturn(
            aResponse()
                .withFixedDelay(1000)
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", ContentType.APPLICATION_JSON.toString())
                .withBody(readText("classpath:mock/bank/BA01_001_single_page_02.json"))));

...
```

3.2.2 테스트코드 작성
```java
  @Test
  @DisplayName("계좌목록조회: 복수페이지")
  public void step_02_getAccounts_multi_page_success() throws Exception {

    /* transaction mock server */
    setupServerAccountsMultiPage();

    /* execution context */
    ExecutionContext executionContext = ExecutionContext.builder()
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accessToken("test")
        .organizationHost(ORGANIZATION_HOST)
        .executionRequestId(UUID.randomUUID().toString())
        .syncStartedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
        .build();

    List<Account> accounts = accountService.getAccounts(executionContext);
    Assertions.assertThat(accounts.size()).isEqualTo(3);
  }
```


