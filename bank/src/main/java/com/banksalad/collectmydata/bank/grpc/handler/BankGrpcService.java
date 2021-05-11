package com.banksalad.collectmydata.bank.grpc.handler;

import com.banksalad.collectmydata.bank.grpc.converter.BankProtoConverter;
import com.banksalad.collectmydata.bank.publishment.deposit.DepositAccountPublishService;
import com.banksalad.collectmydata.bank.publishment.deposit.dto.DepositAccountBasicResponse;
import com.banksalad.collectmydata.bank.publishment.deposit.dto.DepositAccountDetailResponse;
import com.banksalad.collectmydata.bank.publishment.deposit.dto.DepositAccountTransactionResponse;
import com.banksalad.collectmydata.bank.publishment.invest.InvestAccountPublishService;
import com.banksalad.collectmydata.bank.publishment.invest.dto.InvestAccountBasicResponse;
import com.banksalad.collectmydata.bank.publishment.invest.dto.InvestAccountDetailResponse;
import com.banksalad.collectmydata.bank.publishment.invest.dto.InvestAccountTransactionResponse;
import com.banksalad.collectmydata.bank.publishment.loan.LoanAccountPublishService;
import com.banksalad.collectmydata.bank.publishment.loan.dto.LoanAccountBasicResponse;
import com.banksalad.collectmydata.bank.publishment.loan.dto.LoanAccountDetailResponse;
import com.banksalad.collectmydata.bank.publishment.loan.dto.LoanAccountTransactionResponse;
import com.banksalad.collectmydata.bank.publishment.summary.AccountSummaryPublishService;
import com.banksalad.collectmydata.bank.publishment.summary.dto.AccountSummaryResponse;
import com.banksalad.collectmydata.common.exception.GrpcException;
import com.banksalad.collectmydata.finance.common.grpc.CollectmydataConnectClientService;
import com.banksalad.collectmydata.finance.common.grpc.handler.interceptor.StatsUnaryServerInterceptor;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankGrpc;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.BankAccountSummary;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.BankDepositAccountBasic;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.BankDepositAccountDetail;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.BankDepositAccountTransaction;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.BankInvestAccountBasic;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.BankInvestAccountDetail;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.BankInvestAccountTransaction;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.BankLoanAccountBasic;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.BankLoanAccountDetail;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.BankLoanAccountTransaction;
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
import java.util.stream.Collectors;

@Slf4j
@GRpcService(interceptors = {StatsUnaryServerInterceptor.class})
@RequiredArgsConstructor
public class BankGrpcService extends CollectmydatabankGrpc.CollectmydatabankImplBase {

  private final AccountSummaryPublishService accountSummaryPublishService;
  private final DepositAccountPublishService depositAccountPublishService;
  private final InvestAccountPublishService investAccountPublishService;
  private final LoanAccountPublishService loanAccountPublishService;
  private final CollectmydataConnectClientService collectmydataConnectClientService;
  private final BankProtoConverter bankProtoConverter;

