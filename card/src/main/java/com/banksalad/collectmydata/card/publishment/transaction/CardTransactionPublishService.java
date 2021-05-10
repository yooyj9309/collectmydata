package com.banksalad.collectmydata.card.publishment.transaction;

import com.banksalad.collectmydata.card.card.dto.ApprovalDomestic;
import com.banksalad.collectmydata.card.card.dto.ApprovalOverseas;
import com.banksalad.collectmydata.card.publishment.transaction.dto.ApprovalDomesticPublishment;
import com.banksalad.collectmydata.card.publishment.transaction.dto.ApprovalOverseasPublishment;

import java.time.LocalDateTime;
import java.util.List;

public interface CardTransactionPublishService {

  List<ApprovalDomesticPublishment> getCardApprovalDomesticResponse(long banksaladUserId, String organizationId, String cardId,
      LocalDateTime createdAfterMs, int limit);

  List<ApprovalOverseasPublishment> getCardApprovalOverseasResponses(long banksaladUserId, String organizationId, String cardId,
      LocalDateTime createdAt, int limit);
}
