package com.banksalad.collectmydata.capital.common.db.entity.mapper;

import com.banksalad.collectmydata.capital.common.TestHelper;
import com.banksalad.collectmydata.capital.common.db.entity.AccountTransactionEntity;
import com.banksalad.collectmydata.capital.common.db.entity.AccountTransactionInterestEntity;
import com.banksalad.collectmydata.capital.common.db.repository.AccountTransactionInterestRepository;
import com.banksalad.collectmydata.capital.common.db.repository.AccountTransactionRepository;
import com.banksalad.collectmydata.capital.loan.dto.LoanAccountTransaction;
import com.banksalad.collectmydata.capital.loan.dto.LoanAccountTransactionInterest;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Optional;

import static com.banksalad.collectmydata.capital.common.TestHelper.ACCOUNT_NUM;
import static com.banksalad.collectmydata.capital.common.TestHelper.BALANCE_AMT;
import static com.banksalad.collectmydata.capital.common.TestHelper.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.capital.common.TestHelper.INT_NO;
import static com.banksalad.collectmydata.capital.common.TestHelper.ORGANIZATION_ID;
import static com.banksalad.collectmydata.capital.common.TestHelper.PRINCIPAL_AMT;
import static com.banksalad.collectmydata.capital.common.TestHelper.SEQNO1;
import static com.banksalad.collectmydata.capital.common.TestHelper.SYNCED_AT;
import static com.banksalad.collectmydata.capital.common.TestHelper.TRANSACTION_YEAR_MONTH;
import static com.banksalad.collectmydata.capital.common.TestHelper.TRANS_AMT;
import static com.banksalad.collectmydata.capital.common.TestHelper.UNIQUE_TRANS_NO;
import static com.banksalad.collectmydata.capital.common.TestHelper.createAccountTransactionEntity;
import static com.banksalad.collectmydata.capital.common.TestHelper.createAccountTransactionInterestEntity;
import static com.banksalad.collectmydata.capital.common.TestHelper.generateLoanAccountTransaction;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class AccountTransactionEntityMapperTest {

  private final AccountTransactionMapper accountTransactionMapper = Mappers.getMapper(AccountTransactionMapper.class);
  private final AccountTransactionInterestMapper accountTransactionInterestMapper = Mappers
      .getMapper(AccountTransactionInterestMapper.class);

  @MockBean
  private AccountTransactionRepository accountTransactionRepository;

  @MockBean
  private AccountTransactionInterestRepository accountTransactionInterestRepository;

  @Test
  @DisplayName("API 응답이 account_transaction 테이블에 있는데 내용이 다른 경우 UPDATE")
  public void updateAccountTransactionEntityFromAccountTransaction() {
    // Given
    Mockito.when(accountTransactionRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndTransactionYearMonthAndUniqueTransNo(
            BANKSALAD_USER_ID, ORGANIZATION_ID, ACCOUNT_NUM, SEQNO1, TRANSACTION_YEAR_MONTH, UNIQUE_TRANS_NO
        ))
        .thenReturn(Optional.ofNullable(TestHelper.createAccountTransactionEntity()));

    // When
    AccountTransactionEntity accountTransactionEntity = queryAccountTransaction();
    // Create an empty transaction object.
    LoanAccountTransaction loanAccountTransaction = LoanAccountTransaction.builder().build();
    // We simulate some changes against the empty transaction object.
    loanAccountTransaction.setTransAmt(TRANS_AMT.add(BigDecimal.valueOf(-10)));
    loanAccountTransaction.setBalanceAmt(BALANCE_AMT.add(BigDecimal.valueOf(10)));
    // At this time, all fields except `transAmt` and `balanceAmt` are in null state.
    // Try to update only with a few non-null fields.
    accountTransactionMapper.updateEntityFromDto(loanAccountTransaction, accountTransactionEntity);

    // Then
    // See `accountTransactionEntity` was updated only with `transAmt` and `balanceAmt`.
    assertThat(accountTransactionEntity.getTransAmt()).isEqualTo(TRANS_AMT.add(BigDecimal.valueOf(-10)));
    assertThat(accountTransactionEntity.getBalanceAmt()).isEqualTo(BALANCE_AMT.add(BigDecimal.valueOf(10)));
    assertThat(accountTransactionEntity.getPrincipalAmt()).isEqualTo(PRINCIPAL_AMT);
  }

  @Test
  @DisplayName("API 응답이 account_transaction 테이블에 없을 경우 INSERT")
  public void insertAccountTransactionEntityFromAccountTransaction() {
    // Given
    Mockito.when(accountTransactionRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndTransactionYearMonthAndUniqueTransNo(
            BANKSALAD_USER_ID, ORGANIZATION_ID, ACCOUNT_NUM, SEQNO1, TRANSACTION_YEAR_MONTH, UNIQUE_TRANS_NO
        ))
        .thenReturn(Optional.empty());

    // When
    AccountTransactionEntity accountTransactionEntity = queryAccountTransaction();
    // We simulate to get an `LoanAccountTransaction` from an external mydata capital API.
    LoanAccountTransaction loanAccountTransaction = generateLoanAccountTransaction();
    // At this time, we transfer a fully-packed object to the mapper.
    // Then the mapper will pour `loanAccountTransaction` into `accountTransactionEntity`.
    accountTransactionMapper.updateEntityFromDto(loanAccountTransaction, accountTransactionEntity);

    // Then
    assertThat(accountTransactionEntity).usingRecursiveComparison().isEqualTo(createAccountTransactionEntity());
  }

  @Test
  @DisplayName("기존 account_transaction_interest 테이블 레코드 삭제하고 API 응답을 INSERT")
  public void rewriteAccountTransactionInterestEntityFromAccountTransactionInterest() {
    // Given
    // Assume we have gotten an `AccountTransactionEntity` from the above test cases.
    AccountTransactionEntity accountTransactionEntity = createAccountTransactionEntity();
    // `LoanAccountTransactionInterest` is acquired from the external transaction API.
    LoanAccountTransactionInterest loanAccountTransactionInterest = generateLoanAccountTransaction().getIntList().get(0);

    // When
    // The following deletion has no meaning but just shows our business logic.
    accountTransactionInterestRepository
        .deleteByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndTransactionYearMonthAndUniqueTransNo(
            BANKSALAD_USER_ID, ORGANIZATION_ID, ACCOUNT_NUM, SEQNO1, TRANSACTION_YEAR_MONTH, UNIQUE_TRANS_NO);
    // Create an empty `AccountTransactionInterestEntity` object.
    AccountTransactionInterestEntity accountTransactionInterestEntity = AccountTransactionInterestEntity.builder()
        .build();
    // Fill `AccountTransactionInterestEntity` object with accountTransactionEntity, interest sequence number,
    // and loanAccountTransactionInterest.
    accountTransactionInterestMapper
        .updateEntityFromDto(accountTransactionEntity, INT_NO, loanAccountTransactionInterest,
            accountTransactionInterestEntity);

    // Then
    assertThat(accountTransactionInterestEntity).usingRecursiveComparison()
        .isEqualTo(createAccountTransactionInterestEntity());
  }

  private AccountTransactionEntity queryAccountTransaction() {
    return accountTransactionRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndTransactionYearMonthAndUniqueTransNo(
            BANKSALAD_USER_ID, ORGANIZATION_ID, ACCOUNT_NUM, SEQNO1, TRANSACTION_YEAR_MONTH, UNIQUE_TRANS_NO
        ).orElse(AccountTransactionEntity.builder()
            .transactionYearMonth(TRANSACTION_YEAR_MONTH)
            .syncedAt(SYNCED_AT)
            .banksaladUserId(BANKSALAD_USER_ID)
            .organizationId(ORGANIZATION_ID)
            .accountNum(ACCOUNT_NUM)
            .seqno(SEQNO1)
            .uniqueTransNo(UNIQUE_TRANS_NO)
            .build());
  }
}
