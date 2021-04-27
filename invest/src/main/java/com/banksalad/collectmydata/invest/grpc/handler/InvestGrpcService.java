package com.banksalad.collectmydata.invest.grpc.handler;

import com.banksalad.collectmydata.common.exception.GrpcException;
import com.banksalad.collectmydata.finance.common.grpc.CollectmydataConnectClientService;
import com.banksalad.collectmydata.invest.grpc.converter.InvestProtoConverter;
import com.banksalad.collectmydata.invest.grpc.handler.interceptor.StatsUnaryServerInterceptor;
import com.banksalad.collectmydata.invest.publishment.account.AccountBasicPublishService;
import com.banksalad.collectmydata.invest.publishment.account.AccountProductPublishService;
import com.banksalad.collectmydata.invest.publishment.account.AccountTransactionPublishService;
import com.banksalad.collectmydata.invest.publishment.account.dto.AccountBasicResponse;
import com.banksalad.collectmydata.invest.publishment.account.dto.AccountProductResponse;
import com.banksalad.collectmydata.invest.publishment.account.dto.AccountTransactionResponse;
import com.banksalad.collectmydata.invest.publishment.summary.AccountSummaryPublishService;
import com.banksalad.collectmydata.invest.publishment.summary.dto.AccountSummaryResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainvestGrpc;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainvestProto.InvestAccountBasic;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainvestProto.InvestAccountProduct;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainvestProto.InvestAccountSummary;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainvestProto.InvestAccountTransaction;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainvestProto.ListInvestAccountBasicsRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainvestProto.ListInvestAccountBasicsResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainvestProto.ListInvestAccountProductsRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainvestProto.ListInvestAccountProductsResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainvestProto.ListInvestAccountSummariesRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainvestProto.ListInvestAccountSummariesResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainvestProto.ListInvestAccountTransactionsRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatainvestProto.ListInvestAccountTransactionsResponse;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lognet.springboot.grpc.GRpcService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.banksalad.collectmydata.common.util.DateUtil.UTC_ZONE_ID;

@Slf4j
@GRpcService(interceptors = { StatsUnaryServerInterceptor.class })
@RequiredArgsConstructor
public class InvestGrpcService extends CollectmydatainvestGrpc.CollectmydatainvestImplBase {

  private final CollectmydataConnectClientService connectClientService;
  private final AccountSummaryPublishService accountSummaryPublishService;
  private final AccountBasicPublishService accountBasicPublishService;
  private final AccountTransactionPublishService accountTransactionPublishService;
  private final AccountProductPublishService accountProductPublishService;
  private final InvestProtoConverter investProtoConverter;

  @Override
  public void listInvestAccountSummaries(ListInvestAccountSummariesRequest request,
      StreamObserver<ListInvestAccountSummariesResponse> responseObserver) {

    try {
      long banksaladUserId = Long.parseLong(request.getBanksaladUserId());
      String organizationId = connectClientService.getOrganizationByOrganizationObjectid(request.getOrganizationObjectid())
          .getOrganizationId();

      List<AccountSummaryResponse> accountSummaryResponses = accountSummaryPublishService
          .getAccountSummaryResponses(banksaladUserId, organizationId);

      List<InvestAccountSummary> investAccountSummaries = accountSummaryResponses
          .stream()
          .map(investProtoConverter::toInvestAccountSummary)
          .collect(Collectors.toList());

      responseObserver.onNext(ListInvestAccountSummariesResponse.newBuilder()
          .addAllAccountSummaries(investAccountSummaries)
          .build());
      responseObserver.onCompleted();

    } catch (GrpcException e) {
      log.error("listInvestAccountSummaries gRPC error message, {}", e.getMessage(), e);
      responseObserver.onError(e.handle());

    } catch (Exception e) {
      log.error("listBankAccountSummaries unknown error message, {}", e.getMessage(), e);
      responseObserver.onError(e);

    }
  }

