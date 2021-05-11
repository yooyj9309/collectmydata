package com.banksalad.collectmydata.finance.common.constant;

public class FinanceConstant {

  public static final String AUTHORIZATION = "Authorization";

  // FIXME
  /** ENTITY_EXCLUDE_FIELD_FOR_TESTBED 생성이유는 6.3.7 ~ 8 금보원 테스트베드 호출 시
   *  같은 승인번호(approvedNum)에서 다른 승인시각(approvedDtime)과 다른 승인금액(approvedAmt)이 응답되는 이상한케이스에 대비.
   *  없을 경우 duplicate error가 발생. DB 저장을 위해 추가했고 추후 삭제 예정.
   *  @author hyunjun
   */
  public static final String[] ENTITY_EXCLUDE_FIELD_FOR_TESTBED = {"id", "consentId", "approvedDtime", "approvedAmt", "syncRequestId", "syncedAt", "createdAt", "createdBy", "updatedAt", "updatedBy"};
  public static final String[] ENTITY_EXCLUDE_FIELD = {"syncedAt", "createdAt", "createdBy", "updatedAt", "updatedBy", "consentId", "syncRequestId"};
  public static final String[] INVALID_RESPONSE_CODE = {"40305", "40404"};

  public static final int DEFAULT_SEARCH_YEAR = 5;
  public static final int BILL_DEFAUTL_SEARCH_MONTH = 13;
  public static final int DEFAULT_PAGING_LIMIT = 500;
  public static final String CURRENCY_KRW = "KRW";

  public static final String REQUESTED_BY_SCHEDULE = "SCHEDULED";
}
