package com.banksalad.collectmydata.bank.common.service;

import com.banksalad.collectmydata.bank.grpc.client.CipherClientService;
import com.banksalad.collectmydata.common.crypto.Base64Util;
import com.banksalad.collectmydata.common.exception.CollectRuntimeException;

import org.springframework.beans.factory.annotation.Value;

import com.github.banksalad.idl.apis.v1.cipher.CipherProto.GetEncryptedDbTableCipherKeyResponse;
import javax.annotation.PostConstruct;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.DecryptRequest;
import software.amazon.awssdk.services.kms.model.DecryptResponse;

import java.util.Base64;

//@Component
public class KeyManagementServiceImpl implements KeyManagementService {

  public enum TableNameForCipher {
    invest_account, invest_account_transaction, loan_account, loan_account_transaction, deposit_account, deposit_account_transaction, api_log
  }

  public final static String COLLECTMYDATA_BANK_DB_NAME = "collectmydata-bank";

  @Value("${aws.iam.collectmydata.bank.access-key}")
  private String awsAccessKey;

  @Value("${aws.iam.collectmydata.bank.access-token}")
  private String awsAccessToken;

  @Value("${aws.region}")
  private String awsRegion;

  // IV
  private String investAccountIv;
  private String investAccountTransactionIv;
  private String loanAccountIv;
  private String loanAccountTransactionIv;
  private String depositAccountIv;
  private String depositAccountTransactionIv;
  private String apiLogIv;

  // Secret
  private String investAccountSecret;
  private String investAccountTransactionSecret;
  private String loanAccountSecret;
  private String loanAccountTransactionSecret;
  private String depositAccountSecret;
  private String depositAccountTransactionSecret;
  private String apiLogSecret;

  private final CipherClientService cipherClientService;
  private KmsClient kmsClient;

  public KeyManagementServiceImpl(CipherClientService cipherClientService) {
    this.cipherClientService = cipherClientService;
  }

  @PostConstruct
  private void init() {
    kmsClient = KmsClient.builder()
        .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(awsAccessKey, awsAccessToken)))
        .region(Region.of(awsRegion))
        .build();

    GetEncryptedDbTableCipherKeyResponse encryptedInvestTableCipherKeyResponse = cipherClientService
        .getEncryptedDbTableCipherKey(
            COLLECTMYDATA_BANK_DB_NAME, TableNameForCipher.invest_account.name());

    investAccountSecret = decryptSecret(encryptedInvestTableCipherKeyResponse.getCipherKey());
    investAccountIv = encryptedInvestTableCipherKeyResponse.getNonce();

    GetEncryptedDbTableCipherKeyResponse encryptedInvestAccountTransactionTableCipherKeyResponse = cipherClientService
        .getEncryptedDbTableCipherKey(
            COLLECTMYDATA_BANK_DB_NAME, TableNameForCipher.invest_account_transaction.name());

    investAccountTransactionSecret = decryptSecret(
        encryptedInvestAccountTransactionTableCipherKeyResponse.getCipherKey());
    investAccountTransactionIv = encryptedInvestAccountTransactionTableCipherKeyResponse.getNonce();

    GetEncryptedDbTableCipherKeyResponse encryptedLoanAccountTableCipherKey = cipherClientService
        .getEncryptedDbTableCipherKey(COLLECTMYDATA_BANK_DB_NAME, TableNameForCipher.loan_account.name());

    loanAccountSecret = decryptSecret(encryptedLoanAccountTableCipherKey.getCipherKey());
    loanAccountIv = encryptedLoanAccountTableCipherKey.getNonce();

    GetEncryptedDbTableCipherKeyResponse encryptedLoanAccountTransactionTableCipherKey = cipherClientService
        .getEncryptedDbTableCipherKey(COLLECTMYDATA_BANK_DB_NAME, TableNameForCipher.loan_account_transaction.name());

    loanAccountTransactionSecret = decryptSecret(encryptedLoanAccountTransactionTableCipherKey.getCipherKey());
    loanAccountTransactionIv = encryptedLoanAccountTransactionTableCipherKey.getNonce();

    GetEncryptedDbTableCipherKeyResponse encryptedDepositAccountTableCipherKey = cipherClientService
        .getEncryptedDbTableCipherKey(COLLECTMYDATA_BANK_DB_NAME, TableNameForCipher.deposit_account.name());

    depositAccountSecret = decryptSecret(encryptedDepositAccountTableCipherKey.getCipherKey());
    depositAccountIv = encryptedDepositAccountTableCipherKey.getNonce();

    GetEncryptedDbTableCipherKeyResponse encryptedDepositAccountTransactionTableCipherKey = cipherClientService
        .getEncryptedDbTableCipherKey(
            COLLECTMYDATA_BANK_DB_NAME, TableNameForCipher.deposit_account_transaction.name());

    depositAccountTransactionSecret = decryptSecret(encryptedDepositAccountTransactionTableCipherKey.getCipherKey());
    depositAccountTransactionIv = encryptedDepositAccountTransactionTableCipherKey.getNonce();

    GetEncryptedDbTableCipherKeyResponse encryptedApiLogTableCipherKey = cipherClientService
        .getEncryptedDbTableCipherKey(COLLECTMYDATA_BANK_DB_NAME, TableNameForCipher.api_log.name());

    apiLogSecret = decryptSecret(encryptedApiLogTableCipherKey.getCipherKey());
    apiLogIv = encryptedApiLogTableCipherKey.getNonce();
  }

  private String decryptSecret(String encrypted) {
    SdkBytes sdkBytes = SdkBytes.fromByteArray(Base64.getDecoder().decode(encrypted));

    DecryptRequest decryptRequest = DecryptRequest.builder()
        .ciphertextBlob(sdkBytes)
        .build();

    DecryptResponse decryptResponse = kmsClient.decrypt(decryptRequest);

    return Base64Util.encode(decryptResponse.plaintext().asByteArray());
  }

  @Override
  public String getSecret(KeyAlias keyAlias) {
    switch (keyAlias) {
      case INVEST_ACCOUNT:
        return investAccountSecret;

      case INVEST_ACCOUNT_TRANSACTION:
        return investAccountTransactionSecret;

      case LOAN_ACCOUNT:
        return loanAccountSecret;

      case LOAN_ACCOUNT_TRANSACTION:
        return loanAccountTransactionSecret;

      case DEPOSIT_ACCOUNT:
        return depositAccountSecret;

      case DEPOSIT_ACCOUNT_TRANSACTION:
        return depositAccountTransactionSecret;

      case API_LOG:
        return apiLogSecret;
    }

    throw new CollectRuntimeException("invalid key alias");
  }

  @Override
  public String getIv(KeyAlias keyAlias) {
    switch (keyAlias) {
      case INVEST_ACCOUNT:
        return investAccountIv;

      case INVEST_ACCOUNT_TRANSACTION:
        return investAccountTransactionIv;

      case LOAN_ACCOUNT:
        return loanAccountIv;

      case LOAN_ACCOUNT_TRANSACTION:
        return loanAccountTransactionIv;

      case DEPOSIT_ACCOUNT:
        return depositAccountIv;

      case DEPOSIT_ACCOUNT_TRANSACTION:
        return depositAccountTransactionIv;

      case API_LOG:
        return apiLogIv;
    }

    throw new CollectRuntimeException("invalid key alias");
  }
}
