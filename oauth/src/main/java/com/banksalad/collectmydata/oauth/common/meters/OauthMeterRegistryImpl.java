package com.banksalad.collectmydata.oauth.common.meters;

import com.banksalad.collectmydata.oauth.common.enums.AuthorizationErrorType;

import org.springframework.stereotype.Service;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;

@Service
public class OauthMeterRegistryImpl implements OauthMeterRegistry {

  // tag id
  public static final String TAG_ORGANIZATION_ID = "organizationId";
  public static final String TAG_AUTHORIZATION_ID = "AUTHORIZATION_ID";
  public static final String COLLECT_EXECUTION_ERROR_COUNT = "collectmydata.authorization.error.count";

  private final MeterRegistry meterRegistry;

  public OauthMeterRegistryImpl(MeterRegistry meterRegistry) {
    this.meterRegistry = meterRegistry;
  }

  @Override
  public void incrementAuthorizationErrorCount(String organizationId, String error) {
    Tags tags = Tags.of(TAG_ORGANIZATION_ID, organizationId)
        .and(TAG_AUTHORIZATION_ID, AuthorizationErrorType.getAuthorizationErrorCode(error).name());

    meterRegistry.counter(COLLECT_EXECUTION_ERROR_COUNT, tags).increment();
  }
}
