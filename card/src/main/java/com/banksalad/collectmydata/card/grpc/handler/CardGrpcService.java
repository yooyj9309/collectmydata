package com.banksalad.collectmydata.card.grpc.handler;

import com.banksalad.collectmydata.card.publishment.summary.CardSummaryPublishService;
import com.banksalad.collectmydata.card.publishment.summary.dto.CardSummariesProtoResponse;
import com.banksalad.collectmydata.card.publishment.userbase.UserBasePublishService;
import com.banksalad.collectmydata.card.publishment.userbase.dto.CardLoanLongTermProtoResponse;
import com.banksalad.collectmydata.card.publishment.userbase.dto.CardLoanShortTermProtoResponse;
import com.banksalad.collectmydata.card.publishment.userbase.dto.CardLoanSummaryProtoResponse;
import com.banksalad.collectmydata.card.publishment.userbase.dto.CardRevolvingProtoResponse;
import com.banksalad.collectmydata.card.publishment.userbase.dto.LoanLongTermPublishment;
import com.banksalad.collectmydata.card.publishment.userbase.dto.LoanShortTermPublishment;
import com.banksalad.collectmydata.card.publishment.userbase.dto.LoanSummaryPublishment;
import com.banksalad.collectmydata.card.publishment.userbase.dto.PaymentProtoResponse;
import com.banksalad.collectmydata.card.publishment.userbase.dto.PaymentPublishment;
import com.banksalad.collectmydata.card.publishment.userbase.dto.PointProtoResponse;
import com.banksalad.collectmydata.card.publishment.userbase.dto.PointPublishment;
import com.banksalad.collectmydata.card.publishment.userbase.dto.RevolvingPublishment;
import com.banksalad.collectmydata.card.summary.dto.CardSummary;
import com.banksalad.collectmydata.common.exception.GrpcException;
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
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lognet.springboot.grpc.GRpcService;

import java.util.List;

import static com.banksalad.collectmydata.common.util.LogFormatUtil.makeLogFormat;

@Slf4j
@GRpcService(interceptors = {StatsUnaryServerInterceptor.class})
@RequiredArgsConstructor
public class CardGrpcService extends CollectmydatacardGrpc.CollectmydatacardImplBase {

  private static final String GRPC_ERROR_MESSAGE = "gRPC error message";
  private static final String UNKNOWN_ERROR_MESSAGE  = "unknown error message";

  private final CardSummaryPublishService cardSummaryPublishService;
  private final UserBasePublishService userBasePublishService;

  private final CollectmydataConnectClientService collectmydataConnectClientService;


  @Override
  public void listCardSummaries(
      ListCardSummariesRequest request, StreamObserver<ListCardSummariesResponse> responseObserver) {
    try {
      long banksaladUserId = Long.parseLong(request.getBanksaladUserId());
      String organizationId = collectmydataConnectClientService
          .getOrganizationByOrganizationGuid(request.getOrganizationGuid()).getOrganizationId();

      List<CardSummary> cardSummaryResponses = cardSummaryPublishService
          .getCardSummaryResponses(banksaladUserId, organizationId);

      CardSummariesProtoResponse cardSummariesProtoResponse = CardSummariesProtoResponse.builder()
          .cardSummaries(cardSummaryResponses)
          .build();

      responseObserver.onNext(cardSummariesProtoResponse.toListCardSummariesResponseProto());
      responseObserver.onCompleted();

    } catch (GrpcException e) {
      log.error(makeLogFormat("listCardSummaries", GRPC_ERROR_MESSAGE), e.getMessage(), e);
      responseObserver.onError(e.handle());

    } catch (Exception e) {
      log.error(makeLogFormat("listCardSummaries", UNKNOWN_ERROR_MESSAGE), e.getMessage(), e);
      responseObserver.onError(e);
    }
  }

  @Override
  public void listCardBasics(ListCardBasicsRequest request, StreamObserver<ListCardBasicsResponse> responseObserver) {
    super.listCardBasics(request, responseObserver);
  }

  @Override
  public void listCardPoints(ListCardPointsRequest request, StreamObserver<ListCardPointsResponse> responseObserver) {
    try {
      long banksaladUserId = Long.parseLong(request.getBanksaladUserId());
      String organizationId = collectmydataConnectClientService
          .getOrganizationByOrganizationGuid(request.getOrganizationGuid()).getOrganizationId();

      List<PointPublishment> cardPointResponses = userBasePublishService.getCardPointResponses(banksaladUserId, organizationId);

      PointProtoResponse pointProtoResponse = PointProtoResponse.builder()
          .pointPublishments(cardPointResponses)
          .build();

      responseObserver.onNext(pointProtoResponse.toListCardPointResponseProto());
      responseObserver.onCompleted();

    } catch (GrpcException e) {
      log.error(makeLogFormat("listCardPoints", UNKNOWN_ERROR_MESSAGE), e.getMessage(), e);
      responseObserver.onError(e.handle());

    } catch (Exception e ) {
      log.error(makeLogFormat("listCardPoints", UNKNOWN_ERROR_MESSAGE), e.getMessage(), e);
      responseObserver.onError(e);
    }
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
    try {
      long banksaladUserId = Long.parseLong(request.getBanksaladUserId());
      String organizationId = collectmydataConnectClientService
          .getOrganizationByOrganizationGuid(request.getOrganizationGuid()).getOrganizationId();

      List<PaymentPublishment> payments = userBasePublishService.getPaymentsResponses(banksaladUserId, organizationId);

      PaymentProtoResponse paymentProtoResponse = PaymentProtoResponse.builder()
          .paymentPublishments(payments)
          .build();

      responseObserver.onNext(paymentProtoResponse.toListCardPaymentResponseProto());
      responseObserver.onCompleted();

    } catch (GrpcException e) {
      log.error(makeLogFormat("listCardPayments", UNKNOWN_ERROR_MESSAGE), e.getMessage(), e);
      responseObserver.onError(e.handle());

    } catch (Exception e ) {
      log.error(makeLogFormat("listCardPayments", UNKNOWN_ERROR_MESSAGE), e.getMessage(), e);
      responseObserver.onError(e);
    }
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
    try {
      long banksaladUserId = Long.parseLong(request.getBanksaladUserId());
      String organizationId = collectmydataConnectClientService
          .getOrganizationByOrganizationGuid(request.getOrganizationGuid()).getOrganizationId();

      List<LoanSummaryPublishment> cardLoanSummaries = userBasePublishService
          .getCardLoanSummaries(banksaladUserId, organizationId);

      CardLoanSummaryProtoResponse cardLoanSummaryProtoResponse = CardLoanSummaryProtoResponse.builder()
          .loanSummaryPublishments(cardLoanSummaries)
          .build();

      responseObserver.onNext(cardLoanSummaryProtoResponse.toListCardLoanSummariesResponseProto());
      responseObserver.onCompleted();

    } catch (GrpcException e) {
      log.error(makeLogFormat("listCardLoanSummaries", UNKNOWN_ERROR_MESSAGE), e.getMessage(), e);
      responseObserver.onError(e.handle());

    } catch (Exception e ) {
      log.error(makeLogFormat("listCardLoanSummaries", UNKNOWN_ERROR_MESSAGE), e.getMessage(), e);
      responseObserver.onError(e);
    }
  }

