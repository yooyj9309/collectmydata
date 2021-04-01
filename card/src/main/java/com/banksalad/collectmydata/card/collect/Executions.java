package com.banksalad.collectmydata.card.collect;

import com.banksalad.collectmydata.card.card.dto.GetCardBasicResponse;
import com.banksalad.collectmydata.card.card.dto.ListApprovalDomesticResponse;
import com.banksalad.collectmydata.card.card.dto.ListBillBasicResponse;
import com.banksalad.collectmydata.card.card.dto.ListBillDetailResponse;
import com.banksalad.collectmydata.card.card.dto.ListPaymentsResponse;
import com.banksalad.collectmydata.card.card.dto.ListPointsResponse;
import com.banksalad.collectmydata.card.card.dto.ListRevolvingsResponse;
import com.banksalad.collectmydata.card.loan.dto.GetLoanSummaryResponse;
import com.banksalad.collectmydata.card.loan.dto.ListLoanLongTermsResponse;
import com.banksalad.collectmydata.card.loan.dto.ListLoanShortTermsResponse;
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

  // 6.3.3 포인트 정보 조회
  public static final Execution finance_card_point =
      Execution.create()
          .exchange(Apis.finance_card_point)
          .as(ListPointsResponse.class)
          .build();

  // 6.3.4 청구 기본정보 조회
  public static final Execution finance_card_bills =
      Execution.create()
          .exchange(Apis.finance_card_bills)
          .as(ListBillBasicResponse.class)
          .build();

  // 6.3.5 청구 추가정보 조회
  public static final Execution finance_card_bills_detail =
      Execution.create()
          .exchange(Apis.finance_card_bills_detail)
          .as(ListBillDetailResponse.class)
          .build();

  // 6.3.6 결제정보 조회
  public static final Execution finance_card_payment =
      Execution.create()
          .exchange(Apis.finance_card_payment)
          .as(ListPaymentsResponse.class)
          .build();

  // 6.3.7 국내 승인내역 조회
  public static final Execution finance_card_approval_domestic =
      Execution.create()
          .exchange(Apis.finance_card_approval_domestic)
          .as(ListApprovalDomesticResponse.class)
          .build();

  // 6.3.9 대출상품 목록 조회
  public static final Execution finance_loan_summary =
      Execution.create()
          .exchange(Apis.finance_loan_summary)
          .as(GetLoanSummaryResponse.class)
          .build();

  // 6.3.10 리볼빙 정보 조회
  public static final Execution finance_loan_revolvings =
      Execution.create()
          .exchange(Apis.finance_loan_revolvings)
          .as(ListRevolvingsResponse.class)
          .build();

  // 6.3.11 단기대출 정보 조회
  public static final Execution finance_loan_short_terms =
      Execution.create()
          .exchange(Apis.finance_loan_short_terms)
          .as(ListLoanShortTermsResponse.class)
          .build();

  // 6.3.12 장기대출 정보 조회
  public static final Execution finance_loan_long_terms =
      Execution.create()
          .exchange(Apis.finance_loan_long_terms)
          .as(ListLoanLongTermsResponse.class)
          .build();
}
