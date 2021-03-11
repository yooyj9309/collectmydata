package com.banksalad.collectmydata.insu.common.util;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.enums.Industry;
import com.banksalad.collectmydata.common.enums.MydataSector;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.common.util.NumberUtil;
import com.banksalad.collectmydata.insu.common.dto.ListLoanSummariesResponse;
import com.banksalad.collectmydata.insu.common.dto.LoanSummary;
import com.banksalad.collectmydata.insu.loan.dto.GetLoanDetailResponse;
import com.banksalad.collectmydata.insu.loan.dto.LoanDetail;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TestHelper {

  public static final MydataSector SECTOR = MydataSector.FINANCE;
  public static final Industry INDUSTRY = Industry.INSU;
  public static final LocalDateTime SYNCED_AT = LocalDateTime.now();
  public static final long BANKSALAD_USER_ID = 1L;
  public static final String ORGANIZATION_ID = "X-loan";
  public static final String ORGANIZATION_CODE = "020";
  public static final String ORGANIZATION_HOST = "localhost";
  public static final String ACCESS_TOKEN = "accessToken";
  public static final Map<String, String> HEADERS = Map.of("Authorization", ACCESS_TOKEN);
  public static final String CURRENCY_CODE = "KRW";
  public static final String[] ENTITY_IGNORE_FIELD = {"id", "syncedAt", "createdAt", "createdBy", "updatedAt",
      "updatedBy"};
  public static final String ACCOUNT_NUM = "1234567812345678";

  public static ExecutionContext getExecutionContext(int port) {
    return ExecutionContext.builder()
        .organizationHost("http://" + ORGANIZATION_HOST + ":" + port)
        .accessToken(ACCESS_TOKEN)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .executionRequestId(UUID.randomUUID().toString())
        .syncStartedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
        .build();
  }

  public static ExecutionContext getExecutionContext(int port, LocalDateTime now) {
    return ExecutionContext.builder()
        .organizationHost("http://" + ORGANIZATION_HOST + ":" + port)
        .accessToken(ACCESS_TOKEN)
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .executionRequestId(UUID.randomUUID().toString())
        .syncStartedAt(now)
        .build();
  }
  
  public static ListLoanSummariesResponse buildListLoanSummariesResponse() {
    return ListLoanSummariesResponse.builder()
        .rspCode("00000")
        .rspMsg("성공")
        .searchTimestamp(1000L)
        .loan_cnt(1)
        .loanList(List.of(
            LoanSummary.builder()
                .prodName("좋은 보험대출")
                .accountNum(ACCOUNT_NUM)
                .consent(true)
                .prodName("묻지도 따지지도않고 암보험")
                .accountType("3400")
                .accountStatus("01")
                .build()
        )).build();

  }

  public static GetLoanDetailResponse buildGetLoanDetailResponse() {
    return GetLoanDetailResponse.builder()
        .rspCode("00000")
        .rspMsg("성공")
        .searchTimestamp(1000L)
        .loanDetail(LoanDetail.builder()
            .currencyCode(CURRENCY_CODE)
            .balanceAmt(NumberUtil.bigDecimalOf(125.075, 3))
            .loanPrincipal(NumberUtil.bigDecimalOf(10000.000, 3))
            .nextRepayDate("20210325")
            .build()
        )
        .build();
  }
}
