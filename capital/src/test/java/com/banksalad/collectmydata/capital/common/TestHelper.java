package com.banksalad.collectmydata.capital.common;

import com.banksalad.collectmydata.capital.common.db.entity.AccountTransactionEntity;
import com.banksalad.collectmydata.capital.common.db.entity.AccountTransactionInterestEntity;
import com.banksalad.collectmydata.capital.common.db.entity.mapper.AccountTransactionInterestMapper;
import com.banksalad.collectmydata.capital.common.db.entity.mapper.AccountTransactionMapper;
import com.banksalad.collectmydata.capital.loan.dto.LoanAccountTransaction;
import com.banksalad.collectmydata.capital.loan.dto.LoanAccountTransactionInterest;
import com.banksalad.collectmydata.capital.loan.dto.LoanAccountTransactionResponse;
import com.banksalad.collectmydata.common.crypto.HashUtil;
import com.banksalad.collectmydata.common.enums.Industry;
import com.banksalad.collectmydata.common.enums.MydataSector;

import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static com.banksalad.collectmydata.common.util.NumberUtil.bigDecimalOf;

public class TestHelper {

  public static final int AMOUNT_SCALE = 3;
  public static final MydataSector SECTOR = MydataSector.FINANCE;
  public static final Industry INDUSTRY = Industry.CAPITAL;
  public static final LocalDateTime SYNCED_AT = LocalDateTime.now();
  public static final long BANKSALAD_USER_ID = 1L;
  public static final String ORGANIZATION_ID = "X-loan";
  public static final String ORGANIZATION_CODE = "10041004";
  public static final String ORGANIZATION_HOST = "localhost";
  public static final String ACCOUNT_NUM = "1234567890";
  public static final String SEQNO1 = "1";
  public static final String SEQNO2 = "2";
  public static final String TRANS_DTIME = "20210121103000";
  public static final int TRANSACTION_YEAR_MONTH = Integer.parseInt(TRANS_DTIME.substring(0, 6));
  public static final String TRANS_NO = "1";
  public static final String TRANS_TYPE = "01";
  public static final BigDecimal TRANS_AMT = bigDecimalOf(100.001);
  public static final BigDecimal BALANCE_AMT = bigDecimalOf(899.999);
  public static final BigDecimal PRINCIPAL_AMT = bigDecimalOf(1000.000);
  public static final BigDecimal INT_AMT = bigDecimalOf(5L);
  public static final int INT_CNT = 1;
  public static final int INT_NO = 1;
  public static final String UNIQUE_TRANS_NO = HashUtil
      .hashCat(Arrays.asList(TRANS_DTIME, TRANS_NO, BALANCE_AMT.toString()));
  public static final String INT_START_DATE = "20200101";
  public static final String INT_END_DATE = "20200131";
  public static final BigDecimal INT_RATE = bigDecimalOf(3.124);
  public static final String INT_TYPE = "01";
  public static final String ACCESS_TOKEN = "abc.def.ghi";
  public static final String ACCOUNT_TYPE = "3100";
  public static final String ACCOUNT_STATUS = "01";
  public static final String PRODUCT_NAME = "X-론 직장인 신용대출";
  public static final int MAX_LIMIT = 2;
  public static final String REP_CODE_OK = "00000";
  public static final String REP_MSG_OK = "rsp_msg";

  private static final AccountTransactionMapper accountTransactionMapper = Mappers
      .getMapper(AccountTransactionMapper.class);
  private static final AccountTransactionInterestMapper accountTransactionInterestMapper = Mappers
      .getMapper(AccountTransactionInterestMapper.class);

  public static LoanAccountTransaction generateLoanAccountTransaction() {
    return LoanAccountTransaction.builder()
//        .banksaladUserId(BANKSALAD_USER_ID)
//        .organizationId(ORGANIZATION_ID)
//        .accountNum(ACCOUNT_NUM)
//        .seqno(SEQNO1)
//        .syncedAt(SYNCED_AT)
        .transDtime(TRANS_DTIME)
        .transNo(TRANS_NO)
        .transType(TRANS_TYPE)
        .transAmt(TRANS_AMT)
        .balanceAmt(BALANCE_AMT)
        .principalAmt(PRINCIPAL_AMT)
        .intAmt(INT_AMT)
        .intCnt(INT_CNT)
        .intList(List.of(
            LoanAccountTransactionInterest.builder()
                .intStartDate(INT_START_DATE)
                .intEndDate(INT_END_DATE)
                .intRate(INT_RATE)
                .intType(INT_TYPE)
                .build()))
        .build();
  }

