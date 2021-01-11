package com.banksalad.collectmydata.bank.grpc.client;

import org.springframework.context.annotation.Profile;

import com.github.banksalad.idl.apis.v1.cipher.CipherGrpc.CipherBlockingStub;
import com.github.banksalad.idl.apis.v1.cipher.CipherProto.GetEncryptedDbTableCipherKeyRequest;
import com.github.banksalad.idl.apis.v1.cipher.CipherProto.GetEncryptedDbTableCipherKeyResponse;

//@Service
@Profile({"production", "development", "local"})
public class CipherClientServiceImpl implements CipherClientService {

  private final CipherBlockingStub cipherBlockingStub;

  public CipherClientServiceImpl(CipherBlockingStub cipherBlockingStub) {
    this.cipherBlockingStub = cipherBlockingStub;
  }

  public GetEncryptedDbTableCipherKeyResponse getEncryptedDbTableCipherKey(String dbName, String tableName) {
    GetEncryptedDbTableCipherKeyRequest request = GetEncryptedDbTableCipherKeyRequest.newBuilder()
        .setDbName(dbName)
        .setTableName(tableName)
        .build();

    return cipherBlockingStub.getEncryptedDbTableCipherKey(request);
  }
}
