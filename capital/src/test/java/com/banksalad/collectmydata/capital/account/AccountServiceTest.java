package com.banksalad.collectmydata.capital.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.banksalad.collectmydata.capital.account.dto.AccountDetailResponse;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
