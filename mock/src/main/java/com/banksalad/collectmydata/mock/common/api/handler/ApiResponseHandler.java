package com.banksalad.collectmydata.mock.common.api.handler;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.banksalad.collectmydata.mock.common.api.code.ApiResponseCode;
import com.banksalad.collectmydata.mock.common.api.context.ApiResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@RestControllerAdvice
@Slf4j
public class ApiResponseHandler implements ResponseBodyAdvice<Object> {

  @Override
  public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
    return true;
  }

  @Override
  @SuppressWarnings({"unchecked"})
  public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
      Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
      ServerHttpResponse response) {

    ApiResponseWrapper<Object> apiResponseWrapper = ApiResponseWrapper.builder()
        .rspCode(ApiResponseCode.SUCCESS.getResponseCode())
        .rspMsg(ApiResponseCode.SUCCESS.getDefaultResponseMessage())
        .data(body)
        .build();

    if (body instanceof ApiResponseWrapper) {
      apiResponseWrapper = (ApiResponseWrapper<Object>) body;
      if (StringUtils.isEmpty(apiResponseWrapper.getRspCode())) {
        log.error("There is no response code, so it is replaced with UNKNOWN_ERROR.");
        apiResponseWrapper = ApiResponseWrapper.builder()
            .rspCode(ApiResponseCode.UNKNOWN_ERROR.getResponseCode())
            .rspMsg(ApiResponseCode.UNKNOWN_ERROR.getDefaultResponseMessage())
            .build();
      }
    }

    return apiResponseWrapper.setResponseSearchTimestamp();
  }
}
