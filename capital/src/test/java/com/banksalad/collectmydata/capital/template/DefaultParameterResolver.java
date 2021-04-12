package com.banksalad.collectmydata.capital.template;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

@RequiredArgsConstructor
public class DefaultParameterResolver<T> implements ParameterResolver {

  final T testCase;

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isInstance(testCase);
  }

  @Override
  public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    return testCase;
  }
}
