package com.banksalad.collectmydata.card.publishment.bill;

import com.banksalad.collectmydata.card.card.dto.BillBasic;
import com.banksalad.collectmydata.card.card.dto.BillDetail;
import com.banksalad.collectmydata.card.publishment.bill.dto.BillBasicPublishment;
import com.banksalad.collectmydata.card.publishment.bill.dto.BillDetailPublishment;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.CardBillBasic;

import java.time.LocalDateTime;
import java.util.List;

public interface CardBillPublishService {

  List<BillBasicPublishment> getCardBillBasicResponse(long banksaladUserId, String organizationId, LocalDateTime createdAt, int limit);

  List<BillDetailPublishment> getCardBillDetailResponse(long banksaladUserId, String organizationId, String seqNo, String chargeMonth);
}
