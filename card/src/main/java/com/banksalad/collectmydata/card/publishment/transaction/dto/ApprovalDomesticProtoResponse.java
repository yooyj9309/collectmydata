package com.banksalad.collectmydata.card.publishment.transaction.dto;

import com.banksalad.collectmydata.common.util.DateUtil;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.CardApprovalDomestic;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.ListCardApprovalDomesticsResponse;
import com.google.protobuf.Int32Value;
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
public class ApprovalDomesticProtoResponse {

  List<ApprovalDomesticPublishment> approvalDomesticPublishments;

  public ListCardApprovalDomesticsResponse toListCardApprovalDomesticsResponseProto() {

    List<CardApprovalDomestic> cardApprovalDomesticProtos = approvalDomesticPublishments.stream()
        .map(approvalDomesticPublishment -> CardApprovalDomestic.newBuilder()
            .setCardId(approvalDomesticPublishment.getCardId())
            .setApprovedNum(approvalDomesticPublishment.getApprovedNum())
            .setStatus(approvalDomesticPublishment.getStatus())
            .setPayType(approvalDomesticPublishment.getPayType())
            .setApprovedDtime(approvalDomesticPublishment.getApprovedDtime())
            .setCancelDtime(StringValue.of(approvalDomesticPublishment.getCancelDtime()))
            .setMerchantName(approvalDomesticPublishment.getMerchantName())
            .setApprovedAmt3F(toInt64ValueMultiply1000(approvalDomesticPublishment.getApprovedAmt()).getValue())
            .setTotalInstallCnt(Int32Value.of(approvalDomesticPublishment.getTotalInstallCnt()))
            .setCreatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(approvalDomesticPublishment.getCreatedAt()))
            .setUpdatedAtMs(DateUtil.utcLocalDateTimeToEpochMilliSecond(approvalDomesticPublishment.getCreatedAt()))
            .build()).collect(Collectors.toList());

    return ListCardApprovalDomesticsResponse.newBuilder()
        .addAllCardApprovalDomestics(cardApprovalDomesticProtos).build();
  }
}
