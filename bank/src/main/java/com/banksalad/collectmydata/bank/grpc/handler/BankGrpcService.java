package com.banksalad.collectmydata.bank.grpc.handler;

import com.banksalad.collectmydata.bank.grpc.client.ConnectClientService;
import com.banksalad.collectmydata.bank.grpc.handler.interceptor.StatsUnaryServerInterceptor;
import com.banksalad.collectmydata.bank.publishment.deposit.DepositAccountPublishService;
import com.banksalad.collectmydata.bank.publishment.deposit.dto.DepositAccountBasicResponse;
import com.banksalad.collectmydata.bank.publishment.deposit.dto.DepositAccountBasicsProtoResponse;
import com.banksalad.collectmydata.bank.publishment.deposit.dto.DepositAccountDetailResponse;
import com.banksalad.collectmydata.bank.publishment.deposit.dto.DepositAccountDetailsProtoResponse;
import com.banksalad.collectmydata.bank.publishment.deposit.dto.DepositAccountTransactionResponse;
import com.banksalad.collectmydata.bank.publishment.deposit.dto.DepositAccountTransactionsProtoResponse;
import com.banksalad.collectmydata.bank.publishment.invest.InvestAccountPublishService;
import com.banksalad.collectmydata.bank.publishment.invest.dto.InvestAccountBasicResponse;
import com.banksalad.collectmydata.bank.publishment.invest.dto.InvestAccountBasicsProtoResponse;
import com.banksalad.collectmydata.bank.publishment.invest.dto.InvestAccountDetailResponse;
import com.banksalad.collectmydata.bank.publishment.invest.dto.InvestAccountDetailsProtoResponse;
import com.banksalad.collectmydata.bank.publishment.invest.dto.InvestAccountTransactionResponse;
import com.banksalad.collectmydata.bank.publishment.invest.dto.InvestAccountTransactionsProtoResponse;
import com.banksalad.collectmydata.bank.publishment.loan.LoanAccountPublishService;
import com.banksalad.collectmydata.bank.publishment.loan.dto.LoanAccountBasicResponse;
import com.banksalad.collectmydata.bank.publishment.loan.dto.LoanAccountBasicsProtoResponse;
import com.banksalad.collectmydata.bank.publishment.loan.dto.LoanAccountDetailResponse;
import com.banksalad.collectmydata.bank.publishment.loan.dto.LoanAccountDetailsProtoResponse;
import com.banksalad.collectmydata.bank.publishment.loan.dto.LoanAccountTransactionResponse;
import com.banksalad.collectmydata.bank.publishment.loan.dto.LoanAccountTransactionsProtoResponse;
import com.banksalad.collectmydata.bank.publishment.summary.AccountSummaryPublishService;
import com.banksalad.collectmydata.bank.publishment.summary.dto.AccountSummariesProtoResponse;
import com.banksalad.collectmydata.bank.publishment.summary.dto.AccountSummaryResponse;
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
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.ListBankInvestAccountBasicsRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.ListBankInvestAccountBasicsResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.ListBankInvestAccountDetailsRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.ListBankInvestAccountDetailsResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.ListBankInvestAccountTransactionsRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.ListBankInvestAccountTransactionsResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.ListBankLoanAccountBasicsRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.ListBankLoanAccountBasicsResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.ListBankLoanAccountDetailsRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.ListBankLoanAccountDetailsResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.ListBankLoanAccountTransactionsRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.ListBankLoanAccountTransactionsResponse;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lognet.springboot.grpc.GRpcService;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Slf4j
@GRpcService(interceptors = {StatsUnaryServerInterceptor.class})
@RequiredArgsConstructor
public class BankGrpcService extends CollectmydatabankGrpc.CollectmydatabankImplBase {

