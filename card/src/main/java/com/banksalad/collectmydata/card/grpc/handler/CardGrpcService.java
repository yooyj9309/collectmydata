package com.banksalad.collectmydata.card.grpc.handler;

import com.banksalad.collectmydata.card.publishment.summary.CardSummaryPublishService;
import com.banksalad.collectmydata.card.publishment.summary.dto.CardSummariesProtoResponse;
import com.banksalad.collectmydata.card.summary.dto.CardSummary;
import com.banksalad.collectmydata.common.exception.GrpcException;
import com.banksalad.collectmydata.common.util.LogFormatUtil;
import com.banksalad.collectmydata.finance.common.grpc.CollectmydataConnectClientService;
import com.banksalad.collectmydata.finance.common.grpc.handler.interceptor.StatsUnaryServerInterceptor;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardGrpc;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.ListCardApprovalDomesticsRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.ListCardApprovalDomesticsResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.ListCardApprovalOverseasRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.ListCardApprovalOverseasResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.ListCardBasicsRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.ListCardBasicsResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.ListCardBillBasicsRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.ListCardBillBasicsResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.ListCardBillDetailsRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.ListCardBillDetailsResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.ListCardLoanLongTermsRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.ListCardLoanLongTermsResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.ListCardLoanShortTermsRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.ListCardLoanShortTermsResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.ListCardLoanSummariesRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.ListCardLoanSummariesResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.ListCardPaymentsRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.ListCardPaymentsResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.ListCardPointsRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.ListCardPointsResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.ListCardRevolvingsRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.ListCardRevolvingsResponse;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.ListCardSummariesRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.ListCardSummariesResponse;
import com.google.protobuf.DescriptorProtos.EnumDescriptorProto.EnumReservedRangeOrBuilder;
import io.grpc.stub.ServerCalls;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lognet.springboot.grpc.GRpcService;

import java.util.List;

@Slf4j
@GRpcService(interceptors = {StatsUnaryServerInterceptor.class})
@RequiredArgsConstructor
public class CardGrpcService extends CollectmydatacardGrpc.CollectmydatacardImplBase {

  private static final String GRPC_ERROR_MESSAGE = "gRPC error message";
  private static final String UNKNOWN_ERROR_MESSAGE  = "unknown error message";

  private final CardSummaryPublishService cardSummaryPublishService;
  private final CollectmydataConnectClientService collectmydataConnectClientService;


  @Override
  public void listCardSummaries(
      ListCardSummariesRequest request, StreamObserver<ListCardSummariesResponse> responseObserver) {
    try {
      long banksaladUserId = Long.parseLong(request.getBanksaladUserId());
      String organizationId = collectmydataConnectClientService
          .getOrganizationByOrganizationObjectid(request.getOrganizationObjectid()).getOrganizationId();

      List<CardSummary> cardSummaryResponses = cardSummaryPublishService
          .getCardSummaryResponses(banksaladUserId, organizationId);

      CardSummariesProtoResponse cardSummariesProtoResponse = CardSummariesProtoResponse.builder()
          .cardSummaries(cardSummaryResponses)
          .build();

      responseObserver.onNext(cardSummariesProtoResponse.toListCardSummariesResponseProto());
      responseObserver.onCompleted();

    } catch (GrpcException e) {
      log.error(LogFormatUtil.makeLogFormat("listCardSummaries", GRPC_ERROR_MESSAGE), e.getMessage(), e);

    } catch (Exception e) {
      log.error(LogFormatUtil.makeLogFormat("listCardSummaries", UNKNOWN_ERROR_MESSAGE), e.getMessage(), e);
    }
  }

  @Override
  public void listCardBasics(ListCardBasicsRequest request, StreamObserver<ListCardBasicsResponse> responseObserver) {
    super.listCardBasics(request, responseObserver);
  }

  @Override
  public void listCardPoints(ListCardPointsRequest request, StreamObserver<ListCardPointsResponse> responseObserver) {
    super.listCardPoints(request, responseObserver);
  }

  @Override
  public void listCardBillBasics(ListCardBillBasicsRequest request,
      StreamObserver<ListCardBillBasicsResponse> responseObserver) {
    super.listCardBillBasics(request, responseObserver);
  }

  @Override
  public void listCardBillDetails(ListCardBillDetailsRequest request,
      StreamObserver<ListCardBillDetailsResponse> responseObserver) {
    super.listCardBillDetails(request, responseObserver);
  }

  @Override
  public void listCardPayments(ListCardPaymentsRequest request,
      StreamObserver<ListCardPaymentsResponse> responseObserver) {
    super.listCardPayments(request, responseObserver);
  }

  @Override
  public void listCardApprovalDomestics(ListCardApprovalDomesticsRequest request,
      StreamObserver<ListCardApprovalDomesticsResponse> responseObserver) {
    super.listCardApprovalDomestics(request, responseObserver);
  }

  @Override
  public void listCardApprovalOverseas(ListCardApprovalOverseasRequest request,
      StreamObserver<ListCardApprovalOverseasResponse> responseObserver) {
    super.listCardApprovalOverseas(request, responseObserver);
  }

  @Override
  public void listCardLoanSummaries(ListCardLoanSummariesRequest request,
      StreamObserver<ListCardLoanSummariesResponse> responseObserver) {
    super.listCardLoanSummaries(request, responseObserver);
  }

  @Override
  public void listCardRevolvings(ListCardRevolvingsRequest request,
      StreamObserver<ListCardRevolvingsResponse> responseObserver) {
    super.listCardRevolvings(request, responseObserver);
  }

  @Override
  public void listCardLoanShortTerms(ListCardLoanShortTermsRequest request,
      StreamObserver<ListCardLoanShortTermsResponse> responseObserver) {
    super.listCardLoanShortTerms(request, responseObserver);
  }

  @Override
  public void listCardLoanLongTerms(ListCardLoanLongTermsRequest request,
      StreamObserver<ListCardLoanLongTermsResponse> responseObserver) {
    super.listCardLoanLongTerms(request, responseObserver);
  }
}