  @Override
  public void listInvestAccountBasics(ListInvestAccountBasicsRequest request,
      StreamObserver<ListInvestAccountBasicsResponse> responseObserver) {

    try {
      long banksaladUserId = Long.parseLong(request.getBanksaladUserId());
      String organizationId = connectClientService.getOrganizationByOrganizationObjectid(request.getOrganizationObjectid())
          .getOrganizationId();

      List<AccountBasicResponse> accountBasicResponses = accountBasicPublishService
          .getAccountBasicResponses(banksaladUserId, organizationId);

      List<InvestAccountBasic> investAccountBasics = accountBasicResponses.stream()
          .map(investProtoConverter::toInvestAccountBasic)
          .collect(Collectors.toList());

      responseObserver.onNext(ListInvestAccountBasicsResponse.newBuilder()
          .addAllAccountBasics(investAccountBasics)
          .build());
      responseObserver.onCompleted();

    } catch (GrpcException e) {
      log.error("listInvestAccountBasics gRPC error message, {}", e.getMessage(), e);
      responseObserver.onError(e.handle());

    } catch (Exception e) {
      log.error("listInvestAccountBasics unknown error message, {}", e.getMessage(), e);
      responseObserver.onError(e);

    }
  }

  @Override
  public void listInvestAccountTransactions(ListInvestAccountTransactionsRequest request,
      StreamObserver<ListInvestAccountTransactionsResponse> responseObserver) {

    try {
      long banksaladUserId = Long.parseLong(request.getBanksaladUserId());
      String organizationId = connectClientService.getOrganizationByOrganizationObjectid(request.getOrganizationObjectid())
          .getOrganizationId();
      LocalDateTime createdAfterMs = LocalDateTime
          .ofInstant(Instant.ofEpochSecond(request.getCreatedAfterMs()), UTC_ZONE_ID);

      List<AccountTransactionResponse> accountTransactionResponses = accountTransactionPublishService
          .getAccountTransactionResponses(banksaladUserId, organizationId, request.getAccountNum(), createdAfterMs,
              request.getLimit());

      List<InvestAccountTransaction> investAccountTransactions = accountTransactionResponses.stream()
          .map(investProtoConverter::toInvestAccountTransaction)
          .collect(Collectors.toList());

      responseObserver.onNext(ListInvestAccountTransactionsResponse.newBuilder()
          .addAllAccountTransactions(investAccountTransactions)
          .build());
      responseObserver.onCompleted();

    } catch (GrpcException e) {
      log.error("listInvestAccountTransactions gRPC error message, {}", e.getMessage(), e);
      responseObserver.onError(e.handle());

    } catch (Exception e) {
      log.error("listInvestAccountTransactions unknown error message, {}", e.getMessage(), e);
      responseObserver.onError(e);

    }
  }

  @Override
  public void listInvestAccountProducts(ListInvestAccountProductsRequest request,
      StreamObserver<ListInvestAccountProductsResponse> responseObserver) {

    try {
      long banksaladUserId = Long.parseLong(request.getBanksaladUserId());
      String organizationId = connectClientService.getOrganizationByOrganizationObjectid(request.getOrganizationObjectid())
          .getOrganizationId();

      List<AccountProductResponse> accountProductResponses = accountProductPublishService
          .getAccountProductResponses(banksaladUserId, organizationId);

      List<InvestAccountProduct> investAccountProducts = accountProductResponses.stream()
          .map(investProtoConverter::toInvestAccountProduct)
          .collect(Collectors.toList());

      responseObserver.onNext(ListInvestAccountProductsResponse.newBuilder()
          .addAllAccountProducts(investAccountProducts)
          .build());
      responseObserver.onCompleted();

    } catch (GrpcException e) {
      log.error("listInvestAccountProducts gRPC error message, {}", e.getMessage(), e);
      responseObserver.onError(e.handle());

    } catch (Exception e) {
      log.error("listInvestAccountProducts unknown error message, {}", e.getMessage(), e);
      responseObserver.onError(e);

    }
  }
}
