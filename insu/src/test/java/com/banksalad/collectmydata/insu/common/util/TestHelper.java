package com.banksalad.collectmydata.insu.common.util;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.enums.Industry;
import com.banksalad.collectmydata.common.enums.MydataSector;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.common.util.NumberUtil;
import com.banksalad.collectmydata.insu.loan.dto.GetLoanDetailResponse;
import com.banksalad.collectmydata.insu.loan.dto.LoanDetail;
import com.banksalad.collectmydata.insu.summary.dto.ListLoanSummariesResponse;
import com.banksalad.collectmydata.insu.summary.dto.LoanSummary;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Deprecated
public class TestHelper {

  public static final MydataSector SECTOR = MydataSector.FINANCE;
  public static final Industry INDUSTRY = Industry.INSU;
  public static final LocalDateTime NOW = LocalDateTime.now();
  public static final LocalDateTime SYNCED_AT = DateUtil.toLocalDateTime("20210401","101000");
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
        .syncRequestId(UUID.randomUUID().toString())
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .organizationCode(ORGANIZATION_CODE)
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
        .organizationCode(ORGANIZATION_CODE)
        .executionRequestId(UUID.randomUUID().toString())
        .organizationCode(ORGANIZATION_CODE)
        .organizationHost("http://" + ORGANIZATION_HOST + ":" + port)
        .accessToken("test")
        .syncStartedAt(now)
        .build();
  }

  public static ListLoanSummariesResponse buildListLoanSummariesResponse() {
    return ListLoanSummariesResponse.builder()
        .rspCode("00000")
        .rspMsg("??????")
        .searchTimestamp(1000L)
        .loanCnt(1)
        .loanList(List.of(
            LoanSummary.builder()
                .prodName("?????? ????????????")
                .accountNum(ACCOUNT_NUM)
                .consent(true)
                .prodName("????????? ?????????????????? ?????????")
                .accountType("3400")
                .accountStatus("01")
                .build()
        )).build();

  }

  public static GetLoanDetailResponse buildGetLoanDetailResponse() {
    return GetLoanDetailResponse.builder()
        .rspCode("00000")
        .rspMsg("??????")
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
