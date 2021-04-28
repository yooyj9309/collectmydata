package com.banksalad.collectmydata.mock.common.api.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.banksalad.collectmydata.mock.common.api.annotation.OrgCode;
import com.banksalad.collectmydata.mock.common.api.context.ApiContextHolder;
import org.apache.commons.lang3.StringUtils;

@Component
public class OrgCodeArgumentResolver implements HandlerMethodArgumentResolver {

  @Override
  public boolean supportsParameter(MethodParameter methodParameter) {
    return methodParameter.hasParameterAnnotation(OrgCode.class);
  }

  @Override
  public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws MissingServletRequestParameterException {

    String orgCode = ApiContextHolder.getRequest().getOrgCode();
    if (StringUtils.isEmpty(orgCode)) {
      throw new MissingServletRequestParameterException("org_code", "String");
    }
    return orgCode;
  }
}
