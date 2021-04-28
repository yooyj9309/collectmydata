package com.banksalad.collectmydata.mock.common.api.filter;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import com.banksalad.collectmydata.mock.common.api.code.ApiResponseCode;
import com.banksalad.collectmydata.mock.common.api.context.ApiContextHolder;
import com.banksalad.collectmydata.mock.common.api.context.ApiResponseWrapper;
import com.banksalad.collectmydata.mock.common.api.context.ReusableHttpServletRequestWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import javax.servlet.FilterChain;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
@WebFilter(filterName = "ApiRequestWrappingFilter", urlPatterns = "/*")
@Order(1)
public class ApiRequestWrappingFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws IOException {

    HttpServletRequest reusableRequest = new ReusableHttpServletRequestWrapper(request);
    ContentCachingResponseWrapper contentCachingResponseWrapper = new ContentCachingResponseWrapper(response);

    try {
      ApiContextHolder.validateAndSetRequest(reusableRequest);
      filterChain.doFilter(reusableRequest, contentCachingResponseWrapper);

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      contentCachingResponseWrapper.resetBuffer();
      contentCachingResponseWrapper.setStatus(ApiResponseCode.INTERNAL_SERVER_ERROR.getHttpStatus().value());
      contentCachingResponseWrapper.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
      contentCachingResponseWrapper.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
      contentCachingResponseWrapper.getWriter().print(new ObjectMapper()
          .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
          .writeValueAsString(ApiResponseWrapper.builder()
              .rspCode(ApiResponseCode.INTERNAL_SERVER_ERROR.getResponseCode())
              .rspMsg(e.getMessage())
              .build()));
      contentCachingResponseWrapper.flushBuffer();
    } finally {
      contentCachingResponseWrapper.copyBodyToResponse();
    }
  }
}