  public static LoanAccountTransaction generateLoanAccountTransaction1() {
    return LoanAccountTransaction.builder()
        .transDtime("20210121103000")
        .transNo("trans#2")
        .transType("03")
        .transAmt(bigDecimalOf(1000.3))
        .balanceAmt(bigDecimalOf(18000.7))
        .principalAmt(bigDecimalOf(20000.0))
        .intAmt(bigDecimalOf(100))
        .intCnt(2)
        .intList(List.of(
            LoanAccountTransactionInterest.builder()
                .intStartDate("20201201")
                .intEndDate("20201231")
                .intRate(bigDecimalOf(4.125))
                .intType("02")
                .build(),
            LoanAccountTransactionInterest.builder()
                .intStartDate("20201201")
                .intEndDate("20201231")
                .intRate(bigDecimalOf(3.025))
                .intType("01")
                .build()
        ))
        .build();
  }

  public static String uniqueTransNo1() {
    return HashUtil.hashCat(Arrays.asList("20210121103000", "trans#2", bigDecimalOf(18000.7).toString()));
  }

  public static LoanAccountTransaction generateLoanAccountTransaction2() {
    return LoanAccountTransaction.builder()
        .transDtime("20210121093000")
        .transNo("trans#1")
        .transType("03")
        .transAmt(bigDecimalOf(1000.3))
        .balanceAmt(bigDecimalOf(18000.7))
        .principalAmt(bigDecimalOf(20000.0))
        .intAmt(bigDecimalOf(100))
        .intCnt(1)
        .intList(List.of(
            LoanAccountTransactionInterest.builder()
                .intStartDate("20201201")
                .intEndDate("20201231")
                .intRate(bigDecimalOf(3.025))
                .intType("99")
                .build()
        ))
        .build();
  }

  public static String uniqueTransNo2() {
    return HashUtil.hashCat(Arrays.asList("20210121093000", "trans#1", bigDecimalOf(18000.7).toString()));
  }

  public static LoanAccountTransaction generateLoanAccountTransaction3() {
    return LoanAccountTransaction.builder()
        .transDtime("20210121221000")
        .transNo("trans#3")
        .transType("99")
        .transAmt(bigDecimalOf(0.0))
        .balanceAmt(bigDecimalOf(18000.7))
        .principalAmt(bigDecimalOf(20000.0))
        .intAmt(bigDecimalOf(0))
        .intCnt(0)
        .intList(List.of())
        .build();
  }

  public static String uniqueTransNo3() {
    return HashUtil.hashCat(Arrays.asList("20210121221000", "trans#3", bigDecimalOf(18000.7).toString()));
  }

  public static LoanAccountTransactionResponse respondLoanAccountTransactionResponseWithEmptyPages() {
    return LoanAccountTransactionResponse.builder()
        .rspCode(REP_CODE_OK)
        .rspMsg(REP_MSG_OK)
        .nextPage("0")
        .transCnt(0)
        .transList(null)
        .build();
  }

  public static LoanAccountTransactionResponse respondLoanAccountTransactionResponseWithOnePage() {
    return LoanAccountTransactionResponse.builder()
        .rspCode(REP_CODE_OK)
        .rspMsg(REP_MSG_OK)
        .transCnt(1)
        .transList(List.of(generateLoanAccountTransaction()))
        .build();
  }

  public static LoanAccountTransactionResponse respondLoanAccountTransactionResponseWithTwoPages() {
    return LoanAccountTransactionResponse.builder()
        .rspCode(REP_CODE_OK)
        .rspMsg(REP_MSG_OK)
        .transCnt(3)
        .transList(List.of(
            generateLoanAccountTransaction1(),
            generateLoanAccountTransaction2(),
            generateLoanAccountTransaction3()
        ))
        .build();
  }

  public static AccountTransactionEntity createAccountTransactionEntity() {
    AccountTransactionEntity accountTransactionEntity = AccountTransactionEntity.builder()
        .transactionYearMonth(TRANSACTION_YEAR_MONTH)
        .syncedAt(SYNCED_AT)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .accountNum(ACCOUNT_NUM)
        .seqno(SEQNO1)
        .uniqueTransNo(UNIQUE_TRANS_NO)
        .build();
    accountTransactionMapper.updateEntityFromDto(generateLoanAccountTransaction(), accountTransactionEntity);
    return accountTransactionEntity;
  }

  public static AccountTransactionInterestEntity createAccountTransactionInterestEntity() {
    AccountTransactionInterestEntity accountTransactionInterestEntity = AccountTransactionInterestEntity.builder()
        .build();
    accountTransactionInterestMapper
        .updateEntityFromDto(createAccountTransactionEntity(), 1, generateLoanAccountTransaction().getIntList().get(0),
            accountTransactionInterestEntity);
    return accountTransactionInterestEntity;
  }
}