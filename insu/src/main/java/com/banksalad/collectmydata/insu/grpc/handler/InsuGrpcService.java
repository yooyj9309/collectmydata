package com.banksalad.collectmydata.insu.grpc.handler;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.common.exception.GrpcException;
import com.banksalad.collectmydata.finance.common.grpc.CollectmydataConnectClientService;
import com.banksalad.collectmydata.insu.grpc.converter.InsuProtoConverter;
import com.banksalad.collectmydata.insu.publishment.car.CarInsurancePublishmentService;
import com.banksalad.collectmydata.insu.publishment.insurance.InsurancePublishmentService;
import com.banksalad.collectmydata.insu.publishment.loan.LoanPublishmentService;
import com.banksalad.collectmydata.insu.publishment.summary.InsuranceSummaryPublishmentService;
import com.banksalad.collectmydata.insu.publishment.summary.LoanSummaryPublishmentService;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainsuGrpc;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainsuProto.CarInsurance;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainsuProto.CarInsuranceTransaction;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainsuProto.InsuranceBasic;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainsuProto.InsuranceContract;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainsuProto.InsurancePayment;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainsuProto.InsuranceSummary;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainsuProto.InsuranceTransaction;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainsuProto.ListCarInsuranceTransactionsRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainsuProto.ListCarInsuranceTransactionsResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainsuProto.ListCarInsurancesRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainsuProto.ListCarInsurancesResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainsuProto.ListInsuranceBasicsRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainsuProto.ListInsuranceBasicsResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainsuProto.ListInsuranceContractsRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainsuProto.ListInsuranceContractsResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainsuProto.ListInsurancePaymentsRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainsuProto.ListInsurancePaymentsResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainsuProto.ListInsuranceSummariesRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainsuProto.ListInsuranceSummariesResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainsuProto.ListInsuranceTransactionsRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainsuProto.ListInsuranceTransactionsResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainsuProto.ListLoanBasicsRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainsuProto.ListLoanBasicsResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainsuProto.ListLoanDetailsRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainsuProto.ListLoanDetailsResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainsuProto.ListLoanSummariesRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainsuProto.ListLoanSummariesResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainsuProto.ListLoanTransactionsRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainsuProto.ListLoanTransactionsResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainsuProto.LoanBasic;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainsuProto.LoanDetail;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainsuProto.LoanSummary;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainsuProto.LoanTransaction;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
//@GRpcService(interceptors = {StatsUnaryServerInterceptor.class})
@Service
@RequiredArgsConstructor
public class InsuGrpcService extends CollectmydatainsuGrpc.CollectmydatainsuImplBase {

  private final InsuranceSummaryPublishmentService insuranceSummaryPublishmentService;
  private final LoanSummaryPublishmentService loanSummaryPublishmentService;
  private final InsurancePublishmentService insurancePublishmentService;
  private final LoanPublishmentService loanPublishmentService;
  private final CarInsurancePublishmentService carInsurancePublishmentService;
  private final CollectmydataConnectClientService collectmydataConnectClientService;
  private final InsuProtoConverter insuProtoConverter;

  @Override
  public void listInsuranceSummaries(ListInsuranceSummariesRequest request,
      StreamObserver<ListInsuranceSummariesResponse> responseObserver) {
    try {
      long banksaladUserId = Long.parseLong(request.getBanksaladUserId());
      String organizationId = collectmydataConnectClientService
          .getOrganizationByOrganizationGuid(request.getOrganizationGuid())
          .getOrganizationId();

      List<InsuranceSummary> insuranceSummaries = insuranceSummaryPublishmentService
          .getInsuranceSummaryResponses(banksaladUserId, organizationId)
          .stream()
          .map(insuProtoConverter::toInsuranceSummary)
          .collect(Collectors.toList());

      responseObserver.onNext(
          ListInsuranceSummariesResponse.newBuilder()
              .addAllInsuranceSummaries(insuranceSummaries)
              .build());
      responseObserver.onCompleted();

    } catch (GrpcException e) {
      log.error("listInsuranceSummaries gRPC error message, {}", e.getMessage(), e);
      responseObserver.onError(e.handle());
    } catch (Exception e) {
      log.error("listInsuranceSummaries unknown error message, {}", e.getMessage(), e);
      responseObserver.onError(e);
    }
  }

