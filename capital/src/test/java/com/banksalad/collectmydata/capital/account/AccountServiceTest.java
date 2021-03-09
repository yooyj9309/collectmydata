package com.banksalad.collectmydata.capital.account;

import com.banksalad.collectmydata.capital.account.dto.AccountBasicResponse;
import com.banksalad.collectmydata.capital.account.dto.AccountTransaction;
import com.banksalad.collectmydata.capital.account.dto.AccountTransactionResponse;
import com.banksalad.collectmydata.capital.common.db.entity.AccountBasicEntity;
import com.banksalad.collectmydata.capital.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.capital.common.db.repository.AccountBasicRepository;
import com.banksalad.collectmydata.capital.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.capital.common.db.repository.AccountTransactionInterestRepository;
import com.banksalad.collectmydata.capital.common.db.repository.AccountTransactionRepository;
import com.banksalad.collectmydata.capital.common.db.repository.UserSyncStatusRepository;
import com.banksalad.collectmydata.capital.common.dto.AccountSummary;
import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.capital.common.service.AccountSummaryService;
import com.banksalad.collectmydata.capital.common.service.ExternalApiService;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.crypto.HashUtil;
import com.banksalad.collectmydata.common.enums.Industry;
import com.banksalad.collectmydata.common.enums.MydataSector;
import com.banksalad.collectmydata.common.exception.CollectRuntimeException;
import com.banksalad.collectmydata.common.util.DateUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.persistence.EntityExistsException;
import javax.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.banksalad.collectmydata.capital.common.TestHelper.respondAccountTransactionResponseWithEmptyPages;
import static com.banksalad.collectmydata.capital.common.TestHelper.respondAccountTransactionResponseWithOnePage;
import static com.banksalad.collectmydata.capital.common.TestHelper.respondAccountTransactionResponseWithTwoPages;
import static java.lang.Boolean.TRUE;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@SpringBootTest
public class AccountServiceTest {

  @MockBean
  private ExternalApiService externalApiService;

  @Autowired
  private AccountService accountService;

  @Autowired
  private AccountSummaryService accountSummaryService;

  @Autowired
  private AccountSummaryRepository accountSummaryRepository;

  @Autowired
  private AccountBasicRepository accountBasicRepository;

  @Autowired
  private AccountTransactionRepository accountTransactionRepository;

  @Autowired
  private AccountTransactionInterestRepository accountTransactionInterestRepository;

  @Autowired
  private UserSyncStatusRepository userSyncStatusRepository;

  private long banksaladUserId = 1L;
  private String organizationId = "shinhancard";
  private String accountNum = "1234567812345678";
  private Integer seqno = 1;

  private static final MydataSector SECTOR = MydataSector.FINANCE;
  private static final Industry INDUSTRY = Industry.CAPITAL;
  private static final String ORGANIZATION_ID = "X-loan";
  private static final String ORGANIZATION_CODE = "10041004";
  private static final String ORGANIZATION_HOST = "localhost";
  private static final Long BANKSALAD_USER_ID = 1L;
  private static final String ACCESS_TOKEN = "abc.def.ghi";
  private static final String ACCOUNT_NUMBER = "1234567890";
  private static final Integer SEQNO = 1;
  private static final String SEQNO1 = "1";
  private static final String ACCOUNT_TYPE = "3100";
  private static final String ACCOUNT_STATUS = "01";
  private static final String PRODUCT_NAME = "X-론 직장인 신용대출";
  private static final int MAX_LIMIT = 2;

  @AfterEach
  public void cleanRepositories() {
    accountSummaryRepository.deleteAll();
    accountBasicRepository.deleteAll();
    accountTransactionRepository.deleteAll();
    accountTransactionInterestRepository.deleteAll();
    userSyncStatusRepository.deleteAll();
  }

