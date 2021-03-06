package com.banksalad.collectmydata.oauth.exception;

import com.banksalad.collectmydata.oauth.common.exception.AuthorizationException;
import com.banksalad.collectmydata.oauth.common.exception.OauthException;
import com.banksalad.collectmydata.oauth.common.meters.OauthMeterRegistry;

import org.springframework.ui.Model;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebInputException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class ExceptionControllerAdvice {

  private OauthMeterRegistry oauthMeterRegistry;

  //TODO client와 협의 후, 변경해야할 부분
  private final String DEEP_LINK = "banksalad://webview?url=https%3A%2F%2Fsupport.banksalad.com%2Fhc%2Fko%2Farticles%2F360047387994&aos-need-custom-tab=true&ios-present-config=present&title=%EA%B3%B5%EC%A7%80%EC%82%AC%ED%95%AD";

  public ExceptionControllerAdvice(OauthMeterRegistry oauthMeterRegistry) {
    this.oauthMeterRegistry = oauthMeterRegistry;
  }

  @ExceptionHandler
  public String oauthException(OauthException e, Model model) {
    log.error("CollectRuntimeException message: {}", e.getMessage(), e);

    oauthMeterRegistry.incrementServiceErrorCount(e.getOrganizationId(), e.getTag());
    setViewModel(model);
    return "pages/error";
  }

  @ExceptionHandler
  public String authorizationException(AuthorizationException e, Model model) {
    log.error("CollectRuntimeException message: {}", e.getMessage(), e);

    oauthMeterRegistry.incrementAuthorizationErrorCount(e.getOrganizationId(), e.getAuthorizationResultType());
    setViewModel(model);
    return "pages/error";
  }


  @ExceptionHandler
  public String bindException(ServerWebInputException e, Model model) {
    StringBuilder message = new StringBuilder();
    message.append((e.getReason()));
    log.error("ServerWebInputException occurs : {}  , Exception message : {}", message, e.getMessage(), e);

    setViewModel(model);
    return "pages/error";
  }

  @ExceptionHandler
  public String bindException(WebExchangeBindException e, Model model) {
    StringBuilder message = new StringBuilder();
    for (ObjectError objectError : (e.getBindingResult().getAllErrors())) {
      message.append(objectError.getDefaultMessage());
      message.append(" \n");
    }
    log.error("WebExchangeBindException occurs : {}  , Exception message : {}", message, e.getMessage(), e);

    setViewModel(model);
    return "pages/error";
  }

  private void setViewModel(Model model) {
    // TODO 화면 정책이 있는경우, 해당 value 변경 필요.
    model.addAttribute("isClose", false); // true 인경우 웹에서 팝업 발생.
    model.addAttribute("contents", "일시적인 오류 입니다.");
    model.addAttribute("btnStyle", "visibility: none");
    model.addAttribute("redirectUrl", DEEP_LINK);
    model.addAttribute("btnText", "홈으로 가기");
  }
}
