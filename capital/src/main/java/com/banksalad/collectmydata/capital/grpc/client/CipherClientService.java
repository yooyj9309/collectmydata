package com.banksalad.collectmydata.capital.grpc.client;

import org.springframework.stereotype.Service;

import com.github.banksalad.idl.apis.v1.cipher.CipherGrpc.CipherBlockingStub;
import com.github.banksalad.idl.apis.v1.cipher.CipherProto.GetEncryptedDbTableCipherKeyRequest;
import com.github.banksalad.idl.apis.v1.cipher.CipherProto.GetEncryptedDbTableCipherKeyResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CipherClientService {

  private final CipherBlockingStub cipherBlockingStub;

  public GetEncryptedDbTableCipherKeyResponse getEncryptedDbTableCipherKey(String dbName, String tableName) {
    GetEncryptedDbTableCipherKeyRequest request = GetEncryptedDbTableCipherKeyRequest.newBuilder()
        .setDbName(dbName)
        .setTableName(tableName)
        .build();

    return cipherBlockingStub.getEncryptedDbTableCipherKey(request);
  }
}
