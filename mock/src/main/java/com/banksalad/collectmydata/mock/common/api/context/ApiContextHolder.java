package com.banksalad.collectmydata.mock.common.api.context;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.banksalad.collectmydata.common.exception.CollectmydataRuntimeException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.IOException;

public class ApiContextHolder {

  private static final String ATTRIBUTE_NAME_REQUEST = "api.request.wrapper";

  public static void validateAndSetRequest(HttpServletRequest request)
      throws IOException {

    String authorizationHeaderValue = request.getHeader("Authorization");
    if (StringUtils.isEmpty(authorizationHeaderValue) || !authorizationHeaderValue.startsWith("Bearer")) {
      throw new CollectmydataRuntimeException("missing authorization header");
    }

    Long banksaladUserId = NumberUtils.toLong(
        StringUtils.trim(StringUtils.removeStart(authorizationHeaderValue, "Bearer")));

    if ("GET".equalsIgnoreCase(request.getMethod())) {
      setRequest(ApiRequestWrapper.builder()
          .banksaladUserId(banksaladUserId)
          .orgCode(StringUtils.defaultString(request.getParameter("org_code")))
          .searchTimestamp(NumberUtils
              .toLong(StringUtils.defaultString(request.getParameter("search_timestamp")), -1))
          .build());
    } else {
      String requestBody = IOUtils.toString(request.getReader());
      setRequest(new ObjectMapper()
          .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
          .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
          .readValue(StringUtils.defaultIfBlank(requestBody, "{}"), ApiRequestWrapper.class)
          .setBanksaladUserId(banksaladUserId));
    }
  }

  private static void setRequest(ApiRequestWrapper apiRequestWrapper) {
    RequestContextHolder.getRequestAttributes()
        .setAttribute(ATTRIBUTE_NAME_REQUEST, apiRequestWrapper, RequestAttributes.SCOPE_REQUEST);
  }

  public static ApiRequestWrapper getRequest() {
    return (ApiRequestWrapper) RequestContextHolder.getRequestAttributes()
        .getAttribute(ATTRIBUTE_NAME_REQUEST, RequestAttributes.SCOPE_REQUEST);
  }
}
