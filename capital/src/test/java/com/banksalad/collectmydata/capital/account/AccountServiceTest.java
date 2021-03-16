package com.banksalad.collectmydata.capital.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.banksalad.collectmydata.capital.account.dto.AccountBasicResponse;
import com.banksalad.collectmydata.capital.account.dto.AccountDetailResponse;
import com.banksalad.collectmydata.capital.common.db.entity.AccountBasicEntity;
import com.banksalad.collectmydata.capital.common.db.entity.AccountDetailEntity;
import com.banksalad.collectmydata.capital.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.capital.common.db.repository.AccountBasicRepository;
import com.banksalad.collectmydata.capital.common.db.repository.AccountDetailRepository;
import com.banksalad.collectmydata.capital.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.capital.common.db.repository.AccountTransactionInterestRepository;
import com.banksalad.collectmydata.capital.common.db.repository.AccountTransactionRepository;
import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.capital.common.service.AccountSummaryService;
import com.banksalad.collectmydata.capital.common.service.ExternalApiService;
import com.banksalad.collectmydata.capital.summary.dto.AccountSummary;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.exception.CollectRuntimeException;
import com.banksalad.collectmydata.common.util.DateUtil;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

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
import static java.lang.Boolean.TRUE;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@Transactional
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
  private AccountDetailRepository accountDetailRepository;

  @Autowired
  private AccountTransactionRepository accountTransactionRepository;

  @Autowired
  private AccountTransactionInterestRepository accountTransactionInterestRepository;

  @AfterEach
  void cleanBefore() {
    accountSummaryRepository.deleteAll();
  }

  @Test
  @DisplayName("6.7.2 account_basic table 에 row 가 있음 && Data Provider API Response 와 다름")
  void givenExistingAccountBasicDifferedWithApiResponse_whenListAccountBasics_ThenUpdateAccountBasic() {
    // Given
    ExecutionContext executionContext = getExecutionContext();
    Organization organization = getOrganization();
    AccountSummary accountSummary = getAccountSummary();
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
            accountSummary.getAccountNum(), accountSummary.getSeqno())
        .orElseThrow(EntityNotFoundException::new);
    assertEquals(accountBasicResponse.getRepayAccountNum(), actualAccountBasicEntity.getRepayAccountNum());
  }

  @Test
  @DisplayName("6.7.2 account_basic table 에 row 가 없음")
  void givenNotExistingAccountBasic_whenListAccountBasics_ThenSaveAccountBasicAndUpdateSearchTimestamp() {
    // Given
    ExecutionContext executionContext = getExecutionContext();
    Organization organization = getOrganization();
    AccountSummary accountSummary = getAccountSummary();
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
            accountSummary.getAccountNum(), accountSummary.getSeqno())
        .orElseThrow(EntityNotFoundException::new);
    assertNotNull(actualAccountBasicEntity);

    AccountSummaryEntity actualAccountSummaryEntity = accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
            executionContext.getBanksaladUserId(), organization.getOrganizationId(),
            accountSummary.getAccountNum(), accountSummary.getSeqno())
        .orElseThrow(EntityExistsException::new);
    assertEquals(1000, actualAccountSummaryEntity.getBasicSearchTimestamp());
  }

  @Test
  @DisplayName("6.7.3 account_detail table 에 row 가 있음 && Data Provider API Response 와 다름")
  void givenExistingAccountDetailDifferedWithApiResponse_whenListAccountDetails_ThenUpdateAccountDetail() {
    // Given
    ExecutionContext executionContext = getExecutionContext();
    Organization organization = getOrganization();
    AccountSummary accountSummary = getAccountSummary();
    AccountDetailResponse response = getAccountDetailResponse();

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

    AccountDetailEntity accountDetailEntity = AccountDetailEntity.builder()
        .syncedAt(LocalDateTime.now())
        .banksaladUserId(executionContext.getBanksaladUserId())
        .organizationId(organization.getOrganizationId())
        .accountNum(accountSummary.getAccountNum())
        .seqno(accountSummary.getSeqno())
        .balanceAmt(response.getBalanceAmt())
        .loanPrincipal(BigDecimal.valueOf(9999))
        .nextRepayDate(LocalDate.parse(response.getNextRepayDate(), DateTimeFormatter.ofPattern("yyyyMMdd")))
        .build();
    accountDetailRepository.save(accountDetailEntity);

    given(externalApiService.getAccountDetail(executionContext, organization, accountSummary)).willReturn(response);

    // When
    accountService.listAccountDetails(executionContext, organization, singletonList(accountSummary));

    // Then
    AccountDetailEntity actualAccountDetailEntity = accountDetailRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
            executionContext.getBanksaladUserId(), organization.getOrganizationId(),
            accountSummary.getAccountNum(), accountSummary.getSeqno())
        .orElseThrow(EntityNotFoundException::new);
    assertEquals(response.getLoanPrincipal().setScale(3, RoundingMode.DOWN),
        actualAccountDetailEntity.getLoanPrincipal());
  }

  @Test
  @DisplayName("6.7.3 account_detail table 에 row 가 없음")
  void givenNotExistingAccountDetail_whenListAccountDetails_ThenSaveAccountDetailAndUpdateSearchTimestamp() {
    // Given
    ExecutionContext executionContext = getExecutionContext();
    Organization organization = getOrganization();
    AccountSummary accountSummary = getAccountSummary();
    AccountDetailResponse accountDetailResponse = getAccountDetailResponse();
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

    given(externalApiService.getAccountDetail(executionContext, organization, accountSummary))
        .willReturn(accountDetailResponse);

    // When
    accountService.listAccountDetails(executionContext, organization, singletonList(accountSummary));

    // Then
    AccountDetailEntity actualAccountDetailEntity = accountDetailRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
            executionContext.getBanksaladUserId(), organization.getOrganizationId(),
            accountSummary.getAccountNum(), accountSummary.getSeqno())
        .orElseThrow(EntityNotFoundException::new);
    assertNotNull(actualAccountDetailEntity);

    AccountSummaryEntity actualAccountSummaryEntity = accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
            executionContext.getBanksaladUserId(), organization.getOrganizationId(),
            accountSummary.getAccountNum(), accountSummary.getSeqno())
        .orElseThrow(EntityExistsException::new);
    assertEquals(1000, actualAccountSummaryEntity.getDetailSearchTimestamp());
  }

  @Test
  @Transactional
  @DisplayName("updateAccountTimestamp 성공 케이스")
  public void updateAccountTimestamp_success() {
    saveAccountSummaryEntity();
    accountSummaryService.updateSearchTimestamp(BANKSALAD_USER_ID, ORGANIZATION_ID, accountAssembler());
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
        () -> accountSummaryService.updateSearchTimestamp(BANKSALAD_USER_ID, ORGANIZATION_ID, null)
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
        () -> accountSummaryService.updateSearchTimestamp(BANKSALAD_USER_ID, ORGANIZATION_ID, accountAssembler())
    );
    assertThat(exception).isInstanceOf(CollectRuntimeException.class);
    assertEquals("No data AccountSummaryEntity", exception.getMessage());
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

  private AccountSummary accountAssembler() {
    return AccountSummary.builder()
        .accountNum(ACCOUNT_NUM)
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

  private AccountDetailResponse getAccountDetailResponse() {
    return AccountDetailResponse.builder()
        .rspCode("000")
        .rspMsg("rep_msg")
        .searchTimestamp(1000)
        .balanceAmt(BigDecimal.valueOf(30000.123))
        .loanPrincipal(BigDecimal.valueOf(20000.456))
        .nextRepayDate("20210301")
        .build();
  }
}
