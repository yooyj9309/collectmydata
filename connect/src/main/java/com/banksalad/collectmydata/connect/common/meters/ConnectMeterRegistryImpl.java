package com.banksalad.collectmydata.connect.common.meters;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.connect.common.enums.ConnectErrorType;
import com.banksalad.collectmydata.connect.common.enums.TokenErrorType;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConnectMeterRegistryImpl implements ConnectMeterRegistry {

  // tag
  private static final String TAG_ORGANIZATION_ID = "organizationId";
  private static final String ERROR = "error";
  private static final String ERROR_CODE = "error_code";

  // measurement
  private static final String TOKEN_ERROR_COUNT = "token.error.count";
  private static final String SERVICE_ERROR_COUNT = "service.error.count";

  private final MeterRegistry meterRegistry;

  @Override
  public void incrementTokenErrorCount(String organizationId, TokenErrorType tokenErrorType) {
    Tags tags = Tags.of(TAG_ORGANIZATION_ID, organizationId)
        .and(ERROR, tokenErrorType.getError());

    meterRegistry.counter(TOKEN_ERROR_COUNT, tags).increment();
  }

  @Override
  public void incrementServiceErrorCount(String organizationId, ConnectErrorType connectErrorType) {
    Tags tags = Tags.of(TAG_ORGANIZATION_ID, organizationId)
        .and(ERROR_CODE, connectErrorType.getErrorCode().name());

    meterRegistry.counter(SERVICE_ERROR_COUNT, tags).increment();
  }
}
