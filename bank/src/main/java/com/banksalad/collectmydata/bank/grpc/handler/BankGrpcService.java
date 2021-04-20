package com.banksalad.collectmydata.bank.grpc.handler;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.bank.publishment.deposit.dto.DepositAccountBasicResponse;
import com.banksalad.collectmydata.bank.publishment.deposit.dto.DepositAccountBasicsProtoResponse;
import com.banksalad.collectmydata.bank.publishment.deposit.dto.DepositAccountDetailResponse;
import com.banksalad.collectmydata.bank.publishment.deposit.dto.DepositAccountDetailsProtoResponse;
import com.banksalad.collectmydata.bank.publishment.deposit.dto.DepositAccountTransactionResponse;
import com.banksalad.collectmydata.bank.publishment.deposit.dto.DepositAccountTransactionsProtoResponse;
import com.banksalad.collectmydata.bank.publishment.summary.AccountSummaryPublishService;
import com.banksalad.collectmydata.bank.publishment.deposit.DepositAccountPublishService;
import com.banksalad.collectmydata.bank.publishment.summary.dto.AccountSummaryResponse;
import com.banksalad.collectmydata.bank.publishment.summary.dto.AccountSummariesProtoResponse;
import com.banksalad.collectmydata.common.exception.GrpcException;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankGrpc;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.ListBankAccountSummariesRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.ListBankAccountSummariesResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.ListBankDepositAccountBasicsRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.ListBankDepositAccountBasicsResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.ListBankDepositAccountDetailsRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.ListBankDepositAccountDetailsResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.ListBankDepositAccountTransactionsRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.ListBankDepositAccountTransactionsResponse;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BankGrpcService extends CollectmydatabankGrpc.CollectmydatabankImplBase {

  private final AccountSummaryPublishService accountSummaryPublishService;
  private final DepositAccountPublishService depositAccountPublishService;

  @Override
  public void listBankAccountSummaries(ListBankAccountSummariesRequest request,
      StreamObserver<ListBankAccountSummariesResponse> responseObserver) {
    try {
      // TODO : validate parameter value
      List<AccountSummaryResponse> accountSummaryResponses = accountSummaryPublishService
          .getAccountSummaryResponses(request);
      AccountSummariesProtoResponse accountSummariesProtoResponse = AccountSummariesProtoResponse.builder()
          .accountSummaryResponses(accountSummaryResponses)
          .build();

      responseObserver.onNext(accountSummariesProtoResponse.toListBankAccountSummariesResponseProto());
      responseObserver.onCompleted();
    } catch (GrpcException e) {
      responseObserver.onError(e.handle());
    } catch (Exception e) {
      log.error("listBankAccountSummaries error message, {}", e.getMessage(), e);
      responseObserver.onError(new GrpcException().handle());
    }
  }

  @Override
  public void listBankDepositAccountBasics(ListBankDepositAccountBasicsRequest request,
      StreamObserver<ListBankDepositAccountBasicsResponse> responseObserver) {
    try {
      // TODO : validate parameter value
      List<DepositAccountBasicResponse> depositAccountBasicResponses = depositAccountPublishService
          .getDepositAccountBasicResponses(request);
      DepositAccountBasicsProtoResponse depositAccountBasicsProtoResponse = DepositAccountBasicsProtoResponse.builder()
          .depositAccountBasicResponses(depositAccountBasicResponses)
          .build();

      responseObserver.onNext(depositAccountBasicsProtoResponse.toListBankDepositAccountBasicsResponseProto());
      responseObserver.onCompleted();
    } catch (GrpcException e) {
      responseObserver.onError(e.handle());
    } catch (Exception e) {
      log.error("listBankDepositAccountBasics error message, {}", e.getMessage(), e);
      responseObserver.onError(new GrpcException().handle());
    }
  }

  @Override
  public void listBankDepositAccountDetails(ListBankDepositAccountDetailsRequest request,
      StreamObserver<ListBankDepositAccountDetailsResponse> responseObserver) {
    try {
      // TODO : validate parameter value
      List<DepositAccountDetailResponse> depositAccountDetailResponses = depositAccountPublishService
          .getDepositAccountDetailResponses(request);
      DepositAccountDetailsProtoResponse depositAccountDetailsProtoResponse = DepositAccountDetailsProtoResponse
          .builder()
          .depositAccountDetailResponses(depositAccountDetailResponses)
          .build();

      responseObserver.onNext(depositAccountDetailsProtoResponse.toListBankDepositAccountDetailsResponseProto());
      responseObserver.onCompleted();
    } catch (GrpcException e) {
      responseObserver.onError(e.handle());
    } catch (Exception e) {
      log.error("listBankDepositAccountDetails error message, {}", e.getMessage(), e);
      responseObserver.onError(new GrpcException().handle());
    }
  }

  @Override
  public void listBankDepositAccountTransactions(ListBankDepositAccountTransactionsRequest request,
      StreamObserver<ListBankDepositAccountTransactionsResponse> responseObserver) {
    try {
      // TODO : validate parameter value
      List<DepositAccountTransactionResponse> depositAccountTransactionResponses = depositAccountPublishService
          .getDepositAccountTransactionResponses(request);
      DepositAccountTransactionsProtoResponse depositAccountTransactionsProtoResponse = DepositAccountTransactionsProtoResponse.builder()
          .depositAccountTransactionResponses(depositAccountTransactionResponses)
          .build();

      responseObserver.onNext(depositAccountTransactionsProtoResponse.toListBankDepositAccountTransactionsResponseProto());
      responseObserver.onCompleted();
    } catch (GrpcException e) {
      responseObserver.onError(e.handle());
    } catch (Exception e) {
      log.error("listBankDepositAccountTransactions error message, {}", e.getMessage(), e);
      responseObserver.onError(new GrpcException().handle());
    }
  }
}
