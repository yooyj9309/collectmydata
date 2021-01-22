package com.banksalad.collectmydata.connect.token.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.banksalad.collectmydata.connect.token.validator.GetAccessTokenRequestValidator;
import com.banksalad.collectmydata.connect.token.validator.IssueTokenRequestValidator;
import com.banksalad.collectmydata.connect.token.validator.RefreshTokenRequestValidator;
import com.banksalad.collectmydata.connect.token.validator.RevokeAllTokensRequestValidator;
import com.banksalad.collectmydata.connect.token.validator.RevokeTokenRequestValidator;
import javax.validation.Valid;

@Validated
@Service
public class ValidatorService {

  public void validate(@Valid IssueTokenRequestValidator validator) {
  }

  public void validate(@Valid GetAccessTokenRequestValidator validator) {
  }

  public void validate(@Valid RefreshTokenRequestValidator validator) {
  }

  public void validate(@Valid RevokeTokenRequestValidator validator) {
  }

  public void validate(@Valid RevokeAllTokensRequestValidator validator) {
  }
}


