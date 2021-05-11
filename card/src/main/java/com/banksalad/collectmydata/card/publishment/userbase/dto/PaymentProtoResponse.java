package com.banksalad.collectmydata.card.publishment.userbase.dto;

import com.banksalad.collectmydata.card.grpc.converter.CardProtoConverter;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.CardPayment;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.ListCardPaymentsResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentProtoResponse {

  List<PaymentPublishment> paymentPublishments;

  public ListCardPaymentsResponse toListCardPaymentResponseProto() {

    List<CardPayment> paymentsProtos = paymentPublishments.stream().map(CardProtoConverter::toCardPaymentProto)
        .collect(Collectors.toList());

    return ListCardPaymentsResponse.newBuilder()
        .addAllCardPayments(paymentsProtos).build();
  }
}
