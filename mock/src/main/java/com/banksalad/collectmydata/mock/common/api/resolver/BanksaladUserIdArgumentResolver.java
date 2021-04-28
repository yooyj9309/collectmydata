package com.banksalad.collectmydata.mock.common.api.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.banksalad.collectmydata.mock.common.api.annotation.BanksaladUserId;
import com.banksalad.collectmydata.mock.common.api.context.ApiContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class BanksaladUserIdArgumentResolver implements HandlerMethodArgumentResolver {

  @Override
  public boolean supportsParameter(MethodParameter methodParameter) {
    return methodParameter.hasParameterAnnotation(BanksaladUserId.class);
  }

  @Override
  public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
    return ApiContextHolder.getRequest().getBanksaladUserId();
  }
}
