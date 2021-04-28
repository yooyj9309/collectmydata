package com.banksalad.collectmydata.finance.common.constant;

public class FinanceConstant {

  public static final String AUTHORIZATION = "Authorization";

  public static final String[] ENTITY_EXCLUDE_FIELD = {"syncedAt", "createdAt", "createdBy", "updatedAt", "updatedBy"};
  public static final String[] INVALID_RESPONSE_CODE = {"40305", "40404"};

  public static final int DEFAULT_SEARCH_YEAR = 5;
  public static final int DEFAULT_PAGING_LIMIT = 500;
  public static final String CURRENCY_KRW = "KRW";

  public static final String REQUESTED_BY_SCHEDULE = "SCHEDULED";
}
