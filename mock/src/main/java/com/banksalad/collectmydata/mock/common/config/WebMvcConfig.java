package com.banksalad.collectmydata.mock.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.banksalad.collectmydata.mock.common.api.resolver.BanksaladUserIdArgumentResolver;
import com.banksalad.collectmydata.mock.common.api.resolver.OrgCodeArgumentResolver;
import com.banksalad.collectmydata.mock.common.api.resolver.SearchTimestampArgumentResolver;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

  private final BanksaladUserIdArgumentResolver banksaladUserIdArgumentResolver;
  private final OrgCodeArgumentResolver orgCodeArgumentResolver;
  private final SearchTimestampArgumentResolver searchTimestampArgumentResolver;

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
    argumentResolvers.add(banksaladUserIdArgumentResolver);
    argumentResolvers.add(orgCodeArgumentResolver);
    argumentResolvers.add(searchTimestampArgumentResolver);
  }
}
