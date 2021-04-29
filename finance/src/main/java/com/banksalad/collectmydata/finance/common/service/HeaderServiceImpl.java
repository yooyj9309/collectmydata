package com.banksalad.collectmydata.finance.common.service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class HeaderServiceImpl implements HeaderService {

  public static final String CONTENT_TYPE = "contentType";
  public static final String AUTHORIZATION = "Authorization";
  public static final String X_FSI_SVC_DATA_KEY = "X-FSI-SVC-DATA-KEY";

  private final String TESTBED_DATA_HEADER = "N";
  private final String BANKSALAD_DATA_HEADER = "Y";

  @Value("${spring.profiles.active}")
  private String activeProfile;

  public Map<String, String> makeHeader(ExecutionContext executionContext) {
    if ("local".equals(activeProfile) || "staging".equals(activeProfile)) {
      return makeHeaderStaging(executionContext);
    }

    return makeHeaderProduction(executionContext);
  }

  private Map<String, String> makeHeaderProduction(ExecutionContext executionContext) {
    return Map.of(
        CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE,
        AUTHORIZATION, "Bearer " + executionContext.getAccessToken()
    );
  }

  private Map<String, String> makeHeaderStaging(ExecutionContext executionContext) {
    return Map.of(
        CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE,
        AUTHORIZATION, "Bearer " + executionContext.getAccessToken(),
        X_FSI_SVC_DATA_KEY, TESTBED_DATA_HEADER
    );
  }
}
