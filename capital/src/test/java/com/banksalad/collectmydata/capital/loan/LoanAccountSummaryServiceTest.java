package com.banksalad.collectmydata.capital.loan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.banksalad.collectmydata.capital.common.db.entity.AccountListEntity;
import com.banksalad.collectmydata.capital.common.db.repository.AccountListRepository;
import com.banksalad.collectmydata.capital.common.db.repository.AccountTransactionInterestRepository;
import com.banksalad.collectmydata.capital.common.db.repository.AccountTransactionRepository;
import com.banksalad.collectmydata.capital.common.db.repository.UserSyncStatusRepository;
import com.banksalad.collectmydata.capital.common.dto.AccountSummary;
import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.capital.common.service.ExternalApiService;
import com.banksalad.collectmydata.capital.loan.dto.LoanAccountTransaction;
import com.banksalad.collectmydata.capital.loan.dto.LoanAccountTransactionResponse;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.crypto.HashUtil;
import com.banksalad.collectmydata.common.enums.Industry;
import com.banksalad.collectmydata.common.enums.MydataSector;
import com.banksalad.collectmydata.common.exception.CollectRuntimeException;
import com.banksalad.collectmydata.common.util.DateUtil;
import javax.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.banksalad.collectmydata.capital.common.TestHelper.respondLoanAccountTransactionResponseWithEmptyPages;
import static com.banksalad.collectmydata.capital.common.TestHelper.respondLoanAccountTransactionResponseWithOnePage;
import static com.banksalad.collectmydata.capital.common.TestHelper.respondLoanAccountTransactionResponseWithTwoPages;
import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
@DisplayName("LoanAccountSummaryService Test")
public class LoanAccountSummaryServiceTest {

  @MockBean
  private ExternalApiService externalApiService;

  @Autowired
  private LoanAccountService loanAccountService;

  @Autowired
  private AccountListRepository accountListRepository;

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
    accountTransactionRepository.deleteAll();
    accountTransactionInterestRepository.deleteAll();
    userSyncStatusRepository.deleteAll();
  }

  @Test
  @DisplayName("6.7.4 빈 트랜잭션 응답")
  public void givenRequest_whenListAccountTransactions_thenEmptyPageResponse() {
    // Given
    final ExecutionContext executionContext = getExecutionContext();
    final Organization organization = getOrganization();
    final AccountSummary accountSummary = getAccount();
    when(externalApiService.getAccountTransactions(executionContext, organization, accountSummary))
        .thenReturn(respondLoanAccountTransactionResponseWithEmptyPages());

    // When
    List<LoanAccountTransaction> response = loanAccountService
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
    LoanAccountTransactionResponse expectedLoanAccountTransactionResponse = respondLoanAccountTransactionResponseWithTwoPages();
    expectedLoanAccountTransactionResponse.getTransList().forEach(loanAccountTransaction -> {
          loanAccountTransaction.setAccountNum(accountSummary.getAccountNum());
          loanAccountTransaction.setSeqno(accountSummary.getSeqno());
        }
    );
    when(externalApiService.getAccountTransactions(executionContext, organization, accountSummary))
        .thenReturn(expectedLoanAccountTransactionResponse);

    // When
    List<LoanAccountTransaction> actualLoanAccountTransactions = loanAccountService
        .listAccountTransactions(executionContext, organization, List.of(accountSummary));

    // Then
    // Check the new response were inserted.
    assertEquals(3, accountTransactionRepository.count());
    assertEquals(3, accountTransactionInterestRepository.count());
    // Compare API response with modified result from the loan service.
    assertUniqueTransNo(actualLoanAccountTransactions, bankSaladUserId, organizationId);
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
    LoanAccountTransactionResponse expectedLoanAccountTransactionResponse = respondLoanAccountTransactionResponseWithOnePage();
    expectedLoanAccountTransactionResponse.getTransList().forEach(loanAccountTransaction -> {
          loanAccountTransaction.setAccountNum(accountSummary.getAccountNum());
          loanAccountTransaction.setSeqno(accountSummary.getSeqno());
        }
    );
    when(externalApiService.getAccountTransactions(executionContext, organization, accountSummary))
        .thenReturn(expectedLoanAccountTransactionResponse);

    // When
    List<LoanAccountTransaction> actualLoanAccountTransactions = loanAccountService
        .listAccountTransactions(executionContext, organization, List.of(accountSummary));

    // Then
    // Check the new response were inserted.
    assertEquals(1, accountTransactionRepository.count());
    assertEquals(1, accountTransactionInterestRepository.count());
    // Compare API response with modified result from the loan service.
    assertUniqueTransNo(actualLoanAccountTransactions, bankSaladUserId, organizationId);
  }

  private void assertUniqueTransNo(List<LoanAccountTransaction> actualLoanAccountTransactions, Long bankSaladUserId,
      String organizationId) {
    List<String> expectedUniqueTransNoList = actualLoanAccountTransactions.stream()
        .map(loanAccountTransaction -> HashUtil.hashCat(Arrays.asList(loanAccountTransaction.getTransDtime(),
            loanAccountTransaction.getTransNo(), loanAccountTransaction.getBalanceAmt().toString())))
        .collect(Collectors.toList());
    List<String> actualUniqueTransNoList = actualLoanAccountTransactions.stream()
        .map(loanAccountTransaction -> {
          final String accountNum = loanAccountTransaction.getAccountNum();
          final String seqno = loanAccountTransaction.getSeqno();
          final Integer transactionYearMonth = Integer.valueOf(loanAccountTransaction.getTransDtime().substring(0, 6));
          final String uniqueTransNo = HashUtil.hashCat(Arrays.asList(loanAccountTransaction.getTransDtime(),
              loanAccountTransaction.getTransNo(), loanAccountTransaction.getBalanceAmt().toString()));
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
    saveAccountListEntity();
    loanAccountService.updateSearchTimestampOnAccount(banksaladUserId, organizationId, accountAssembler());
    assertEquals(1, accountListRepository.findAll().size());

    AccountListEntity entity = accountListRepository.findAll().get(0);
    assertEquals(1000L, entity.getBasicSearchTimestamp());
    assertEquals(2000L, entity.getDetailSearchTimestamp());
    assertEquals(3000L, entity.getOperatingLeaseBasicSearchTimestamp());

  }

  @Test
  @Transactional
  @DisplayName("updateAccountTimestamp account가 넘어오지 않은경우.")
  public void updateAccountTimestamp_invalid_account() {
    saveAccountListEntity();
    Exception exception = assertThrows(
        Exception.class,
        () -> loanAccountService.updateSearchTimestampOnAccount(banksaladUserId, organizationId, null)
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
        () -> loanAccountService.updateSearchTimestampOnAccount(banksaladUserId, organizationId, accountAssembler())
    );
    assertThat(exception).isInstanceOf(CollectRuntimeException.class);
    assertEquals("No data AccountListEntity", exception.getMessage());
  }

  private void saveAccountListEntity() {
    accountListRepository.save(
        AccountListEntity.builder()
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
}
