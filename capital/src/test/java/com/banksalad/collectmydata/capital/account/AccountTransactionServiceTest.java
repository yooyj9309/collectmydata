package com.banksalad.collectmydata.capital.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.banksalad.collectmydata.capital.account.dto.AccountTransaction;
import com.banksalad.collectmydata.capital.account.dto.AccountTransactionResponse;
import com.banksalad.collectmydata.capital.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.capital.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.capital.common.db.repository.AccountTransactionInterestRepository;
import com.banksalad.collectmydata.capital.common.db.repository.AccountTransactionRepository;
import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.capital.common.service.ExternalApiService;
import com.banksalad.collectmydata.capital.summary.dto.AccountSummary;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.crypto.HashUtil;
import com.banksalad.collectmydata.common.util.DateUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.banksalad.collectmydata.capital.common.TestHelper.ACCESS_TOKEN;
import static com.banksalad.collectmydata.capital.common.TestHelper.ACCOUNT_NUM;
import static com.banksalad.collectmydata.capital.common.TestHelper.ACCOUNT_STATUS;
import static com.banksalad.collectmydata.capital.common.TestHelper.ACCOUNT_TYPE;
import static com.banksalad.collectmydata.capital.common.TestHelper.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.capital.common.TestHelper.INDUSTRY;
import static com.banksalad.collectmydata.capital.common.TestHelper.ORGANIZATION_CODE;
import static com.banksalad.collectmydata.capital.common.TestHelper.ORGANIZATION_HOST;
import static com.banksalad.collectmydata.capital.common.TestHelper.ORGANIZATION_ID;
import static com.banksalad.collectmydata.capital.common.TestHelper.PRODUCT_NAME;
import static com.banksalad.collectmydata.capital.common.TestHelper.SECTOR;
import static com.banksalad.collectmydata.capital.common.TestHelper.SEQNO1;
import static com.banksalad.collectmydata.capital.common.TestHelper.respondAccountTransactionResponseWithEmptyPages;
import static com.banksalad.collectmydata.capital.common.TestHelper.respondAccountTransactionResponseWithOnePage;
import static com.banksalad.collectmydata.capital.common.TestHelper.respondAccountTransactionResponseWithTwoPages;
import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
public class AccountTransactionServiceTest {

  @MockBean
  private ExternalApiService externalApiService;

  @Autowired
  private AccountService accountService;

  @Autowired
  private AccountSummaryRepository accountSummaryRepository;

  @Autowired
  private AccountTransactionRepository accountTransactionRepository;

  @Autowired
  private AccountTransactionInterestRepository accountTransactionInterestRepository;

  @AfterEach
  void cleanBefore() {
    accountSummaryRepository.deleteAll();
    accountTransactionRepository.deleteAll();
    accountTransactionInterestRepository.deleteAll();
  }

