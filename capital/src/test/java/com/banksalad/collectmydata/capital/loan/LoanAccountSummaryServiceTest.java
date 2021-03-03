package com.banksalad.collectmydata.capital.loan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.banksalad.collectmydata.capital.common.db.entity.AccountListEntity;
import com.banksalad.collectmydata.capital.common.db.repository.AccountListRepository;
import com.banksalad.collectmydata.capital.common.dto.AccountSummary;
import com.banksalad.collectmydata.common.exception.CollectRuntimeException;
import javax.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DisplayName("LoanAccountService Test")
public class LoanAccountSummaryServiceTest {

  @Autowired
  private LoanAccountService loanAccountService;

  @Autowired
  private AccountListRepository accountListRepository;

  private long banksaladUserId = 1L;
  private String organizationId = "shinhancard";
  private String accountNum = "1234567812345678";
  private Integer seqno = 1;

  @Test
  @Transactional
  @DisplayName("updateAccountTimestamp 성공 케이스")
  public void updateAccountTimestamp_success() {
    saveAccountListEntity();
    loanAccountService.updateSearchTimestampOnAccount(banksaladUserId, organizationId, accountAssembler());
    assertEquals(1, accountListRepository.findAll().size());

    AccountListEntity entity = accountListRepository.findAll().get(0);
    assertEquals(1000l, entity.getBasicSearchTimestamp());
    assertEquals(2000l, entity.getDetailSearchTimestamp());
    assertEquals(3000l, entity.getOperatingLeaseBasicSearchTimestamp());

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
            .seqno(1)
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
        .seqno(1)
        .basicSearchTimestamp(1000l)
        .detailSearchTimestamp(2000l)
        .operatingLeaseBasicSearchTimestamp(3000l)
        .build();
  }
}
