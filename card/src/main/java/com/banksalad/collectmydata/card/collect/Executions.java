package com.banksalad.collectmydata.card.collect;

import com.banksalad.collectmydata.card.card.dto.GetCardBasicResponse;
import com.banksalad.collectmydata.card.loan.dto.GetLoanSummaryResponse;
import com.banksalad.collectmydata.card.card.dto.ListBillBasicResponse;
import com.banksalad.collectmydata.card.card.dto.ListApprovalDomesticResponse;
import com.banksalad.collectmydata.card.summary.dto.ListCardSummariesResponse;
import com.banksalad.collectmydata.common.collect.execution.Execution;

public class Executions {

  // 6.3.1 카드 목록 조회
  public static final Execution finance_card_summaries =
      Execution.create()
          .exchange(Apis.finance_card_summaries)
          .as(ListCardSummariesResponse.class)
          .build();

  // 6.3.2 카드 기본정보 조회
  public static final Execution finance_card_basic =
      Execution.create()
          .exchange(Apis.finance_card_basic)
          .as(GetCardBasicResponse.class)
          .build();

  // 6.3.4 청구 기본정보 조회
  public static final Execution finance_card_bills =
      Execution.create()
          .exchange(Apis.finance_card_bills)
          .as(ListBillBasicResponse.class)
          .build();

  // 6.3.7 국내 승인내역 조회
  public static final Execution finance_card_approval_domestic =
      Execution.create()
          .exchange(Apis.finance_card_approval_domestic)
          .as(ListApprovalDomesticResponse.class)
          .build();

  // 6.3.9 대출상품 목록 조회
  public static final Execution finance_loan_summaries =
      Execution.create()
          .exchange(Apis.finance_loan_summaries)
          .as(GetLoanSummaryResponse.class)
          .build();
}
