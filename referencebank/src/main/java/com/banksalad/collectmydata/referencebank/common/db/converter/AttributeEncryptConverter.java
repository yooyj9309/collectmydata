package com.banksalad.collectmydata.referencebank.common.db.converter;

import com.banksalad.collectmydata.common.crypto.AesGcmEncrypt;
import com.banksalad.collectmydata.referencebank.common.service.KeyManagementService;

import javax.persistence.AttributeConverter;

public class AttributeEncryptConverter implements AttributeConverter<String, String> {

  private final KeyManagementService.KeyAlias keyAlias;
  private final KeyManagementService keyManagementService;

  public AttributeEncryptConverter(KeyManagementService keyManagementService, KeyManagementService.KeyAlias keyAlias) {
    this.keyManagementService = keyManagementService;
    this.keyAlias = keyAlias;
  }

  @Override
  public String convertToDatabaseColumn(String attribute) {
    if (attribute == null || attribute.isEmpty()) {
      return attribute;
    }

    return AesGcmEncrypt.encryptStringBase64(
        keyManagementService.getSecret(keyAlias),
        keyManagementService.getIv(keyAlias),
        attribute);
  }

  @Override
  public String convertToEntityAttribute(String dbData) {
    if (dbData == null || dbData.isEmpty()) {
      return dbData;
    }

    return AesGcmEncrypt.decryptStringBase64(
        keyManagementService.getSecret(keyAlias),
        keyManagementService.getIv(keyAlias),
        dbData);
  }
}
