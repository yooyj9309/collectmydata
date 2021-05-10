package com.banksalad.collectmydata.card.publishment.userbase;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.card.card.dto.Payment;
import com.banksalad.collectmydata.card.card.dto.Point;
import com.banksalad.collectmydata.card.card.dto.Revolving;
import com.banksalad.collectmydata.card.common.db.repository.LoanLongTermRepository;
import com.banksalad.collectmydata.card.common.db.repository.LoanShortTermRepository;
import com.banksalad.collectmydata.card.common.db.repository.LoanSummaryRepository;
import com.banksalad.collectmydata.card.common.db.repository.PaymentRepository;
import com.banksalad.collectmydata.card.common.db.repository.PointRepository;
import com.banksalad.collectmydata.card.common.db.repository.RevolvingRepository;
import com.banksalad.collectmydata.card.common.mapper.LoanLongTermMapper;
import com.banksalad.collectmydata.card.common.mapper.LoanShortTermMapper;
import com.banksalad.collectmydata.card.common.mapper.LoanSummaryMapper;
import com.banksalad.collectmydata.card.common.mapper.PaymentMapper;
import com.banksalad.collectmydata.card.common.mapper.PointMapper;
import com.banksalad.collectmydata.card.common.mapper.RevolvingMapper;
import com.banksalad.collectmydata.card.loan.dto.LoanLongTerm;
import com.banksalad.collectmydata.card.loan.dto.LoanShortTerm;
import com.banksalad.collectmydata.card.loan.dto.LoanSummary;
import com.banksalad.collectmydata.card.publishment.userbase.dto.LoanLongTermPublishment;
import com.banksalad.collectmydata.card.publishment.userbase.dto.LoanShortTermPublishment;
import com.banksalad.collectmydata.card.publishment.userbase.dto.LoanSummaryPublishment;
import com.banksalad.collectmydata.card.publishment.userbase.dto.PaymentPublishment;
import com.banksalad.collectmydata.card.publishment.userbase.dto.PointPublishment;
import com.banksalad.collectmydata.card.publishment.userbase.dto.RevolvingPublishment;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserBasePublishServiceImpl implements UserBasePublishService {

  private final PointRepository pointRepository;
  private final PointMapper pointMapper = Mappers.getMapper(PointMapper.class);

  private final PaymentRepository paymentRepository;
  private final PaymentMapper paymentMapper = Mappers.getMapper(PaymentMapper.class);

  private final LoanSummaryRepository loanSummaryRepository;
  private final LoanSummaryMapper loanSummaryMapper = Mappers.getMapper(LoanSummaryMapper.class);

  private final RevolvingRepository revolvingRepository;
  private final RevolvingMapper revolvingMapper = Mappers.getMapper(RevolvingMapper.class);

  private final LoanShortTermRepository loanShortTermRepository;
  private final LoanShortTermMapper loanShortTermMapper = Mappers.getMapper(LoanShortTermMapper.class);

  private final LoanLongTermRepository loanLongTermRepository;
  private final LoanLongTermMapper loanLongTermMapper = Mappers.getMapper(LoanLongTermMapper.class);

  @Override
  public List<PointPublishment> getCardPointResponses(long banksaladUserId, String organizationId) {
    return pointRepository.findAllByBanksaladUserIdAndOrganizationId(banksaladUserId, organizationId)
        .stream().map(pointMapper::entityToPublishmentDto).collect(
            Collectors.toList());
  }

  @Override
  public List<PaymentPublishment> getPaymentsResponses(long banksaladUserId, String organizationId) {
    return paymentRepository.findAllByBanksaladUserIdAndOrganizationId(banksaladUserId, organizationId).stream()
        .map(paymentMapper::entityToPublishmentDto).collect(Collectors.toList());
  }

  @Override
  public List<LoanSummaryPublishment> getCardLoanSummaries(long banksaladUserId, String organizationId) {
    return loanSummaryRepository.findAllByBanksaladUserIdAndOrganizationId(banksaladUserId, organizationId).stream()
        .map(loanSummaryMapper::entityToPublishmentDto)
        .collect(Collectors.toList());
  }

  @Override
  public List<RevolvingPublishment> getCardRevolvingsResponse(long banksaladUserId, String organizationId) {
    return revolvingRepository.findByBanksaladUserIdAndOrganizationId(banksaladUserId, organizationId)
        .stream().map(revolvingMapper::entityToPublishmentDto).collect(Collectors.toList());
  }

  @Override
  public List<LoanShortTermPublishment> getCardLoanShortTermsResponse(long banksaladUserId, String organizationId) {
    return loanShortTermRepository.findByBanksaladUserIdAndOrganizationId(banksaladUserId, organizationId).stream()
        .map(loanShortTermMapper::entityToPublishmentDto).collect(
            Collectors.toList());
  }

  @Override
  public List<LoanLongTermPublishment> getCardLoanLongTermsResponse(long banksaladUserId, String organizationId) {
    return loanLongTermRepository.findByBanksaladUserIdAndOrganizationId(banksaladUserId, organizationId).stream()
        .map(loanLongTermMapper::entityToPublishmentDto).collect(
            Collectors.toList());
  }
}
