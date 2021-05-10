package com.banksalad.collectmydata.card.publishment.transaction.dto;

import com.banksalad.collectmydata.common.util.DateUtil;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.CardApprovalOversea;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.ListCardApprovalOverseasResponse;
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
public class CardApprovalOverseaProtoResponse {

  List<ApprovalOverseasPublishment> approvalOverseasPublishments;

  public ListCardApprovalOverseasResponse toListCardApprovalOverseasResponseProto() {

    List<CardApprovalOversea> cardApprovalOverseasProtos = approvalOverseasPublishments.stream()
        .map(approvalOverseasPublishment -> CardApprovalOversea.newBuilder()
            .setCardId(approvalOverseasPublishment.getCardId())
            .setApprovedNum(approvalOverseasPublishment.getApprovedNum())
            .setStatus(approvalOverseasPublishment.getStatus())
            .setPayType(approvalOverseasPublishment.getPayType())
            .setApprovedDtime(approvalOverseasPublishment.getApprovedNum())
            .setCancelDtime(StringValue.of(approvalOverseasPublishment.getCancelDtime()))
            .setMerchantName(approvalOverseasPublishment.getMerchantName())
            .setApprovedAmt3F(toInt64ValueMultiply1000(approvalOverseasPublishment.getApprovedAmt()).getValue())
            .setCountryCode(approvalOverseasPublishment.getCountryCode())
            .setKrwAmt3F(toInt64ValueMultiply1000(approvalOverseasPublishment.getKrwAmt()))
            .setCreatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(approvalOverseasPublishment.getCreatedAt()))
            .setUpdatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(approvalOverseasPublishment.getUpdatedAt())).build())
        .collect(
            Collectors.toList());

    return ListCardApprovalOverseasResponse.newBuilder()
        .addAllCardApprovalOverseas(cardApprovalOverseasProtos).build();
  }
}