  @Override
  public void listBankAccountSummaries(ListBankAccountSummariesRequest request,
      StreamObserver<ListBankAccountSummariesResponse> responseObserver) {
    try {
      // TODO : validate parameter value
      long banksaladUserId = Long.valueOf(request.getBanksaladUserId());
      String organizationId = collectmydataConnectClientService
          .getOrganizationByOrganizationGuid(request.getOrganizationGuid()).getOrganizationId();

      List<AccountSummaryResponse> accountSummaryResponses = accountSummaryPublishService
          .getAccountSummaryResponses(banksaladUserId, organizationId);

      List<BankAccountSummary> bankAccountSummaries = accountSummaryResponses.stream()
          .map(bankProtoConverter::toBankAccountSummary)
          .collect(Collectors.toList());

      responseObserver.onNext(
          ListBankAccountSummariesResponse.newBuilder()
              .addAllAccountSummaries(bankAccountSummaries)
              .build());
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
      long banksaladUserId = Long.valueOf(request.getBanksaladUserId());
      String organizationId = collectmydataConnectClientService
          .getOrganizationByOrganizationGuid(request.getOrganizationGuid()).getOrganizationId();

      List<DepositAccountBasicResponse> depositAccountBasicResponses = depositAccountPublishService
          .getDepositAccountBasicResponses(banksaladUserId, organizationId);

      List<BankDepositAccountBasic> bankDepositAccountBasics = depositAccountBasicResponses.stream()
          .map(bankProtoConverter::toBankDepositAccountBasic)
          .collect(Collectors.toList());

      responseObserver.onNext(
          ListBankDepositAccountBasicsResponse.newBuilder()
              .addAllDepositAccountBasics(bankDepositAccountBasics)
              .build());
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
      long banksaladUserId = Long.valueOf(request.getBanksaladUserId());
      String organizationId = collectmydataConnectClientService
          .getOrganizationByOrganizationGuid(request.getOrganizationGuid()).getOrganizationId();

      List<DepositAccountDetailResponse> depositAccountDetailResponses = depositAccountPublishService
          .getDepositAccountDetailResponses(banksaladUserId, organizationId);

      List<BankDepositAccountDetail> bankDepositAccountDetails = depositAccountDetailResponses.stream()
          .map(bankProtoConverter::toBankDepositAccountDetail)
          .collect(Collectors.toList());

      responseObserver.onNext(
          ListBankDepositAccountDetailsResponse.newBuilder()
              .addAllDepositAccountDetails(bankDepositAccountDetails)
              .build());
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
      long banksaladUserId = Long.valueOf(request.getBanksaladUserId());
      String organizationId = collectmydataConnectClientService
          .getOrganizationByOrganizationGuid(request.getOrganizationGuid()).getOrganizationId();
      LocalDateTime createdAt = LocalDateTime.ofEpochSecond(request.getCreatedAfterMs(), 0, ZoneOffset.UTC);

      List<DepositAccountTransactionResponse> depositAccountTransactionResponses = depositAccountPublishService
          .getDepositAccountTransactionResponses(banksaladUserId, organizationId, request.getAccountNum(),
              request.getSeqno().getValue(), createdAt, Long.valueOf(request.getLimit()).intValue());

      List<BankDepositAccountTransaction> bankDepositAccountTransactions = depositAccountTransactionResponses.stream()
          .map(bankProtoConverter::toBankDepositAccountTransaction)
          .collect(Collectors.toList());

      responseObserver.onNext(
          ListBankDepositAccountTransactionsResponse.newBuilder()
              .addAllDepositAccountTransactions(bankDepositAccountTransactions)
              .build());
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
      long banksaladUserId = Long.valueOf(request.getBanksaladUserId());
      String organizationId = collectmydataConnectClientService
          .getOrganizationByOrganizationGuid(request.getOrganizationGuid()).getOrganizationId();

      List<InvestAccountBasicResponse> investAccountBasicResponses = investAccountPublishService
          .getInvestAccountBasicResponses(banksaladUserId, organizationId);

      List<BankInvestAccountBasic> bankInvestAccountBasics = investAccountBasicResponses.stream()
          .map(bankProtoConverter::toBankInvestAccountBasic)
          .collect(Collectors.toList());

      responseObserver.onNext(
          ListBankInvestAccountBasicsResponse.newBuilder()
              .addAllInvestAccountBasics(bankInvestAccountBasics)
              .build());
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
      long banksaladUserId = Long.valueOf(request.getBanksaladUserId());
      String organizationId = collectmydataConnectClientService
          .getOrganizationByOrganizationGuid(request.getOrganizationGuid()).getOrganizationId();

      List<InvestAccountDetailResponse> investAccountDetailResponses = investAccountPublishService
          .getInvestAccountDetailResponses(banksaladUserId, organizationId);

      List<BankInvestAccountDetail> bankInvestAccountDetails = investAccountDetailResponses.stream()
          .map(bankProtoConverter::toBankInvestAccountDetail)
          .collect(Collectors.toList());

      responseObserver.onNext(
          ListBankInvestAccountDetailsResponse.newBuilder()
              .addAllInvestAccountDetails(bankInvestAccountDetails)
              .build());
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
      long banksaladUserId = Long.valueOf(request.getBanksaladUserId());
      String organizationId = collectmydataConnectClientService
          .getOrganizationByOrganizationGuid(request.getOrganizationGuid()).getOrganizationId();
      LocalDateTime createdAt = LocalDateTime.ofEpochSecond(request.getCreatedAfterMs(), 0, ZoneOffset.UTC);

      List<InvestAccountTransactionResponse> investAccountTransactionResponses = investAccountPublishService
          .getInvestAccountTransactionResponses(banksaladUserId, organizationId, request.getAccountNum(),
              request.getSeqno().getValue(), createdAt, Long.valueOf(request.getLimit()).intValue());

      List<BankInvestAccountTransaction> bankInvestAccountTransactions = investAccountTransactionResponses.stream()
          .map(bankProtoConverter::toBankInvestAccountTransaction)
          .collect(Collectors.toList());

      responseObserver.onNext(
          ListBankInvestAccountTransactionsResponse.newBuilder()
              .addAllInvestAccountTransactions(bankInvestAccountTransactions)
              .build());
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
      long banksaladUserId = Long.valueOf(request.getBanksaladUserId());
      String organizationId = collectmydataConnectClientService
          .getOrganizationByOrganizationGuid(request.getOrganizationGuid()).getOrganizationId();

      List<LoanAccountBasicResponse> loanAccountBasicResponses = loanAccountPublishService
          .getLoanAccountBasicResponses(banksaladUserId, organizationId);

      List<BankLoanAccountBasic> bankLoanAccountBasics = loanAccountBasicResponses.stream()
          .map(bankProtoConverter::toBankLoanAccountBasic)
          .collect(Collectors.toList());

      responseObserver.onNext(
          ListBankLoanAccountBasicsResponse.newBuilder()
              .addAllLoanAccountBasics(bankLoanAccountBasics)
              .build());
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
      long banksaladUserId = Long.valueOf(request.getBanksaladUserId());
      String organizationId = collectmydataConnectClientService
          .getOrganizationByOrganizationGuid(request.getOrganizationGuid()).getOrganizationId();

      List<LoanAccountDetailResponse> loanAccountDetailResponses = loanAccountPublishService
          .getLoanAccountDetailResponses(banksaladUserId, organizationId);

      List<BankLoanAccountDetail> bankLoanAccountDetails = loanAccountDetailResponses.stream()
          .map(bankProtoConverter::toBankLoanAccountDetail)
          .collect(Collectors.toList());

      responseObserver.onNext(
          ListBankLoanAccountDetailsResponse.newBuilder()
              .addAllLoanAccountDetails(bankLoanAccountDetails)
              .build());
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
      long banksaladUserId = Long.valueOf(request.getBanksaladUserId());
      String organizationId = collectmydataConnectClientService
          .getOrganizationByOrganizationGuid(request.getOrganizationGuid()).getOrganizationId();
      LocalDateTime createdAt = LocalDateTime.ofEpochSecond(request.getCreatedAfterMs(), 0, ZoneOffset.UTC);

      List<LoanAccountTransactionResponse> loanAccountTransactionsProtoResponses = loanAccountPublishService
          .getLoanAccountTransactionResponse(banksaladUserId, organizationId, request.getAccountNum(),
              request.getSeqno().getValue(), createdAt, Long.valueOf(request.getLimit()).intValue());

      List<BankLoanAccountTransaction> bankLoanAccountTransactions = loanAccountTransactionsProtoResponses.stream()
          .map(bankProtoConverter::toBankLoanAccountTransaction)
          .collect(Collectors.toList());

      responseObserver.onNext(
          ListBankLoanAccountTransactionsResponse.newBuilder()
              .addAllLoanAccountTransactions(bankLoanAccountTransactions)
              .build());
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
