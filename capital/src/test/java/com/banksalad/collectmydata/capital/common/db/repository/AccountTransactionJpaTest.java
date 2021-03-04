package com.banksalad.collectmydata.capital.common.db.repository;

import com.banksalad.collectmydata.capital.common.db.entity.AccountTransactionEntity;
import com.banksalad.collectmydata.capital.common.db.entity.AccountTransactionInterestEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static com.banksalad.collectmydata.capital.common.TestHelper.ACCOUNT_NUM;
import static com.banksalad.collectmydata.capital.common.TestHelper.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.capital.common.TestHelper.INT_NO;
import static com.banksalad.collectmydata.capital.common.TestHelper.ORGANIZATION_ID;
import static com.banksalad.collectmydata.capital.common.TestHelper.SEQNO1;
import static com.banksalad.collectmydata.capital.common.TestHelper.TRANSACTION_YEAR_MONTH;
import static com.banksalad.collectmydata.capital.common.TestHelper.UNIQUE_TRANS_NO;
import static com.banksalad.collectmydata.capital.common.TestHelper.createAccountTransactionEntity;
import static com.banksalad.collectmydata.capital.common.TestHelper.createAccountTransactionInterestEntity;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
public class AccountTransactionJpaTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private AccountTransactionInterestRepository accountTransactionInterestRepository;

  @Autowired
  private AccountTransactionRepository accountTransactionRepository;

  @Test
  @DisplayName("6.7.4 (1) account_transaction 테이블 조회하기 (존재하는 경우)")
  public void queryExistingAccountTransactionsTest() {
    // given
    entityManager.persist(createAccountTransactionEntity());
    // when
    Optional<AccountTransactionEntity> accountTransactionEntity = accountTransactionRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndTransactionYearMonthAndUniqueTransNo(
            BANKSALAD_USER_ID, ORGANIZATION_ID, ACCOUNT_NUM, SEQNO1, TRANSACTION_YEAR_MONTH,
            UNIQUE_TRANS_NO
        );
    List<AccountTransactionEntity> accountTransactionEntities = accountTransactionRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndTransactionYearMonthAndUniqueTransNoIn(
            BANKSALAD_USER_ID, ORGANIZATION_ID, ACCOUNT_NUM, SEQNO1, TRANSACTION_YEAR_MONTH,
            List.of(UNIQUE_TRANS_NO)
        );
    // then
    assertThat(accountTransactionEntity.get().getUniqueTransNo()).isEqualTo(UNIQUE_TRANS_NO);
    assertThat(accountTransactionEntities.get(0).getUniqueTransNo()).isEqualTo(UNIQUE_TRANS_NO);
  }

  @Test
  @DisplayName("6.7.4 (2) account_transaction_interest 테이블 삭제하기")
  public void deleteAccountTransactionInterestTest() {
    // given
    entityManager.persist(createAccountTransactionInterestEntity());
    // when
    accountTransactionInterestRepository
        .deleteByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndTransactionYearMonthAndUniqueTransNoIn(
            BANKSALAD_USER_ID, ORGANIZATION_ID, ACCOUNT_NUM, SEQNO1, TRANSACTION_YEAR_MONTH,
            List.of(UNIQUE_TRANS_NO)
        );
    // then
    List<AccountTransactionInterestEntity> accountTransactionInterestEntities = accountTransactionInterestRepository
        .findAll();
    assertThat(accountTransactionInterestEntities.size()).isEqualTo(0);
  }

  @Test
  @DisplayName("6.7.4 (3) account_transaction_interest 테이블 입력하기")
  public void insertAccountTransactionInterestTest() {
    // given
    // when
    accountTransactionInterestRepository.save(createAccountTransactionInterestEntity());
    // then
    List<AccountTransactionInterestEntity> accountTransactionInterestEntities = accountTransactionInterestRepository
        .findAll();
    assertThat(accountTransactionInterestEntities.get(0).getIntNo()).isEqualTo(INT_NO);
  }

}
