package com.banksalad.collectmydata.mock.common.api.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.banksalad.collectmydata.mock.common.api.annotation.SearchTimestamp;
import com.banksalad.collectmydata.mock.common.api.context.ApiContextHolder;
import com.banksalad.collectmydata.mock.common.exception.CollectmydataMockRuntimeException;
import com.banksalad.collectmydata.mock.common.exception.code.CollectmydataMockExceptionCode;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
public class SearchTimestampArgumentResolver implements HandlerMethodArgumentResolver {

  @Override
  public boolean supportsParameter(MethodParameter methodParameter) {
    return methodParameter.hasParameterAnnotation(SearchTimestamp.class);
  }

  @Override
  public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws MissingServletRequestParameterException {

    Long searchTimestamp = ApiContextHolder.getRequest().getSearchTimestamp();
    if (searchTimestamp == null || searchTimestamp < 0) {
      throw new MissingServletRequestParameterException("search_timestamp", "long");
    }

    if (0 == searchTimestamp) {
      return LocalDateTime.now().minusYears(5);
    }

    try {
      return LocalDateTime.parse(String.valueOf(searchTimestamp), DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    } catch (DateTimeParseException e) {
      throw new CollectmydataMockRuntimeException(CollectmydataMockExceptionCode.INVALID_PARAMETER_TYPE,
          "invalid search_timestamp format");
    }
  }
}
