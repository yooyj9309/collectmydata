package com.banksalad.collectmydata.card.publishment.summary;

import com.banksalad.collectmydata.card.summary.CardSummaryResponseHelper;
import com.banksalad.collectmydata.card.summary.dto.CardSummary;

import java.util.List;

public interface CardSummaryPublishService {

  List<CardSummary> getCardSummaryResponses(long banksaladUserId, String organizationId);

}
