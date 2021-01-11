package com.banksalad.collectmydata.bank.common.service.converter;

import com.banksalad.collectmydata.bank.common.service.KeyManagementService;

//@Service
public class ApiLogEncryptConverter extends AttributeEncryptConverter {

  public ApiLogEncryptConverter(KeyManagementService keyManagementService) {
    super(keyManagementService, KeyManagementService.KeyAlias.API_LOG);
  }
}