  @Override
  public void listCardRevolvings(ListCardRevolvingsRequest request,
      StreamObserver<ListCardRevolvingsResponse> responseObserver) {
    try {
      long banksaladUserId = Long.parseLong(request.getBanksaladUserId());
      String organizationId = collectmydataConnectClientService
          .getOrganizationByOrganizationGuid(request.getOrganizationGuid()).getOrganizationId();

      List<RevolvingPublishment> cardRevolvingsResponse = userBasePublishService
          .getCardRevolvingsResponse(banksaladUserId, organizationId);

      CardRevolvingProtoResponse cardRevolvingProtoResponse = CardRevolvingProtoResponse.builder()
          .revolvingPublishments(cardRevolvingsResponse)
          .build();

      responseObserver.onNext(cardRevolvingProtoResponse.toListCardRevolvingResponse());
      responseObserver.onCompleted();

    } catch (GrpcException e) {
      log.error(makeLogFormat("listCardRevolvings", UNKNOWN_ERROR_MESSAGE), e.getMessage(), e);
      responseObserver.onError(e.handle());

    } catch (Exception e ) {
      log.error(makeLogFormat("listCardRevolvings", UNKNOWN_ERROR_MESSAGE), e.getMessage(), e);
      responseObserver.onError(e);
    }
  }

  @Override
  public void listCardLoanShortTerms(ListCardLoanShortTermsRequest request,
      StreamObserver<ListCardLoanShortTermsResponse> responseObserver) {
    try {
      long banksaladUserId = Long.parseLong(request.getBanksaladUserId());
      String organizationId = collectmydataConnectClientService
          .getOrganizationByOrganizationGuid(request.getOrganizationGuid()).getOrganizationId();

      List<LoanShortTermPublishment> cardLoanShortTermsResponse = userBasePublishService
          .getCardLoanShortTermsResponse(banksaladUserId, organizationId);

      CardLoanShortTermProtoResponse cardLoanShortTermProtoResponse = CardLoanShortTermProtoResponse.builder()
          .loanShortTermPublishments(cardLoanShortTermsResponse)
          .build();

      responseObserver.onNext(cardLoanShortTermProtoResponse.toListCardLoanShortTermsResponseProto());
      responseObserver.onCompleted();

    } catch (GrpcException e) {
      log.error(makeLogFormat("listCardLoanShortTerms", UNKNOWN_ERROR_MESSAGE), e.getMessage(), e);
      responseObserver.onError(e.handle());

    } catch (Exception e ) {
      log.error(makeLogFormat("listCardLoanShortTerms", UNKNOWN_ERROR_MESSAGE), e.getMessage(), e);
      responseObserver.onError(e);
    }
  }

  @Override
  public void listCardLoanLongTerms(ListCardLoanLongTermsRequest request,
      StreamObserver<ListCardLoanLongTermsResponse> responseObserver) {
    try {
      long banksaladUserId = Long.parseLong(request.getBanksaladUserId());
      String organizationId = collectmydataConnectClientService
          .getOrganizationByOrganizationGuid(request.getOrganizationGuid()).getOrganizationId();

      List<LoanLongTermPublishment> cardLoanLongTermsResponse = userBasePublishService
          .getCardLoanLongTermsResponse(banksaladUserId, organizationId);

      CardLoanLongTermProtoResponse cardLoanLongTermProtoResponse = CardLoanLongTermProtoResponse.builder()
          .loanLongTermPublishments(cardLoanLongTermsResponse)
          .build();

      responseObserver.onNext(cardLoanLongTermProtoResponse.toListCardLoanLongTermsResponseProto());
      responseObserver.onCompleted();

    } catch (GrpcException e) {
      log.error(makeLogFormat("listCardLoanLongTerms", UNKNOWN_ERROR_MESSAGE), e.getMessage(), e);
      responseObserver.onError(e.handle());

    } catch (Exception e ) {
      log.error(makeLogFormat("listCardLoanLongTerms", UNKNOWN_ERROR_MESSAGE), e.getMessage(), e);
      responseObserver.onError(e);
    }
  }
}
