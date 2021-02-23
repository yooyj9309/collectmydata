package com.banksalad.collectmydata.referencebank.common.db.converter;

import com.banksalad.collectmydata.referencebank.common.service.KeyManagementService;

//@Service
public class ApiLogEncryptConverter extends AttributeEncryptConverter {

  public ApiLogEncryptConverter(KeyManagementService keyManagementService) {
    super(keyManagementService, KeyManagementService.KeyAlias.API_LOG);
  }
}