  @Test
  @DisplayName("6.7.2 account_basic table 에 row 가 있음 && Data Provider API Response 와 다름")
  public void givenExistingAccountBasicDifferedWithApiResponse_whenListAccountBasics_UpdateAccountBasic() {
    // Given
    ExecutionContext executionContext = getExecutionContext();
    Organization organization = getOrganization();
    AccountSummary accountSummary = getAccount();
    AccountBasicResponse accountBasicResponse = getAccountBasicResponse();

    AccountSummaryEntity accountSummaryEntity = AccountSummaryEntity.builder()
        .syncedAt(LocalDateTime.now())
        .banksaladUserId(executionContext.getBanksaladUserId())
        .organizationId(organization.getOrganizationId())
        .accountNum(accountSummary.getAccountNum())
        .seqno(accountSummary.getSeqno())
        .isConsent(TRUE)
        .prodName("prodName")
        .accountType("")
        .accountStatus("")
        .build();
    accountSummaryRepository.save(accountSummaryEntity);

    AccountBasicEntity accountBasicEntity = AccountBasicEntity.builder()
        .syncedAt(LocalDateTime.now())
        .banksaladUserId(executionContext.getBanksaladUserId())
        .organizationId(organization.getOrganizationId())
        .accountNum(accountSummary.getAccountNum())
        .seqno(accountSummary.getSeqno())
        .holderName(accountBasicResponse.getHolderName())
        .issueDate(LocalDate.parse(accountBasicResponse.getIssueDate(), DateTimeFormatter.ofPattern("yyyyMMdd")))
        .expDate(LocalDate.parse(accountBasicResponse.getExpDate(), DateTimeFormatter.ofPattern("yyyyMMdd")))
        .lastOfferedRate(accountBasicResponse.getLastOfferedRate())
        .repayDate(accountBasicResponse.getRepayDate())
        .repayMethod(accountBasicResponse.getRepayMethod())
        .repayOrgCode(accountBasicResponse.getRepayOrgCode())
        .repayAccountNum("will-be-updated")
        .build();
    accountBasicRepository.save(accountBasicEntity);

    given(externalApiService.getAccountBasic(executionContext, organization, accountSummary))
        .willReturn(accountBasicResponse);

    // When
    accountService.listAccountBasics(executionContext, organization, singletonList(accountSummary));

    // Then
    AccountBasicEntity actualAccountBasicEntity = accountBasicRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
            executionContext.getBanksaladUserId(), organization.getOrganizationId(),
            accountSummary.getAccountNum(), accountSummary.getSeqno());
    assertEquals(accountBasicResponse.getRepayAccountNum(), actualAccountBasicEntity.getRepayAccountNum());
  }

  @Test
  @DisplayName("6.7.2 account_basic table 에 row 가 없음")
  public void givenNotExistAccountBasic_whenListAccountBasics_SavedAccountBasicAndUpdateSearchTimestamp() {
    // Given
    ExecutionContext executionContext = getExecutionContext();
    Organization organization = getOrganization();
    AccountSummary accountSummary = getAccount();
    AccountBasicResponse accountBasicResponse = getAccountBasicResponse();
    AccountSummaryEntity accountSummaryEntity = AccountSummaryEntity.builder()
        .syncedAt(LocalDateTime.now())
        .banksaladUserId(executionContext.getBanksaladUserId())
        .organizationId(organization.getOrganizationId())
        .accountNum(accountSummary.getAccountNum())
        .seqno(accountSummary.getSeqno())
        .isConsent(TRUE)
        .prodName("prodName")
        .accountType("")
        .accountStatus("")
        .build();
    accountSummaryRepository.save(accountSummaryEntity);

    given(externalApiService.getAccountBasic(executionContext, organization, accountSummary))
        .willReturn(accountBasicResponse);

    // When
    accountService.listAccountBasics(executionContext, organization, singletonList(accountSummary));

    // Then
    AccountBasicEntity actualAccountBasicEntity = accountBasicRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
            executionContext.getBanksaladUserId(), organization.getOrganizationId(),
            accountSummary.getAccountNum(), accountSummary.getSeqno());
    assertNotNull(actualAccountBasicEntity);

    AccountSummaryEntity actualAccountSummaryEntity = accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
            executionContext.getBanksaladUserId(), organization.getOrganizationId(),
            accountSummary.getAccountNum(), accountSummary.getSeqno())
        .orElseThrow(EntityExistsException::new);
    assertEquals(1000, actualAccountSummaryEntity.getBasicSearchTimestamp());
  }

  @Test
  @DisplayName("6.7.4 빈 트랜잭션 응답")
  public void givenRequest_whenListAccountTransactions_thenEmptyPageResponse() {
    // Given
    final ExecutionContext executionContext = getExecutionContext();
    final Organization organization = getOrganization();
    final AccountSummary accountSummary = getAccount();
    when(externalApiService.getAccountTransactions(executionContext, organization, accountSummary))
        .thenReturn(respondAccountTransactionResponseWithEmptyPages());

    // When
    List<AccountTransaction> response = accountService
        .listAccountTransactions(executionContext, organization, List.of(accountSummary));

    // Then
    assertEquals(0, accountTransactionRepository.count());
    assertEquals(0, accountTransactionInterestRepository.count());
    assertThat(response).isEmpty();
    assertThat(accountTransactionRepository.findAll()).isEmpty();
    assertThat(accountTransactionInterestRepository.findAll()).isEmpty();
  }

  @Test
  @DisplayName("6.7.4 새로운 2페이지 짜리 트랜잭션 응답")
  public void givenRequest_whenListAccountTransactions_thenNewTwoPageResponse() {
    // Given
    final ExecutionContext executionContext = getExecutionContext();
    final Organization organization = getOrganization();
    final AccountSummary accountSummary = getAccount();
    final Long bankSaladUserId = executionContext.getBanksaladUserId();
    final String organizationId = organization.getOrganizationId();
    AccountTransactionResponse expectedAccountTransactionResponse = respondAccountTransactionResponseWithTwoPages();
    expectedAccountTransactionResponse.getTransList().forEach(accountTransaction -> {
          accountTransaction.setAccountNum(accountSummary.getAccountNum());
          accountTransaction.setSeqno(accountSummary.getSeqno());
        }
    );
    when(externalApiService.getAccountTransactions(executionContext, organization, accountSummary))
        .thenReturn(expectedAccountTransactionResponse);

    // When
    List<AccountTransaction> actualAccountTransactions = accountService
        .listAccountTransactions(executionContext, organization, List.of(accountSummary));

    // Then
    // Check the new response were inserted.
    assertEquals(3, accountTransactionRepository.count());
    assertEquals(3, accountTransactionInterestRepository.count());
    // Compare API response with modified result from the loan service.
    assertUniqueTransNo(actualAccountTransactions, bankSaladUserId, organizationId);
  }

  //  @Test
  @DisplayName("6.7.4 기존에 있던 1페이지 짜리 트랜잭션 응답")
  public void givenRequest_whenListAccountTransactions_thenExistingTwoPageResponse() {
    // Given
    final ExecutionContext executionContext = getExecutionContext();
    final Organization organization = getOrganization();
    final AccountSummary accountSummary = getAccount();
    final Long bankSaladUserId = executionContext.getBanksaladUserId();
    final String organizationId = organization.getOrganizationId();
    AccountTransactionResponse expectedAccountTransactionResponse = respondAccountTransactionResponseWithOnePage();
    expectedAccountTransactionResponse.getTransList().forEach(accountTransaction -> {
          accountTransaction.setAccountNum(accountSummary.getAccountNum());
          accountTransaction.setSeqno(accountSummary.getSeqno());
        }
    );
    when(externalApiService.getAccountTransactions(executionContext, organization, accountSummary))
        .thenReturn(expectedAccountTransactionResponse);

    // When
    List<AccountTransaction> actualAccountTransactions = accountService
        .listAccountTransactions(executionContext, organization, List.of(accountSummary));

    // Then
    // Check the new response were inserted.
    assertEquals(1, accountTransactionRepository.count());
    assertEquals(1, accountTransactionInterestRepository.count());
    // Compare API response with modified result from the loan service.
    assertUniqueTransNo(actualAccountTransactions, bankSaladUserId, organizationId);
  }

  private void assertUniqueTransNo(List<AccountTransaction> actualAccountTransactions, Long bankSaladUserId,
      String organizationId) {
    List<String> expectedUniqueTransNoList = actualAccountTransactions.stream()
        .map(accountTransaction -> HashUtil.hashCat(Arrays.asList(accountTransaction.getTransDtime(),
            accountTransaction.getTransNo(), accountTransaction.getBalanceAmt().toString())))
        .collect(Collectors.toList());
    List<String> actualUniqueTransNoList = actualAccountTransactions.stream()
        .map(accountTransaction -> {
          final String accountNum = accountTransaction.getAccountNum();
          final String seqno = accountTransaction.getSeqno();
          final Integer transactionYearMonth = Integer.valueOf(accountTransaction.getTransDtime().substring(0, 6));
          final String uniqueTransNo = HashUtil.hashCat(Arrays.asList(accountTransaction.getTransDtime(),
              accountTransaction.getTransNo(), accountTransaction.getBalanceAmt().toString()));
          return accountTransactionRepository
              .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndTransactionYearMonthAndUniqueTransNo(
                  bankSaladUserId, organizationId, accountNum, seqno, transactionYearMonth, uniqueTransNo
              )
              .get()
              .getUniqueTransNo();
        })
        .collect(Collectors.toList());
    for (int i = 0; i < expectedUniqueTransNoList.size(); i++) {
      assertEquals(expectedUniqueTransNoList.get(i), actualUniqueTransNoList.get(i));
    }
  }

  @Test
  @Transactional
  @DisplayName("updateAccountTimestamp 성공 케이스")
  public void updateAccountTimestamp_success() {
    saveAccountSummaryEntity();
    accountSummaryService.updateSearchTimestamp(banksaladUserId, organizationId, accountAssembler());
    assertEquals(1, accountSummaryRepository.findAll().size());

    AccountSummaryEntity entity = accountSummaryRepository.findAll().get(0);
    assertEquals(1000L, entity.getBasicSearchTimestamp());
    assertEquals(2000L, entity.getDetailSearchTimestamp());
    assertEquals(3000L, entity.getOperatingLeaseBasicSearchTimestamp());

  }

  @Test
  @Transactional
  @DisplayName("updateAccountTimestamp account가 넘어오지 않은경우.")
  public void updateAccountTimestamp_invalid_account() {
    saveAccountSummaryEntity();
    Exception exception = assertThrows(
        Exception.class,
        () -> accountSummaryService.updateSearchTimestamp(banksaladUserId, organizationId, null)
    );
    assertThat(exception).isInstanceOf(CollectRuntimeException.class);
    assertEquals("Invalid account", exception.getMessage());
  }

  @Test
  @Transactional
  @DisplayName("updateAccountTimestamp accountList table에 데이터가 없는경우.")
  public void updateAccountTimestamp_nodata() {
    Exception exception = assertThrows(
        Exception.class,
        () -> accountSummaryService.updateSearchTimestamp(banksaladUserId, organizationId, accountAssembler())
    );
    assertThat(exception).isInstanceOf(CollectRuntimeException.class);
    assertEquals("No data AccountSummaryEntity", exception.getMessage());
  }

  private void saveAccountSummaryEntity() {
    accountSummaryRepository.save(
        AccountSummaryEntity.builder()
            .syncedAt(LocalDateTime.now())
            .banksaladUserId(banksaladUserId)
            .organizationId(organizationId)
            .accountNum(accountNum)
            .seqno(SEQNO1)
            .isConsent(true)
            .prodName("prodName")
            .accountType("")
            .accountStatus("")
            .build()
    );
  }

  private AccountSummary accountAssembler() {
    return AccountSummary.builder()
        .accountNum(accountNum)
        .seqno(SEQNO1)
        .basicSearchTimestamp(1000L)
        .detailSearchTimestamp(2000L)
        .operatingLeaseBasicSearchTimestamp(3000L)
        .build();
  }

  private ExecutionContext getExecutionContext() {
    return ExecutionContext.builder()
        .organizationHost("http://" + ORGANIZATION_HOST)
        .accessToken(ACCESS_TOKEN)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .executionRequestId(UUID.randomUUID().toString())
        .syncStartedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
        .build();
  }

  private Organization getOrganization() {
    return Organization.builder()
        .sector(SECTOR)
        .industry(INDUSTRY)
        .organizationId(ORGANIZATION_ID)
        .organizationCode(ORGANIZATION_CODE)
        .domain(ORGANIZATION_HOST)
        .build();
  }

  private AccountSummary getAccount() {
    return AccountSummary.builder()
        .accountNum(ACCOUNT_NUMBER)
        .isConsent(TRUE)
        .seqno(SEQNO1)
        .prodName(PRODUCT_NAME)
        .accountType(ACCOUNT_TYPE)
        .accountStatus(ACCOUNT_STATUS)
        .build();
  }

  private AccountBasicResponse getAccountBasicResponse() {
    return AccountBasicResponse.builder()
        .rspCode("000")
        .rspMsg("rep_msg")
        .searchTimestamp(1000)
        .holderName("대출차주명")
        .issueDate("20210210")
        .expDate("20221231")
        .lastOfferedRate(BigDecimal.valueOf(2.117))
        .repayDate("03")
        .repayMethod("01")
        .repayOrgCode("B01")
        .repayAccountNum("11022212345")
        .build();
  }
}
