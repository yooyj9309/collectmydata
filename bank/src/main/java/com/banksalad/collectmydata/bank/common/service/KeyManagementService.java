package com.banksalad.collectmydata.bank.common.service;

public interface KeyManagementService {

  enum KeyAlias {
    DEPOSIT_ACCOUNT,
    DEPOSIT_ACCOUNT_TRANSACTION,
    INVEST_ACCOUNT,
    INVEST_ACCOUNT_TRANSACTION,
    LOAN_ACCOUNT,
    LOAN_ACCOUNT_TRANSACTION,
    API_LOG
  }

  String getSecret(KeyAlias keyAlias);

  String getIv(KeyAlias keyAlias);
}
