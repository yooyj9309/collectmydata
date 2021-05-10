package com.banksalad.collectmydata.card.publishment.accountinfo;

import com.banksalad.collectmydata.card.card.dto.CardBasic;
import com.banksalad.collectmydata.card.publishment.accountinfo.dto.CardBasicPublishment;

import java.util.List;

public interface CardBasicPublishService {

  List<CardBasicPublishment> getCardBasicResponses(long banksaladUserId, String organzationId);

}