  @Override
  public void listInsuranceBasics(ListInsuranceBasicsRequest request,
      StreamObserver<ListInsuranceBasicsResponse> responseObserver) {
    try {
      long banksaladUserId = Long.parseLong(request.getBanksaladUserId());
      String organizationId = collectmydataConnectClientService
          .getOrganizationByOrganizationGuid(request.getOrganizationGuid())
          .getOrganizationId();

      List<InsuranceBasic> insuranceBasics = insurancePublishmentService
          .getInsuranceBasicResponses(banksaladUserId, organizationId)
          .stream()
          .map(insuProtoConverter::toInsuranceBasic)
          .collect(Collectors.toList());

      responseObserver.onNext(
          ListInsuranceBasicsResponse.newBuilder()
              .addAllInsuranceBasics(insuranceBasics)
              .build());
      responseObserver.onCompleted();

    } catch (GrpcException e) {
      log.error("listInsuranceBasics gRPC error message, {}", e.getMessage(), e);
      responseObserver.onError(e.handle());
    } catch (Exception e) {
      log.error("listInsuranceBasics unknown error message, {}", e.getMessage(), e);
      responseObserver.onError(e);
    }
  }

  @Override
  public void listInsuranceContracts(ListInsuranceContractsRequest request,
      StreamObserver<ListInsuranceContractsResponse> responseObserver) {
    try {
      long banksaladUserId = Long.parseLong(request.getBanksaladUserId());
      String organizationId = collectmydataConnectClientService
          .getOrganizationByOrganizationGuid(request.getOrganizationGuid())
          .getOrganizationId();

      List<InsuranceContract> insuranceContracts = insurancePublishmentService
          .getInsuranceContractResponses(banksaladUserId, organizationId)
          .stream()
          .map(insuProtoConverter::toInsuranceContract)
          .collect(Collectors.toList());

      responseObserver.onNext(
          ListInsuranceContractsResponse.newBuilder()
              .addAllInsuranceContracts(insuranceContracts)
              .build());
      responseObserver.onCompleted();

    } catch (GrpcException e) {
      log.error("listInsuranceContracts gRPC error message, {}", e.getMessage(), e);
      responseObserver.onError(e.handle());
    } catch (Exception e) {
      log.error("listInsuranceContracts unknown error message, {}", e.getMessage(), e);
      responseObserver.onError(e);
    }
  }

  @Override
  public void listCarInsurances(ListCarInsurancesRequest request,
      StreamObserver<ListCarInsurancesResponse> responseObserver) {
    try {
      long banksaladUserId = Long.parseLong(request.getBanksaladUserId());
      String organizationId = collectmydataConnectClientService
          .getOrganizationByOrganizationGuid(request.getOrganizationGuid())
          .getOrganizationId();

      List<CarInsurance> carInsurances = carInsurancePublishmentService
          .getCarInsuranceResponses(banksaladUserId, organizationId)
          .stream()
          .map(insuProtoConverter::toCarInsurance)
          .collect(Collectors.toList());

      responseObserver.onNext(
          ListCarInsurancesResponse.newBuilder()
              .addAllCarInsurances(carInsurances)
              .build());
      responseObserver.onCompleted();

    } catch (GrpcException e) {
      log.error("listCarInsurances gRPC error message, {}", e.getMessage(), e);
      responseObserver.onError(e.handle());
    } catch (Exception e) {
      log.error("listCarInsurances unknown error message, {}", e.getMessage(), e);
      responseObserver.onError(e);
    }
  }

  @Override
  public void listInsurancePayments(ListInsurancePaymentsRequest request,
      StreamObserver<ListInsurancePaymentsResponse> responseObserver) {
    try {
      long banksaladUserId = Long.parseLong(request.getBanksaladUserId());
      String organizationId = collectmydataConnectClientService
          .getOrganizationByOrganizationGuid(request.getOrganizationGuid())
          .getOrganizationId();

      List<InsurancePayment> insurancePayments = insurancePublishmentService
          .getInsurancePaymentResponses(banksaladUserId, organizationId)
          .stream()
          .map(insuProtoConverter::toInsurancePayment)
          .collect(Collectors.toList());

      responseObserver.onNext(
          ListInsurancePaymentsResponse.newBuilder()
              .addAllInsurancePayments(insurancePayments)
              .build());
      responseObserver.onCompleted();

    } catch (GrpcException e) {
      log.error("listInsurancePayments gRPC error message, {}", e.getMessage(), e);
      responseObserver.onError(e.handle());
    } catch (Exception e) {
      log.error("listInsurancePayments unknown error message, {}", e.getMessage(), e);
      responseObserver.onError(e);
    }
  }

