package com.banksalad.collectmydata.oauth.common.meters;

import com.banksalad.collectmydata.oauth.common.enums.AuthorizationResultType;

import org.springframework.stereotype.Service;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;

@Service
public class OauthMeterRegistryImpl implements OauthMeterRegistry {

  // tag id
  public static final String TAG_ORGANIZATION_ID = "organizationId";
  public static final String TAG_OS_NAME = "os";
  public static final String TAG_AUTHORIZATION_ID = "authorizationId";
  public static final String TAG_STEP = "step";
  public static final String TAG_EXCEPTION_ID = "exceptionId";

  //tag name
  public static final String AUTHORIZATION_ERROR_COUNT = "authorization.error.count";
  public static final String SERVICE_ERROR_COUNT = "service.error.count";
  public static final String USER_AUTH_STEP_COUNT = "user.authentication.progress.count";


  public static final String OAUTH_INIT = "oauth-init";
  public static final String OAUTH_COMPLETE = "oauth-complete";

  private final MeterRegistry meterRegistry;

  public OauthMeterRegistryImpl(MeterRegistry meterRegistry) {
    this.meterRegistry = meterRegistry;
  }

  @Override
  public void incrementAuthorizationErrorCount(String organizationId, AuthorizationResultType authorizationResultType) {
    Tags tags = Tags.of(TAG_ORGANIZATION_ID, organizationId)
        .and(TAG_AUTHORIZATION_ID, authorizationResultType.name());

    meterRegistry.counter(AUTHORIZATION_ERROR_COUNT, tags).increment();
  }

  @Override
  public void incrementServiceErrorCount(String organizationId, String tag) {
    Tags tags = Tags.of(TAG_ORGANIZATION_ID, organizationId).and(TAG_EXCEPTION_ID, tag);
    meterRegistry.counter(SERVICE_ERROR_COUNT, tags).increment();
  }

  @Override
  public void incrementUserAuthStepCount(String organizationId, String os, String step) {
    Tags tags = Tags.of(TAG_ORGANIZATION_ID, organizationId).and(TAG_OS_NAME, os)
        .and(TAG_STEP, step);
    meterRegistry.counter(USER_AUTH_STEP_COUNT, tags).increment();
  }
}
