package com.banksalad.collectmydata.mock.common.api.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.banksalad.collectmydata.common.exception.CollectmydataRuntimeException;
import com.banksalad.collectmydata.mock.common.api.code.ApiResponseCode;
import com.banksalad.collectmydata.mock.common.api.context.ApiResponseWrapper;
import com.banksalad.collectmydata.mock.common.exception.CollectmydataMockRuntimeException;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.Collectors;

//TODO 오류유형별 응답코드 매칭 작업 계속 필요함
@RestControllerAdvice
@Slf4j
public class ApiExceptionHandler {

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ApiResponseWrapper<Object>> handleMissingServletRequestParameterException(
      MissingServletRequestParameterException e) {
    return generateResponseEntity(ApiResponseCode.INVALID_REQUEST_PARAMETER, e.getMessage());
  }


  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponseWrapper<Object>> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    String responseMessage = e.getFieldErrors()
        .stream()
        .map(fieldError -> "[" + fieldError.getField() + "] " + fieldError.getDefaultMessage())
        .collect(Collectors.joining(", "));

    return generateResponseEntity(ApiResponseCode.INVALID_REQUEST_PARAMETER, responseMessage);
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ApiResponseWrapper<Object>> handleHttpRequestMethodNotSupportedException(
      HttpRequestMethodNotSupportedException e) {
    return generateResponseEntity(ApiResponseCode.INVALID_API_CALL, "not supported http method");
  }

  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  public ResponseEntity<ApiResponseWrapper<Object>> handleHttpMediaTypeNotSupportedException(
      HttpMediaTypeNotSupportedException e) {
    return generateResponseEntity(ApiResponseCode.INVALID_API_CALL, "not supported content type");
  }

  @ExceptionHandler(NoHandlerFoundException.class)
  public ResponseEntity<ApiResponseWrapper<Object>> handleNoHandlerFoundException(NoHandlerFoundException e) {
    return generateResponseEntity(ApiResponseCode.NOT_FOUND);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponseWrapper<Object>> handleException(Exception e) {
    log.error(e.getMessage(), e);
    return generateResponseEntity(ApiResponseCode.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(CollectmydataRuntimeException.class)
  public ResponseEntity<ApiResponseWrapper<Object>> handleCollectmydataRuntimeException(
      CollectmydataRuntimeException e) {
    log.error(e.getMessage(), e);
    return generateResponseEntity(ApiResponseCode.INTERNAL_SERVER_ERROR, e.getMessage());
  }

  @ExceptionHandler(CollectmydataMockRuntimeException.class)
  public ResponseEntity<ApiResponseWrapper<Object>> handleCollectmydataMockRuntimeException(
      CollectmydataMockRuntimeException e) {
    switch (e.getCollectmydataMockExceptionCode()) {
      case NOT_FOUND_ASSETS:
        return generateResponseEntity(ApiResponseCode.NOT_FOUND_ASSETS, e.getMessage());
      case INVALID_PARAMETER_TYPE:
        return generateResponseEntity(ApiResponseCode.INVALID_REQUEST_PARAMETER, e.getMessage());
      default:
        return generateResponseEntity(ApiResponseCode.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  private ResponseEntity<ApiResponseWrapper<Object>> generateResponseEntity(ApiResponseCode apiResponseCode) {
    return generateResponseEntity(apiResponseCode, apiResponseCode.getDefaultResponseMessage());
  }

  private ResponseEntity<ApiResponseWrapper<Object>> generateResponseEntity(ApiResponseCode apiResponseCode,
      String responseMessage) {
    return ResponseEntity.status(apiResponseCode.getHttpStatus())
        .body(ApiResponseWrapper.builder()
            .rspCode(apiResponseCode.getResponseCode())
            .rspMsg(responseMessage)
            .build());
  }
}
