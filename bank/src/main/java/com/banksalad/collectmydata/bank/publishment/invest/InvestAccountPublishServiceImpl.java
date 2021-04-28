package com.banksalad.collectmydata.bank.publishment.invest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.bank.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.bank.common.db.entity.InvestAccountTransactionEntity;
import com.banksalad.collectmydata.bank.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.bank.common.db.repository.InvestAccountBasicRepository;
import com.banksalad.collectmydata.bank.common.db.repository.InvestAccountDetailRepository;
import com.banksalad.collectmydata.bank.common.db.repository.InvestAccountTransactionRepository;
import com.banksalad.collectmydata.bank.common.mapper.InvestAccountBasicMapper;
import com.banksalad.collectmydata.bank.common.mapper.InvestAccountDetailMapper;
import com.banksalad.collectmydata.bank.common.mapper.InvestAccountTransactionMapper;
import com.banksalad.collectmydata.bank.publishment.invest.dto.InvestAccountBasicResponse;
import com.banksalad.collectmydata.bank.publishment.invest.dto.InvestAccountDetailResponse;
import com.banksalad.collectmydata.bank.publishment.invest.dto.InvestAccountTransactionResponse;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.INVALID_RESPONSE_CODE;

@Service
@RequiredArgsConstructor
public class InvestAccountPublishServiceImpl implements InvestAccountPublishService {

  private final AccountSummaryRepository accountSummaryRepository;
  private final InvestAccountBasicRepository investAccountBasicRepository;
  private final InvestAccountDetailRepository investAccountDetailRepository;
  private final InvestAccountTransactionRepository investAccountTransactionRepository;

  private final InvestAccountBasicMapper investAccountBasicMapper = Mappers.getMapper(InvestAccountBasicMapper.class);
  private final InvestAccountDetailMapper investAccountDetailMapper = Mappers
      .getMapper(InvestAccountDetailMapper.class);
  private final InvestAccountTransactionMapper investAccountTransactionMapper = Mappers
      .getMapper(InvestAccountTransactionMapper.class);

  @Override
  public List<InvestAccountBasicResponse> getInvestAccountBasicResponses(long banksaladUserId, String organizationId) {
    /* load summary entities (is_consent = true & response_code != 40305, 40404) */
    List<AccountSummaryEntity> accountSummaryEntities = accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndBasicResponseCodeNotInAndConsentIsTrue(
            banksaladUserId, organizationId, INVALID_RESPONSE_CODE);

    /* load basic entity and mapping to dto */
    List<InvestAccountBasicResponse> investAccountBasicResponses = new ArrayList<>();
    for (AccountSummaryEntity accountSummaryEntity : accountSummaryEntities) {
      investAccountBasicRepository
          .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
              banksaladUserId, organizationId, accountSummaryEntity.getAccountNum(), accountSummaryEntity.getSeqno())
          .ifPresent(investAccountBasicEntity -> investAccountBasicResponses
              .add(investAccountBasicMapper.entityToResponseDto(investAccountBasicEntity)));
    }
    return investAccountBasicResponses;
  }

  @Override
  public List<InvestAccountDetailResponse> getInvestAccountDetailResponses(long banksaladUserId,
      String organizationId) {
    /* load summary entities (is_consent = true & response_code != 40305, 40404) */
    List<AccountSummaryEntity> accountSummaryEntities = accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndBasicResponseCodeNotInAndConsentIsTrue(
            banksaladUserId, organizationId, INVALID_RESPONSE_CODE);

    /* load detail entity and mapping to dto */
    List<InvestAccountDetailResponse> investAccountDetailResponses = new ArrayList<>();
    for (AccountSummaryEntity accountSummaryEntity : accountSummaryEntities) {
      investAccountDetailRepository
          .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
              banksaladUserId, organizationId, accountSummaryEntity.getAccountNum(), accountSummaryEntity.getSeqno())
          .ifPresent(investAccountDetailEntity -> investAccountDetailResponses
              .add(investAccountDetailMapper.entityToResponseDto(investAccountDetailEntity)));
    }
    return investAccountDetailResponses;
  }

  @Override
  public List<InvestAccountTransactionResponse> getInvestAccountTransactionResponses(long banksaladUserId,
      String organizationId, String accountNum, String seqno, LocalDateTime createdAt, int limit) {
    /* load summary entities (is_consent = true & response_code != 40305, 40404) */
    AccountSummaryEntity accountSummaryEntity = accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndTransactionResponseCodeNotInAndConsentIsTrue(
            banksaladUserId, organizationId, accountNum, seqno, INVALID_RESPONSE_CODE)
        .orElse(null);

    if (accountSummaryEntity == null) {
      return Collections.emptyList();
    }

    /* load transaction entities and mapping to dto */
    return investAccountTransactionRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndCreatedAtAfter(
            banksaladUserId, organizationId, accountNum, seqno, createdAt, PageRequest.of(0, limit))
        .stream()
        .map(investAccountTransactionEntity ->
            investAccountTransactionMapper.entityToResponseDto(investAccountTransactionEntity))
        .collect(Collectors.toList());
  }
}
