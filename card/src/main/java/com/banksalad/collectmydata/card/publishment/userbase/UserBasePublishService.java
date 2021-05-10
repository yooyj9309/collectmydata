package com.banksalad.collectmydata.card.publishment.userbase;

import com.banksalad.collectmydata.card.card.dto.Payment;
import com.banksalad.collectmydata.card.card.dto.Point;
import com.banksalad.collectmydata.card.card.dto.Revolving;
import com.banksalad.collectmydata.card.loan.dto.LoanLongTerm;
import com.banksalad.collectmydata.card.loan.dto.LoanShortTerm;
import com.banksalad.collectmydata.card.loan.dto.LoanSummary;
import com.banksalad.collectmydata.card.publishment.userbase.dto.LoanLongTermPublishment;
import com.banksalad.collectmydata.card.publishment.userbase.dto.LoanShortTermPublishment;
import com.banksalad.collectmydata.card.publishment.userbase.dto.LoanSummaryPublishment;
import com.banksalad.collectmydata.card.publishment.userbase.dto.PaymentPublishment;
import com.banksalad.collectmydata.card.publishment.userbase.dto.PointPublishment;
import com.banksalad.collectmydata.card.publishment.userbase.dto.RevolvingPublishment;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatacardProto.CardPoint;

import java.util.List;

public interface UserBasePublishService {

  List<PointPublishment> getCardPointResponses(long banksaladUserId, String organizationId);

  List<PaymentPublishment> getPaymentsResponses(long banksaladUserId, String organizationId);

  List<LoanSummaryPublishment> getCardLoanSummaries(long banksaladUserId, String organizationId);

  List<RevolvingPublishment> getCardRevolvingsResponse(long banksaladUserId, String organizationId);

  List<LoanShortTermPublishment> getCardLoanShortTermsResponse(long banksaladUserId, String organizationId);

  List<LoanLongTermPublishment> getCardLoanLongTermsResponse(long banksaladUserId, String organizationId);
}
