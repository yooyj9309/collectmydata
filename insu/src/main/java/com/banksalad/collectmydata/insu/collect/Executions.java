package com.banksalad.collectmydata.insu.collect;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.insu.car.dto.GetCarInsuranceResponse;
import com.banksalad.collectmydata.insu.car.dto.ListCarInsuranceTransactionsResponse;
import com.banksalad.collectmydata.insu.summary.dto.ListInsuranceSummariesResponse;
import com.banksalad.collectmydata.insu.summary.dto.ListLoanSummariesResponse;
import com.banksalad.collectmydata.insu.insurance.dto.GetInsuranceBasicResponse;
import com.banksalad.collectmydata.insu.insurance.dto.GetInsuranceContractResponse;
import com.banksalad.collectmydata.insu.insurance.dto.GetInsurancePaymentResponse;
import com.banksalad.collectmydata.insu.insurance.dto.ListInsuranceTransactionsResponse;
import com.banksalad.collectmydata.insu.loan.dto.GetLoanBasicResponse;
import com.banksalad.collectmydata.insu.loan.dto.GetLoanDetailResponse;
import com.banksalad.collectmydata.insu.loan.dto.ListLoanTransactionResponse;

public class Executions {

  // 6.5.1 보험 목록 조회
  public static final Execution insurance_get_summaries =
      Execution.create()
          .exchange(Apis.insurance_get_summaries)
          .as(ListInsuranceSummariesResponse.class)
          .build();

  // 6.5.2 보험 기본정보 조회
  public static final Execution insurance_get_basic =
      Execution.create()
          .exchange(Apis.insurance_get_basic)
          .as(GetInsuranceBasicResponse.class)
          .build();

  // 6.5.3 보험 특약정보 조회
  public static final Execution insurance_get_contract =
      Execution.create()
          .exchange(Apis.insurance_get_contract)
          .as(GetInsuranceContractResponse.class)
          .build();

  // 6.5.4 자동차보험 정보 조회
  public static final Execution insurance_get_car =
      Execution.create()
          .exchange(Apis.insurance_get_car)
          .as(GetCarInsuranceResponse.class)
          .build();

  // 6.5.5 보험 납입정보 조회
  public static final Execution insurance_get_payment =
      Execution.create()
          .exchange(Apis.insurance_get_payment)
          .as(GetInsurancePaymentResponse.class)
          .build();

  // 6.5.6 보험 거래내역 조회
  public static final Execution insurance_get_transactions =
      Execution.create()
          .exchange(Apis.insurance_get_transactions)
          .as(ListInsuranceTransactionsResponse.class)
          .build();

  // 6.5.7 자동차보험 거래내역 조회
  public static final Execution insurance_get_car_transactions =
      Execution.create()
          .exchange(Apis.insurance_get_car_transactions)
          .as(ListCarInsuranceTransactionsResponse.class)
          .build();

  // 6.5.8 대출상품 목록 조회
  public static final Execution insurance_get_loan_summaries =
      Execution.create()
          .exchange(Apis.insurance_get_loan_summaries)
          .as(ListLoanSummariesResponse.class)
          .build();

  // 6.5.9 대출상품 기본정보 조회
  public static final Execution insurance_get_loan_basic =
      Execution.create()
          .exchange(Apis.insurance_get_loan_basic)
          .as(GetLoanBasicResponse.class)
          .build();

  // 6.5.10 대출상품 추가정보 조회
  public static final Execution insurance_get_loan_detail =
      Execution.create()
          .exchange(Apis.insurance_get_loan_detail)
          .as(GetLoanDetailResponse.class)
          .build();

  // 6.5.11 대출상품 거래내역 조회
  public static final Execution insurance_get_loan_transactions =
      Execution.create()
          .exchange(Apis.insurance_get_loan_transactions)
          .as(ListLoanTransactionResponse.class)
          .build();
}
