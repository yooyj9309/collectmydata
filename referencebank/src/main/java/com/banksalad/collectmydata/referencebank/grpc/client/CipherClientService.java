package com.banksalad.collectmydata.referencebank.grpc.client;

import com.github.banksalad.idl.apis.v1.cipher.CipherProto.GetEncryptedDbTableCipherKeyResponse;

public interface CipherClientService {

  GetEncryptedDbTableCipherKeyResponse getEncryptedDbTableCipherKey(String dbName, String tableName);
}