  @Test
  @DisplayName("6.7.4 (1) 빈 트랜잭션 응답")
  public void givenRequest_whenListAccountTransactions_thenEmptyPageResponse() {
    // Given
    final ExecutionContext executionContext = getExecutionContext();
    final Organization organization = getOrganization();
    final AccountSummary accountSummary = getAccountSummary();
    final String orgCode = organization.getOrganizationCode();
    final String accountNum = accountSummary.getAccountNum();
    final String seqno = accountSummary.getSeqno();

    saveAccountSummaryEntity();
    when(externalApiService.getAccountTransactions(executionContext, orgCode, accountNum, seqno,
        DateUtil.utcLocalDateTimeToKstDateString(accountSummary.getTransactionSyncedAt()),
        DateUtil.utcLocalDateTimeToKstDateString(executionContext.getSyncStartedAt())))
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
  @DisplayName("6.7.4 (2) 새로운 2페이지 짜리 트랜잭션 응답")
  public void givenRequest_whenListAccountTransactions_thenNewTwoPageResponse() {
    // Given
    final ExecutionContext executionContext = getExecutionContext();
    final Organization organization = getOrganization();
    final AccountSummary accountSummary = getAccountSummary();
    final Long bankSaladUserId = executionContext.getBanksaladUserId();
    final String organizationId = organization.getOrganizationId();
    final String orgCode = organization.getOrganizationCode();
    final String accountNum = accountSummary.getAccountNum();
    final String seqno = accountSummary.getSeqno();

    saveAccountSummaryEntity();
    AccountTransactionResponse expectedAccountTransactionResponse = respondAccountTransactionResponseWithTwoPages();
    expectedAccountTransactionResponse.getTransList().forEach(accountTransaction -> {
          accountTransaction.setAccountNum(accountSummary.getAccountNum());
          accountTransaction.setSeqno(accountSummary.getSeqno());
        }
    );
    when(externalApiService.getAccountTransactions(executionContext, orgCode, accountNum, seqno,
        DateUtil.utcLocalDateTimeToKstDateString(accountSummary.getTransactionSyncedAt()),
        DateUtil.utcLocalDateTimeToKstDateString(executionContext.getSyncStartedAt())))
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

  @Test
  @DisplayName("6.7.4 (3) 기존에 있던 1페이지 짜리 트랜잭션 응답")
  public void givenRequest_whenListAccountTransactions_thenExistingTwoPageResponse() {
    // Given
    final ExecutionContext executionContext = getExecutionContext();
    final Organization organization = getOrganization();
    final AccountSummary accountSummary = getAccountSummary();
    final Long bankSaladUserId = executionContext.getBanksaladUserId();
    final String organizationId = organization.getOrganizationId();
    final String orgCode = organization.getOrganizationCode();
    final String accountNum = accountSummary.getAccountNum();
    final String seqno = accountSummary.getSeqno();

    saveAccountSummaryEntity();
    AccountTransactionResponse expectedAccountTransactionResponse = respondAccountTransactionResponseWithOnePage();
    expectedAccountTransactionResponse.getTransList().forEach(accountTransaction -> {
          accountTransaction.setAccountNum(accountSummary.getAccountNum());
          accountTransaction.setSeqno(accountSummary.getSeqno());
        }
    );
    when(externalApiService.getAccountTransactions(executionContext, orgCode, accountNum, seqno,
        DateUtil.utcLocalDateTimeToKstDateString(accountSummary.getTransactionSyncedAt()),
        DateUtil.utcLocalDateTimeToKstDateString(executionContext.getSyncStartedAt())))
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
        .map(accountTransaction -> HashUtil.hashCat(accountTransaction.getTransDtime(),
            accountTransaction.getTransNo(), accountTransaction.getBalanceAmt().toString()))
        .collect(Collectors.toList());
    List<String> actualUniqueTransNoList = actualAccountTransactions.stream()
        .map(accountTransaction -> {
          final String accountNum = accountTransaction.getAccountNum();
          final String seqno = accountTransaction.getSeqno();
          final Integer transactionYearMonth = Integer
              .valueOf(accountTransaction.getTransDtime().substring(0, 6));
          final String uniqueTransNo = HashUtil.hashCat(accountTransaction.getTransDtime(),
              accountTransaction.getTransNo(), accountTransaction.getBalanceAmt().toString());
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

  private void saveAccountSummaryEntity() {
    accountSummaryRepository.save(
        AccountSummaryEntity.builder()
            .syncedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
            .banksaladUserId(BANKSALAD_USER_ID)
            .organizationId(ORGANIZATION_ID)
            .accountNum(ACCOUNT_NUM)
            .seqno(SEQNO1)
            .isConsent(true)
            .prodName("prodName")
            .accountType("")
            .accountStatus("")
            .build()
    );
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

  private AccountSummary getAccountSummary() {
    return AccountSummary.builder()
        .accountNum(ACCOUNT_NUM)
        .isConsent(TRUE)
        .seqno(SEQNO1)
        .prodName(PRODUCT_NAME)
        .accountType(ACCOUNT_TYPE)
        .accountStatus(ACCOUNT_STATUS)
        .transactionSyncedAt(DateUtil.toLocalDateTime("20210121", "101010"))
        .operatingLeaseTransactionSyncedAt(DateUtil.toLocalDateTime("20210121", "101010"))
        .build();
  }
}
