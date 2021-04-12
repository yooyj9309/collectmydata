package com.banksalad.collectmydata.capital.common;

import com.banksalad.collectmydata.capital.account.dto.AccountTransaction;
import com.banksalad.collectmydata.capital.account.dto.AccountTransactionInterest;
import com.banksalad.collectmydata.capital.account.dto.ListAccountTransactionsResponse;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.crypto.HashUtil;
import com.banksalad.collectmydata.common.util.DateUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.banksalad.collectmydata.common.util.NumberUtil.bigDecimalOf;

public class TestHelper {

  public static final LocalDateTime NOW = LocalDateTime.now(DateUtil.UTC_ZONE_ID);
  public static final long BANKSALAD_USER_ID = 1L;
  public static final String ORGANIZATION_ID = "X-loan";
  public static final String ORGANIZATION_CODE = "10041004";
  public static final String ORGANIZATION_HOST = "localhost";
  public static final String ACCOUNT_NUM = "1234567890";
  public static final String ACCOUNT_NUM2 = "5678567856785678";
  public static final String SEQNO1 = "1";
  public static final String SEQNO2 = "2";
  public static final String TRANS_DTIME = "20210121103000";
  public static final String TRANS_NO = "1";
  public static final String TRANS_TYPE = "01";
  public static final BigDecimal TRANS_AMT = bigDecimalOf(100.001, 3);
  public static final BigDecimal BALANCE_AMT = bigDecimalOf(899.999, 3);
  public static final BigDecimal PRINCIPAL_AMT = bigDecimalOf(1000.000, 3);
  public static final BigDecimal INT_AMT = bigDecimalOf(5L, 3);
  public static final int INT_CNT = 1;
  public static final String INT_START_DATE = "20200101";
  public static final String INT_END_DATE = "20200131";
  public static final BigDecimal INT_RATE = bigDecimalOf(3.124, 3);
  public static final String INT_TYPE = "01";
  public static final String ACCOUNT_TYPE = "3100";
  public static final String ACCOUNT_STATUS = "01";
  public static final String PRODUCT_NAME = "X-론 직장인 신용대출";
  public static final String REP_CODE_OK = "00000";
  public static final String REP_MSG_OK = "rsp_msg";
  public static final String[] ENTITY_IGNORE_FIELD = {"id", "syncedAt", "createdAt", "createdBy", "updatedAt",
      "updatedBy"};

  // testTemplate에서 사용되는 부분
  public static final LocalDateTime OLD_SYNCED_AT = DateUtil.toLocalDateTime("20210401", "101000");
  public static final LocalDateTime NEW_SYNCED_AT = LocalDateTime.now(DateUtil.UTC_ZONE_ID);
  public static final long SEARCH_TIMESTAMP_0 = 0L;
  public static final long SEARCH_TIMESTAMP_100 = 100L;
  public static final long SEARCH_TIMESTAMP_200 = 200L;

  public static ExecutionContext getExecutionContext() {
    return ExecutionContext.builder()
        .syncRequestId(UUID.randomUUID().toString())
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .executionRequestId(UUID.randomUUID().toString())
        .organizationCode(ORGANIZATION_CODE)
        .organizationHost("http://" + ORGANIZATION_HOST)
        .accessToken("test")
        .syncStartedAt(NOW)
        .build();
  }

  public static ExecutionContext getExecutionContext(int port) {
    return ExecutionContext.builder()
        .syncRequestId(UUID.randomUUID().toString())
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .executionRequestId(UUID.randomUUID().toString())
        .organizationCode(ORGANIZATION_CODE)
        .organizationHost("http://" + ORGANIZATION_HOST + ":" + port)
        .accessToken("test")
        .syncStartedAt(NOW)
        .build();
  }

  public static ExecutionContext getExecutionContext(int port, LocalDateTime now) {
    return ExecutionContext.builder()
        .syncRequestId(UUID.randomUUID().toString())
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .executionRequestId(UUID.randomUUID().toString())
        .organizationCode(ORGANIZATION_CODE)
        .organizationHost("http://" + ORGANIZATION_HOST + ":" + port)
        .accessToken("test")
        .syncStartedAt(now)
        .build();
  }

