package com.banksalad.collectmydata.capital.lease.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.banksalad.collectmydata.capital.account.dto.Account;
import com.banksalad.collectmydata.capital.common.db.entity.AccountListEntity;
import com.banksalad.collectmydata.capital.common.db.entity.OperatingLeaseEntity;
import com.banksalad.collectmydata.capital.common.db.entity.OperatingLeaseHistoryEntity;
import com.banksalad.collectmydata.capital.common.db.repository.AccountListRepository;
import com.banksalad.collectmydata.capital.common.db.repository.OperatingLeaseHistoryRepository;
import com.banksalad.collectmydata.capital.common.db.repository.OperatingLeaseRepository;
import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.capital.common.service.ExternalApiService;
import com.banksalad.collectmydata.capital.lease.dto.OperatingLeaseBasicResponse;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import javax.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
@DisplayName("LeaseService Test")
public class LeaseServiceTest {

  @MockBean
  private ExternalApiService externalApiService;

  @Autowired
  private LeaseService leaseService;

  @Autowired
  private OperatingLeaseRepository operatingLeaseRepository;

  @Autowired
  private OperatingLeaseHistoryRepository operatingLeaseHistoryRepository;

  @Autowired
  private AccountListRepository accountListRepository;

  private long banksaladUserId = 1L;
  private String organizationId = "shinhancard";
  private String accountNum = "1234567812345678";

  @Test
  @Transactional
  @DisplayName("운용리스 기본정보 조회 서비스 로직 성공케이스")
  public void getOperatingLeaseBasic_firstInflow() {

    LocalDateTime now = LocalDateTime.now();
    ExecutionContext context = ExecutionContext.builder()
        .organizationId(organizationId)
        .banksaladUserId(banksaladUserId)
        .syncStartedAt(now)
        .build();

    Organization organization = Organization.builder().build();

    Account account = Account.builder()
        .accountNum(accountNum)
        .seqno(1)
        .build();

    accountListRepository.save(
        AccountListEntity.builder()
            .syncedAt(LocalDateTime.now())
            .banksaladUserId(banksaladUserId)
            .organizationId(organizationId)
            .accountNum(accountNum)
            .seqno(1)
            .isConsent(true)
            .prodName("prodName")
            .accountType("")
            .accountStatus("")
            .build()
    );

    List<Account> accountList = List.of(account);

    when(externalApiService.getOperatingLeaseBasic(context, organization, account))
        .thenReturn(
            OperatingLeaseBasicResponse.builder()
                .rspCode("0000")
                .rspMsg("success")
                .searchTimestamp(1000L)
                .holderName("holderName")
                .issueDate("20210214")
                .expDate("20210314")
                .repayDate("14")
                .repayMethod("04")
                .repayOrgCode("020")
                .repayAccountNum("1234567812345678")
                .nextRepayDate("20210414")
                .build()
        );
    leaseService.syncLeaseBasic(context, organization, accountList);

    List<OperatingLeaseEntity> operatingLeaseEntities = operatingLeaseRepository.findAll();
    List<OperatingLeaseHistoryEntity> operatingLeaseHistoryEntities = operatingLeaseHistoryRepository.findAll();
    validateEntities(now, operatingLeaseEntities, operatingLeaseHistoryEntities);

    // 재조회시 히스토리 중첩여부 테스트
    leaseService.syncLeaseBasic(context, organization, accountList);

    operatingLeaseEntities = operatingLeaseRepository.findAll();
    operatingLeaseHistoryEntities = operatingLeaseHistoryRepository.findAll();
    validateEntities(now, operatingLeaseEntities, operatingLeaseHistoryEntities);
  }

  private void validateEntities(LocalDateTime now, List<OperatingLeaseEntity> operatingLeaseEntities,
      List<OperatingLeaseHistoryEntity> operatingLeaseHistoryEntities) {
    assertEquals(1, operatingLeaseEntities.size());
    assertThat(operatingLeaseEntities.get(0)).usingRecursiveComparison()
        .ignoringFields("operatingLeaseId", "createdAt", "updatedAt")
        .isEqualTo(
            OperatingLeaseEntity.builder()
                .syncedAt(now)
                .banksaladUserId(banksaladUserId)
                .organizationId(organizationId)
                .accountNum(accountNum)
                .seqno(1)
                .holderName("holderName")
                .issueDate(LocalDate.parse("20210214", DateTimeFormatter.ofPattern("yyyyMMdd")))
                .expDate(LocalDate.parse("20210314", DateTimeFormatter.ofPattern("yyyyMMdd")))
                .repayDate(14)
                .repayMethod("04")
                .repayOrgCode("020")
                .repayAccountNum("1234567812345678")
                .nextRepayDate(LocalDate.parse("20210414", DateTimeFormatter.ofPattern("yyyyMMdd")))
                .build()
        );

    assertEquals(1, operatingLeaseHistoryEntities.size());
    assertThat(operatingLeaseHistoryEntities.get(0)).usingRecursiveComparison()
        .ignoringFields("operatingLeaseHistoryId", "createdAt", "updatedAt")
        .isEqualTo(
            OperatingLeaseHistoryEntity.builder()
                .syncedAt(now)
                .banksaladUserId(banksaladUserId)
                .organizationId(organizationId)
                .accountNum(accountNum)
                .seqno(1)
                .holderName("holderName")
                .issueDate(LocalDate.parse("20210214", DateTimeFormatter.ofPattern("yyyyMMdd")))
                .expDate(LocalDate.parse("20210314", DateTimeFormatter.ofPattern("yyyyMMdd")))
                .repayDate(14)
                .repayMethod("04")
                .repayOrgCode("020")
                .repayAccountNum("1234567812345678")
                .nextRepayDate(LocalDate.parse("20210414", DateTimeFormatter.ofPattern("yyyyMMdd")))
                .build()
        );
  }
}