  @Override
  public void listInsuranceTransactions(ListInsuranceTransactionsRequest request,
      StreamObserver<ListInsuranceTransactionsResponse> responseObserver) {
    try {
      long banksaladUserId = Long.parseLong(request.getBanksaladUserId());
      String organizationId = collectmydataConnectClientService
          .getOrganizationByOrganizationGuid(request.getOrganizationGuid())
          .getOrganizationId();
      String insuNum = request.getInsuNum();
      LocalDateTime createdAt = LocalDateTime.ofEpochSecond(request.getCreatedAfterMs(), 0, ZoneOffset.UTC);
      int limit = Long.valueOf(request.getLimit()).intValue();

      List<InsuranceTransaction> insuranceTransactions = insurancePublishmentService
          .getInsuranceTransactionResponses(banksaladUserId, organizationId, insuNum, createdAt, limit)
          .stream()
          .map(insuProtoConverter::toInsuranceTransaction)
          .collect(Collectors.toList());

      responseObserver.onNext(
          ListInsuranceTransactionsResponse.newBuilder()
              .addAllInsuranceTransactions(insuranceTransactions)
              .build());
      responseObserver.onCompleted();

    } catch (GrpcException e) {
      log.error("listInsuranceTransactions gRPC error message, {}", e.getMessage(), e);
      responseObserver.onError(e.handle());
    } catch (Exception e) {
      log.error("listInsuranceTransactions unknown error message, {}", e.getMessage(), e);
      responseObserver.onError(e);
    }
  }

  @Override
  public void listCarInsuranceTransactions(ListCarInsuranceTransactionsRequest request,
      StreamObserver<ListCarInsuranceTransactionsResponse> responseObserver) {
    try {
      long banksaladUserId = Long.parseLong(request.getBanksaladUserId());
      String organizationId = collectmydataConnectClientService
          .getOrganizationByOrganizationGuid(request.getOrganizationGuid())
          .getOrganizationId();
      String insuNum = request.getInsuNum();
      LocalDateTime createdAt = LocalDateTime.ofEpochSecond(request.getCreatedAfterMs(), 0, ZoneOffset.UTC);
      int limit = Long.valueOf(request.getLimit()).intValue();

      List<CarInsuranceTransaction> carInsuranceTransactions = carInsurancePublishmentService
          .getCarInsuranceTransactionResponses(banksaladUserId, organizationId, insuNum, createdAt, limit)
          .stream()
          .map(insuProtoConverter::toCarInsuranceTransaction)
          .collect(Collectors.toList());

      responseObserver.onNext(
          ListCarInsuranceTransactionsResponse.newBuilder()
              .addAllCarInsuranceTransactions(carInsuranceTransactions)
              .build());
      responseObserver.onCompleted();

    } catch (GrpcException e) {
      log.error("listCarInsuranceTransactions gRPC error message, {}", e.getMessage(), e);
      responseObserver.onError(e.handle());
    } catch (Exception e) {
      log.error("listCarInsuranceTransactions unknown error message, {}", e.getMessage(), e);
      responseObserver.onError(e);
    }
  }

  @Override
  public void listLoanSummaries(ListLoanSummariesRequest request,
      StreamObserver<ListLoanSummariesResponse> responseObserver) {
    try {
      long banksaladUserId = Long.parseLong(request.getBanksaladUserId());
      String organizationId = collectmydataConnectClientService
          .getOrganizationByOrganizationGuid(request.getOrganizationGuid())
          .getOrganizationId();

      List<LoanSummary> loanSummaries = loanSummaryPublishmentService
          .getLoanSummaryResponses(banksaladUserId, organizationId)
          .stream()
          .map(insuProtoConverter::toLoanSummary)
          .collect(Collectors.toList());

      responseObserver.onNext(
          ListLoanSummariesResponse.newBuilder()
              .addAllLoanSummaries(loanSummaries)
              .build());
      responseObserver.onCompleted();

    } catch (GrpcException e) {
      log.error("listLoanSummaries gRPC error message, {}", e.getMessage(), e);
      responseObserver.onError(e.handle());
    } catch (Exception e) {
      log.error("listLoanSummaries unknown error message, {}", e.getMessage(), e);
      responseObserver.onError(e);
    }
  }

