package com.banksalad.collectmydata.common.collect.executor;

import com.banksalad.collectmydata.common.collect.api.ApiResponseEntity;

import java.util.Map;

public interface TransferClient {

  ApiResponseEntity execute(String baseUrl, String uri, String httpMethod, Map<String, String> headers,
      String body);
}
