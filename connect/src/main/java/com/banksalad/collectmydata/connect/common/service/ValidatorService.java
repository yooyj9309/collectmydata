package com.banksalad.collectmydata.connect.common.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.banksalad.collectmydata.connect.common.exception.ConnectException;
import com.banksalad.collectmydata.connect.common.enums.ConnectErrorType;
import javax.annotation.PostConstruct;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import java.util.Set;

@Validated
@Service
public class ValidatorService {

  private Validator validator;

  @PostConstruct
  public void init() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  public void validate(Object obj) {
    Set<ConstraintViolation<Object>> violations = validator.validate(obj);
    if (violations == null || violations.size() == 0) {
      return;
    }

    StringBuilder resultMessage = new StringBuilder();
    for (ConstraintViolation<Object> violation : violations) {
      resultMessage.append(violation.getMessage() + "\n");
    }

    // parameter Exception 처리 
    throw new ConnectException(ConnectErrorType.INVALID_PARAMETER, resultMessage.toString());
  }
}