  private final AccountSummaryPublishService accountSummaryPublishService;
  private final DepositAccountPublishService depositAccountPublishService;
  private final InvestAccountPublishService investAccountPublishService;
  private final LoanAccountPublishService loanAccountPublishService;
  private final ConnectClientService connectClientService;

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
      log.error("listBankAccountSummaries gRPC error message, {}", e.getMessage(), e);
      responseObserver.onError(e.handle());

    } catch (Exception e) {
      log.error("listBankAccountSummaries unknown error message, {}", e.getMessage(), e);
      responseObserver.onError(e);
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
      log.error("listBankDepositAccountBasics gRPC error message, {}", e.getMessage(), e);
      responseObserver.onError(e.handle());

    } catch (Exception e) {
      log.error("listBankDepositAccountBasics unknown error message, {}", e.getMessage(), e);
      responseObserver.onError(e);
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
      log.error("listBankDepositAccountDetails gRPC error message, {}", e.getMessage(), e);
      responseObserver.onError(e.handle());

    } catch (Exception e) {
      log.error("listBankDepositAccountDetails unknown error message, {}", e.getMessage(), e);
      responseObserver.onError(e);
    }
  }

  @Override
  public void listBankDepositAccountTransactions(ListBankDepositAccountTransactionsRequest request,
      StreamObserver<ListBankDepositAccountTransactionsResponse> responseObserver) {
    try {
      // TODO : validate parameter value
      List<DepositAccountTransactionResponse> depositAccountTransactionResponses = depositAccountPublishService
          .getDepositAccountTransactionResponses(request);
      DepositAccountTransactionsProtoResponse depositAccountTransactionsProtoResponse = DepositAccountTransactionsProtoResponse
          .builder()
          .depositAccountTransactionResponses(depositAccountTransactionResponses)
          .build();

      responseObserver
          .onNext(depositAccountTransactionsProtoResponse.toListBankDepositAccountTransactionsResponseProto());
      responseObserver.onCompleted();

    } catch (GrpcException e) {
      log.error("listBankDepositAccountTransactions gRPC error message, {}", e.getMessage(), e);
      responseObserver.onError(e.handle());

    } catch (Exception e) {
      log.error("listBankDepositAccountTransactions unknown error message, {}", e.getMessage(), e);
      responseObserver.onError(e);
    }
  }

  @Override
  public void listBankInvestAccountBasics(ListBankInvestAccountBasicsRequest request,
      StreamObserver<ListBankInvestAccountBasicsResponse> responseObserver) {
    try {
      // TODO : validate parameter value
      List<InvestAccountBasicResponse> investAccountBasicResponses = investAccountPublishService
          .getInvestAccountBasicResponses(request);
      InvestAccountBasicsProtoResponse investAccountBasicsProtoResponse = InvestAccountBasicsProtoResponse.builder()
          .investAccountBasicResponses(investAccountBasicResponses)
          .build();

      responseObserver.onNext(investAccountBasicsProtoResponse.toListBankInvestAccountBasicsResponse());
      responseObserver.onCompleted();

    } catch (GrpcException e) {
      log.error("listBankInvestAccountBasics gRPC error message, {}", e.getMessage(), e);
      responseObserver.onError(e.handle());

    } catch (Exception e) {
      log.error("listBankInvestAccountBasics unknown error message, {}", e.getMessage(), e);
      responseObserver.onError(e);
    }
  }

  @Override
  public void listBankInvestAccountDetails(ListBankInvestAccountDetailsRequest request,
      StreamObserver<ListBankInvestAccountDetailsResponse> responseObserver) {
    try {
      // TODO : validate parameter value
      List<InvestAccountDetailResponse> investAccountDetailResponses = investAccountPublishService
          .getInvestAccountDetailResponses(request);
      InvestAccountDetailsProtoResponse investAccountDetailsProtoResponse = InvestAccountDetailsProtoResponse.builder()
          .investAccountDetailResponses(investAccountDetailResponses)
          .build();

      responseObserver.onNext(investAccountDetailsProtoResponse.toListBankInvestAccountDetailsResponse());
      responseObserver.onCompleted();

    } catch (GrpcException e) {
      log.error("listBankInvestAccountDetails gRPC error message, {}", e.getMessage(), e);
      responseObserver.onError(e.handle());

    } catch (Exception e) {
      log.error("listBankInvestAccountDetails unknown error message, {}", e.getMessage(), e);
      responseObserver.onError(e);
    }
  }

  @Override
  public void listBankInvestAccountTransactions(ListBankInvestAccountTransactionsRequest request,
      StreamObserver<ListBankInvestAccountTransactionsResponse> responseObserver) {
    try {
      // TODO : validate parameter value
      List<InvestAccountTransactionResponse> investAccountTransactionResponses = investAccountPublishService
          .getInvestAccountTransactionResponses(request);
      InvestAccountTransactionsProtoResponse investAccountTransactionsProtoResponse = InvestAccountTransactionsProtoResponse
          .builder()
          .investAccountTransactionResponses(investAccountTransactionResponses)
          .build();

      responseObserver.onNext(investAccountTransactionsProtoResponse.toListBankInvestAccountTransactionsResponse());
      responseObserver.onCompleted();

    } catch (GrpcException e) {
      log.error("listBankInvestAccountTransactions gRPC error message, {}", e.getMessage(), e);
      responseObserver.onError(e.handle());

    } catch (Exception e) {
      log.error("listBankInvestAccountTransactions unknown error message, {}", e.getMessage(), e);
      responseObserver.onError(e);
    }
  }

  @Override
  public void listBankLoanAccountBasics(ListBankLoanAccountBasicsRequest request,
      StreamObserver<ListBankLoanAccountBasicsResponse> responseObserver) {
    try {
      // TODO : validate parameter value  & type converter
      List<LoanAccountBasicResponse> loanAccountBasicResponses = loanAccountPublishService
          .getLoanAccountBasicResponses(
              Long.valueOf(request.getBanksaladUserId()),
              connectClientService.getOrganizationByOrganizationObjectid(request.getOrganizationObjectid()).getOrganizationId());
      LoanAccountBasicsProtoResponse loanAccountBasicsProtoResponse = LoanAccountBasicsProtoResponse.builder()
          .loanAccountBasicResponses(loanAccountBasicResponses)
          .build();

      responseObserver.onNext(loanAccountBasicsProtoResponse.toListBankLoanAccountBasicsResponse());
      responseObserver.onCompleted();

    } catch (GrpcException e) {
      log.error("listBankLoanAccountBasics gRPC error message, {}", e.getMessage(), e);
      responseObserver.onError(e.handle());

    } catch (Exception e) {
      log.error("listBankLoanAccountBasics unknown error message, {}", e.getMessage(), e);
      responseObserver.onError(e);
    }
  }

  @Override
  public void listBankLoanAccountDetails(ListBankLoanAccountDetailsRequest request,
      StreamObserver<ListBankLoanAccountDetailsResponse> responseObserver) {
    try {
      // TODO : validate parameter value & type converter
      List<LoanAccountDetailResponse> loanAccountDetailResponses = loanAccountPublishService
          .getLoanAccountDetailResponses(
              Long.valueOf(request.getBanksaladUserId()),
              connectClientService.getOrganizationByOrganizationObjectid(request.getOrganizationObjectid()).getOrganizationId());
      LoanAccountDetailsProtoResponse loanAccountDetailsProtoResponse = LoanAccountDetailsProtoResponse.builder()
          .loanAccountDetailResponses(loanAccountDetailResponses)
          .build();

      responseObserver.onNext(loanAccountDetailsProtoResponse.toListBankLoanAccountDetailsResponse());
      responseObserver.onCompleted();

    } catch (GrpcException e) {
      log.error("listBankLoanAccountDetails gRPC error message, {}", e.getMessage(), e);
      responseObserver.onError(e.handle());

    } catch (Exception e) {
      log.error("listBankLoanAccountDetails unknown error message, {}", e.getMessage(), e);
      responseObserver.onError(e);
    }
  }

  @Override
  public void listBankLoanAccountTransactions(ListBankLoanAccountTransactionsRequest request,
      StreamObserver<ListBankLoanAccountTransactionsResponse> responseObserver) {
    try {
      // TODO : validate parameter value & type converter
      List<LoanAccountTransactionResponse> loanAccountTransactionsProtoResponses = loanAccountPublishService
          .getLoanAccountTransactionResponse(
              Long.valueOf(request.getBanksaladUserId()),
              connectClientService.getOrganizationByOrganizationObjectid(request.getOrganizationObjectid()).getOrganizationId(),
              request.getAccountNum(),
              request.getSeqno().getValue(),
              LocalDateTime.ofEpochSecond(request.getCreatedAfterMs(), 0, ZoneOffset.UTC),
              Long.valueOf(request.getLimit()).intValue());
      LoanAccountTransactionsProtoResponse loanAccountTransactionsProtoResponse = LoanAccountTransactionsProtoResponse
          .builder()
          .loanAccountTransactionResponses(loanAccountTransactionsProtoResponses)
          .build();

      responseObserver.onNext(loanAccountTransactionsProtoResponse.toListLoanAccountTransactionsResponse());
      responseObserver.onCompleted();

    } catch (GrpcException e) {
      log.error("listBankLoanAccountTransactions gRPC error message, {}", e.getMessage(), e);
      responseObserver.onError(e.handle());

    } catch (Exception e) {
      log.error("listBankLoanAccountTransactions unknown error message, {}", e.getMessage(), e);
      responseObserver.onError(e);
    }
  }
}
