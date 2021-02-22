package com.banksalad.collectmydata.capital.common.db;

import com.banksalad.collectmydata.capital.common.db.entity.AccountTransactionEntity;
import com.banksalad.collectmydata.capital.common.db.entity.AccountTransactionInterestEntity;
import com.banksalad.collectmydata.capital.common.db.repository.AccountTransactionInterestRepository;
import com.banksalad.collectmydata.capital.common.db.repository.AccountTransactionRepository;
import com.banksalad.collectmydata.common.util.DateUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
public class AccountTransactionJpaTest {

  private final int TRANSACTION_YEAR_MONTH = 202102;
  private final long BANKSALAD_USER_ID = 1L;
  private final String ORGANIZATION_ID = "X-loan";
  private final String ACCOUNT_NUM_ENCRYPTED = "a1b2c3d4!";
  private final int SEQNO = 0;
  private final String UNIQUE_TRANS_NO = "1_2-3/4+";
  private final LocalDateTime TRANS_DTIME = DateUtil.toLocalDateTime("20200201", "101010");
  private final String TRANS_NO = "1";
  private final String TRANS_TYPE = "01";
  private final BigDecimal TRANS_AMT = BigDecimal.valueOf(100.001);
  private final BigDecimal BALANCE_AMT = BigDecimal.valueOf(899.999);
  private final BigDecimal PRINCIPAL_AMT = BigDecimal.valueOf(1000.000);
  private final BigDecimal INT_AMT = BigDecimal.valueOf(5L);
  private final int INT_NO = 1;

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
    entityManager.persist(generateAccountTransactionEntity());
    // when
    List<AccountTransactionEntity> accountTransactionEntities = accountTransactionRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndTransactionYearMonthAndUniqueTransNoIn(
            BANKSALAD_USER_ID, ORGANIZATION_ID, ACCOUNT_NUM_ENCRYPTED, SEQNO, TRANSACTION_YEAR_MONTH,
            List.of(UNIQUE_TRANS_NO)
        );
    // then
    assertThat(accountTransactionEntities.get(0).getUniqueTransNo()).isEqualTo(UNIQUE_TRANS_NO);
  }

  @Test
  @DisplayName("6.7.4 (2) account_transaction_interest 테이블 삭제하기")
  public void deleteAccountTransactionInterestTest() {
    // given
    entityManager.persist(generateAccountTransactionInterestEntity());
    // when
    accountTransactionInterestRepository
        .deleteByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndTransactionYearMonthAndUniqueTransNoIn(
            BANKSALAD_USER_ID, ORGANIZATION_ID, ACCOUNT_NUM_ENCRYPTED, SEQNO, TRANSACTION_YEAR_MONTH,
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
    accountTransactionInterestRepository.save(generateAccountTransactionInterestEntity());
    // then
    List<AccountTransactionInterestEntity> accountTransactionInterestEntities = accountTransactionInterestRepository
        .findAll();
    assertThat(accountTransactionInterestEntities.get(0).getIntNo()).isEqualTo(INT_NO);
  }

  private AccountTransactionEntity generateAccountTransactionEntity() {
    return AccountTransactionEntity.builder()
        .transactionYearMonth(TRANSACTION_YEAR_MONTH)
        .syncedAt(LocalDateTime.now())
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum(ACCOUNT_NUM_ENCRYPTED)
        .seqno(SEQNO)
        .uniqueTransNo(UNIQUE_TRANS_NO)
        .transDtime(TRANS_DTIME)
        .transNo(TRANS_NO)
        .transType(TRANS_TYPE)
        .transAmt(TRANS_AMT)
        .balanceAmt(BALANCE_AMT)
        .principalAmt(PRINCIPAL_AMT)
        .intAmt(INT_AMT) // TODO: 확인하기: API문서에는 N(15), DB에는 DECIMAL(18,3)
        .build();
  }

  private AccountTransactionInterestEntity generateAccountTransactionInterestEntity() {
    return AccountTransactionInterestEntity.builder()
        .transactionYearMonth(TRANSACTION_YEAR_MONTH)
        .syncedAt(LocalDateTime.now())
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum(ACCOUNT_NUM_ENCRYPTED)
        .seqno(SEQNO)
        .uniqueTransNo(UNIQUE_TRANS_NO)
        .intNo(INT_NO)
        .intStartDate(DateUtil.stringToLocalDate("20200101"))
        .intEndDate(DateUtil.stringToLocalDate("20200131"))
        .intRate(BigDecimal.valueOf(3.124))
        .intType("01")
        .build();
  }
}
