package com.banksalad.collectmydata.connect.common;

import java.time.LocalDateTime;

public class ConnectConstant {

  private ConnectConstant() {
  }

  public static final String SECTOR = "sector";
  public static final String INDUSTRY = "industry";
  public static final long BANKSALAD_USER_ID = 1L;
  public static final String ORGANIZATION_OBJECT_ID = "organizationObjectid";
  public static final String ORGANIZATION_ID = "X-loan";
  public static final String ORGANIZATION_CODE = "10041004";
  public static final String ORGANIZATION_HOST = "localhost";
  public static final String ORGANIZATION_STATUS = "organizationStatus";
  public static final String AUTHORIZATION_CODE = "authorizationCode";
  public static final String ACCESS_TOKEN = "accessToken";
  public static final String REFRESH_TOKEN = "refreshToken";
  public static final String CONSENT_ID = "consentId";
  public static final String TOKEN_TYPE = "Bearer";
  public static final String SCOPE = "card.loan card.bill";

  public static final LocalDateTime ACCESS_TOKEN_EXPIRES_AT = LocalDateTime.now().plusDays(90);
  public static final LocalDateTime REFRESH_TOKEN_EXPIRES_AT = LocalDateTime.now().plusDays(365);
  public static final int ACCESS_TOKEN_EXPIRES_IN = 90 * 3600;
  public static final int REFRESH_TOKEN_EXPIRES_IN = 365 * 3600;
}