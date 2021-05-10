package com.banksalad.collectmydata.card.publishment.userbase.dto;

import com.banksalad.collectmydata.common.util.DateUtil;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.CardPayment;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.ListCardPaymentsResponse;
import com.google.protobuf.StringValue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import static com.banksalad.collectmydata.common.grpc.converter.ProtoTypeConverter.toInt64ValueMultiply1000;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentProtoResponse {

  List<PaymentPublishment> paymentPublishments;

  public ListCardPaymentsResponse toListCardPaymentResponseProto() {

    List<CardPayment> paymentsProtos = paymentPublishments.stream().map(paymentPublishment -> CardPayment.newBuilder()
        .setSeqno(paymentPublishment.getSeqno() != null ? StringValue.of(paymentPublishment.getSeqno()) : null)
        .setPayDueDate(paymentPublishment.getPayDueDate())
        .setPayAmt3F(toInt64ValueMultiply1000(paymentPublishment.getPayAmt()).getValue())
        .setCreatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(paymentPublishment.getCreatedAt()))
        .setUpdatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(paymentPublishment.getUpdatedAt())).build())
        .collect(Collectors.toList());

    return ListCardPaymentsResponse.newBuilder()
        .addAllCardPayments(paymentsProtos).build();
  }
}
