package com.banksalad.collectmydata.oauth.common.meters;

import com.banksalad.collectmydata.oauth.common.enums.AuthorizationResultType;

public interface OauthMeterRegistry {

  public void incrementAuthorizationErrorCount(String organizationId, AuthorizationResultType authorizationResultType);

  public void incrementServiceErrorCount(String organizationId, String tag);

  public void incrementUserAuthStepCount(String organizationId, String os, String step);
}
