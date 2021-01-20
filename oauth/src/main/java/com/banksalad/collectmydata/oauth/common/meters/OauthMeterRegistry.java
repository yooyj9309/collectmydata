package com.banksalad.collectmydata.oauth.common.meters;

public interface OauthMeterRegistry {

  public void incrementAuthorizationErrorCount(String organizationId, String error);

}
