package com.banksalad.collectmydata.card.publishment.bill.dto;

import com.banksalad.collectmydata.common.grpc.converter.ProtoTypeConverter;
import com.banksalad.collectmydata.common.util.DateUtil;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.CardBillDetail;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.ListCardBillDetailsResponse;
import com.google.protobuf.Int32Value;
import com.google.protobuf.Int64Value;
import com.google.protobuf.StringValue;
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
public class CardBillDetailProtoResponse {

  List<BillDetailPublishment> billDetailPublishments;

  public ListCardBillDetailsResponse toListCardBillDetailsResponseProto() {

    List<CardBillDetail> cardBillDetailsProtos = billDetailPublishments.stream().map(billDetailPublishment -> CardBillDetail.newBuilder()
        .setChargeMonth(billDetailPublishment.getChargeMonth().toString())
        .setSeqno(billDetailPublishment.getSeqNo() != null ? StringValue.of(billDetailPublishment.getSeqNo()) : null)
        .setCardId(billDetailPublishment.getCardId())
        .setPaidDtime(billDetailPublishment.getPaidDtime())
        .setCurrencyCode(billDetailPublishment.getCurrencyCode())
        .setMerchantName(billDetailPublishment.getMerchantName())
        .setCreditFeeAmt3F(ProtoTypeConverter.toInt64ValueMultiply1000(billDetailPublishment.getCreditFeeAmt()).getValue())
        .setTotalInstallCnt(Int32Value.of(billDetailPublishment.getTotalInstallCnt()))
        .setCurInstallCnt(Int32Value.of(billDetailPublishment.getCurInstallCnt()))
        .setBalanceAmt3F(Int64Value.of(billDetailPublishment.getBalanceAmt().longValue()))
        .setProdType(billDetailPublishment.getProdType())
        .setCreatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(billDetailPublishment.getCreatedAt()))
        .setUpdatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(billDetailPublishment.getUpdatedAt())).build()).collect(
        Collectors.toList());

    return ListCardBillDetailsResponse.newBuilder()
        .addAllCardBillDetails(cardBillDetailsProtos).build();
  }
}