  public static AccountTransaction generateAccountTransaction() {
    return AccountTransaction.builder()
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
            AccountTransactionInterest.builder()
                .intStartDate(INT_START_DATE)
                .intEndDate(INT_END_DATE)
                .intRate(INT_RATE)
                .intType(INT_TYPE)
                .build()))
        .build();
  }

  public static AccountTransaction generateAccountTransaction1() {
    return AccountTransaction.builder()
        .transDtime("20210121103000")
        .transNo("trans#2")
        .transType("03")
        .transAmt(bigDecimalOf(1000.3, 3))
        .balanceAmt(bigDecimalOf(18000.7, 3))
        .principalAmt(bigDecimalOf(20000.0, 3))
        .intAmt(bigDecimalOf(100, 3))
        .intCnt(2)
        .intList(List.of(
            AccountTransactionInterest.builder()
                .intStartDate("20201201")
                .intEndDate("20201231")
                .intRate(bigDecimalOf(4.125, 3))
                .intType("02")
                .build(),
            AccountTransactionInterest.builder()
                .intStartDate("20201201")
                .intEndDate("20201231")
                .intRate(bigDecimalOf(3.025, 3))
                .intType("01")
                .build()
        ))
        .build();
  }

  public static String uniqueTransNo1() {
    return HashUtil.hashCat("20210121103000", "trans#2", bigDecimalOf(18000.7, 3).toString());
  }

  public static AccountTransaction generateAccountTransaction2() {
    return AccountTransaction.builder()
        .transDtime("20210121093000")
        .transNo("trans#1")
        .transType("03")
        .transAmt(bigDecimalOf(1000.3, 3))
        .balanceAmt(bigDecimalOf(18000.7, 3))
        .principalAmt(bigDecimalOf(20000.0, 3))
        .intAmt(bigDecimalOf(100, 3))
        .intCnt(1)
        .intList(List.of(
            AccountTransactionInterest.builder()
                .intStartDate("20201201")
                .intEndDate("20201231")
                .intRate(bigDecimalOf(3.025, 3))
                .intType("99")
                .build()
        ))
        .build();
  }

  public static String uniqueTransNo2() {
    return HashUtil.hashCat("20210121093000", "trans#1", bigDecimalOf(18000.7, 3).toString());
  }

  public static AccountTransaction generateAccountTransaction3() {
    return AccountTransaction.builder()
        .transDtime("20210121221000")
        .transNo("trans#3")
        .transType("99")
        .transAmt(bigDecimalOf(0.0, 3))
        .balanceAmt(bigDecimalOf(18000.7, 3))
        .principalAmt(bigDecimalOf(20000.0, 3))
        .intAmt(bigDecimalOf(0, 3))
        .intCnt(0)
        .intList(List.of())
        .build();
  }

  public static String uniqueTransNo3() {
    return HashUtil.hashCat("20210121221000", "trans#3", bigDecimalOf(18000.7, 3).toString());
  }

  public static ListAccountTransactionsResponse respondAccountTransactionResponseWithEmptyPages() {
    return ListAccountTransactionsResponse.builder()
        .rspCode(REP_CODE_OK)
        .rspMsg(REP_MSG_OK)
        .nextPage("0")
        .transCnt(0)
        .transList(null)
        .build();
  }

  public static ListAccountTransactionsResponse respondAccountTransactionResponseWithOnePage() {
    return ListAccountTransactionsResponse.builder()
        .rspCode(REP_CODE_OK)
        .rspMsg(REP_MSG_OK)
        .transCnt(1)
        .transList(List.of(generateAccountTransaction()))
        .build();
  }

  public static ListAccountTransactionsResponse respondAccountTransactionResponseWithTwoPages() {
    return ListAccountTransactionsResponse.builder()
        .rspCode(REP_CODE_OK)
        .rspMsg(REP_MSG_OK)
        .transCnt(3)
        .transList(List.of(
            generateAccountTransaction1(),
            generateAccountTransaction2(),
            generateAccountTransaction3()
        ))
        .build();
  }
}