  @Override
  public void listLoanBasics(ListLoanBasicsRequest request,
      StreamObserver<ListLoanBasicsResponse> responseObserver) {
    try {
      long banksaladUserId = Long.parseLong(request.getBanksaladUserId());
      String organizationId = collectmydataConnectClientService
          .getOrganizationByOrganizationGuid(request.getOrganizationGuid())
          .getOrganizationId();

      List<LoanBasic> loanBasics = loanPublishmentService
          .getLoanBasicResponses(banksaladUserId, organizationId)
          .stream()
          .map(insuProtoConverter::toLoanBasic)
          .collect(Collectors.toList());

      responseObserver.onNext(
          ListLoanBasicsResponse.newBuilder()
              .addAllLoanBasics(loanBasics)
              .build());
      responseObserver.onCompleted();

    } catch (GrpcException e) {
      log.error("listLoanBasics gRPC error message, {}", e.getMessage(), e);
      responseObserver.onError(e.handle());
    } catch (Exception e) {
      log.error("listLoanBasics unknown error message, {}", e.getMessage(), e);
      responseObserver.onError(e);
    }
  }

  @Override
  public void listLoanDetails(ListLoanDetailsRequest request,
      StreamObserver<ListLoanDetailsResponse> responseObserver) {
    try {
      long banksaladUserId = Long.parseLong(request.getBanksaladUserId());
      String organizationId = collectmydataConnectClientService
          .getOrganizationByOrganizationGuid(request.getOrganizationGuid())
          .getOrganizationId();

      List<LoanDetail> loanDetails = loanPublishmentService
          .getLoanDetailResponses(banksaladUserId, organizationId)
          .stream()
          .map(insuProtoConverter::toLoanDetail)
          .collect(Collectors.toList());

      responseObserver.onNext(
          ListLoanDetailsResponse.newBuilder()
              .addAllLoanDetails(loanDetails)
              .build());
      responseObserver.onCompleted();

    } catch (GrpcException e) {
      log.error("listLoanDetails gRPC error message, {}", e.getMessage(), e);
      responseObserver.onError(e.handle());
    } catch (Exception e) {
      log.error("listLoanDetails unknown error message, {}", e.getMessage(), e);
      responseObserver.onError(e);
    }
  }

  @Override
  public void listLoanTransactions(ListLoanTransactionsRequest request,
      StreamObserver<ListLoanTransactionsResponse> responseObserver) {
    try {
      long banksaladUserId = Long.parseLong(request.getBanksaladUserId());
      String organizationId = collectmydataConnectClientService
          .getOrganizationByOrganizationGuid(request.getOrganizationGuid())
          .getOrganizationId();
      String accountNum = request.getAccountNum();
      LocalDateTime createdAt = LocalDateTime.ofEpochSecond(request.getCreatedAfterMs(), 0, ZoneOffset.UTC);
      int limit = Long.valueOf(request.getLimit()).intValue();

      List<LoanTransaction> loanTransactions = loanPublishmentService
          .getLoanTransactionResponses(banksaladUserId, organizationId, accountNum, createdAt, limit)
          .stream()
          .map(insuProtoConverter::toLoanTransaction)
          .collect(Collectors.toList());

      responseObserver.onNext(
          ListLoanTransactionsResponse.newBuilder()
              .addAllLoanTransactions(loanTransactions)
              .build());
      responseObserver.onCompleted();

    } catch (GrpcException e) {
      log.error("listLoanTransactions gRPC error message, {}", e.getMessage(), e);
      responseObserver.onError(e.handle());
    } catch (Exception e) {
      log.error("listLoanTransactions unknown error message, {}", e.getMessage(), e);
      responseObserver.onError(e);
    }
  }
}
