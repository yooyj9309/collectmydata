package com.banksalad.collectmydata.bank.publishment.loan;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.bank.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.bank.common.db.entity.LoanAccountTransactionEntity;
import com.banksalad.collectmydata.bank.common.db.entity.LoanAccountTransactionInterestEntity;
import com.banksalad.collectmydata.bank.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.bank.common.db.repository.LoanAccountBasicRepository;
import com.banksalad.collectmydata.bank.common.db.repository.LoanAccountDetailRepository;
import com.banksalad.collectmydata.bank.common.db.repository.LoanAccountTransactionInterestRepository;
import com.banksalad.collectmydata.bank.common.db.repository.LoanAccountTransactionRepository;
import com.banksalad.collectmydata.bank.common.mapper.LoanAccountBasicMapper;
import com.banksalad.collectmydata.bank.common.mapper.LoanAccountDetailMapper;
import com.banksalad.collectmydata.bank.common.mapper.LoanAccountTransactionInterestMapper;
import com.banksalad.collectmydata.bank.common.mapper.LoanAccountTransactionMapper;
import com.banksalad.collectmydata.bank.publishment.loan.dto.LoanAccountBasicResponse;
import com.banksalad.collectmydata.bank.publishment.loan.dto.LoanAccountDetailResponse;
import com.banksalad.collectmydata.bank.publishment.loan.dto.LoanAccountTransactionInterest;
import com.banksalad.collectmydata.bank.publishment.loan.dto.LoanAccountTransactionResponse;
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
public class LoanAccountPublishServiceImpl implements LoanAccountPublishService {

  private final AccountSummaryRepository accountSummaryRepository;
  private final LoanAccountBasicRepository loanAccountBasicRepository;
  private final LoanAccountDetailRepository loanAccountDetailRepository;
  private final LoanAccountTransactionRepository loanAccountTransactionRepository;
  private final LoanAccountTransactionInterestRepository loanAccountTransactionInterestRepository;

  private final LoanAccountBasicMapper loanAccountBasicMapper = Mappers.getMapper(LoanAccountBasicMapper.class);
  private final LoanAccountDetailMapper loanAccountDetailMapper = Mappers.getMapper(LoanAccountDetailMapper.class);
  private final LoanAccountTransactionMapper loanAccountTransactionMapper = Mappers
      .getMapper(LoanAccountTransactionMapper.class);
  private final LoanAccountTransactionInterestMapper loanAccountTransactionInterestMapper = Mappers
      .getMapper(LoanAccountTransactionInterestMapper.class);

  @Override
  public List<LoanAccountBasicResponse> getLoanAccountBasicResponses(long banksaladUserId, String organizationId) {
    /* load summary entities (is_consent = true & response_code != 40305, 40404) */
    List<AccountSummaryEntity> accountSummaryEntities = accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndBasicResponseCodeNotInAndConsentIsTrue(
            banksaladUserId, organizationId, INVALID_RESPONSE_CODE);

    /* load basic entity and mapping to dto */
    List<LoanAccountBasicResponse> loanAccountBasicResponses = new ArrayList<>();
    for (AccountSummaryEntity accountSummaryEntity : accountSummaryEntities) {
      loanAccountBasicRepository
          .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
              banksaladUserId, organizationId, accountSummaryEntity.getAccountNum(), accountSummaryEntity.getSeqno())
          .ifPresent(loanAccountBasicEntity -> loanAccountBasicResponses
              .add(loanAccountBasicMapper.entityToResponseDto(loanAccountBasicEntity)));
    }
    return loanAccountBasicResponses;
  }

  @Override
  public List<LoanAccountDetailResponse> getLoanAccountDetailResponses(long banksaladUserId, String organizationId) {
    /* load summary entities (is_consent = true & response_code != 40305, 40404) */
    List<AccountSummaryEntity> accountSummaryEntities = accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndDetailResponseCodeNotInAndConsentIsTrue(
            banksaladUserId, organizationId, INVALID_RESPONSE_CODE);

    /* load detail entity and mapping to dto */
    List<LoanAccountDetailResponse> loanAccountDetailResponses = new ArrayList<>();
    for (AccountSummaryEntity accountSummaryEntity : accountSummaryEntities) {
      loanAccountDetailRepository
          .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
              banksaladUserId, organizationId, accountSummaryEntity.getAccountNum(), accountSummaryEntity.getSeqno())
          .ifPresent(loanAccountDetailEntity -> loanAccountDetailResponses
              .add(loanAccountDetailMapper.entityToResponseDto(loanAccountDetailEntity)));
    }
    return loanAccountDetailResponses;
  }

  @Override
  public List<LoanAccountTransactionResponse> getLoanAccountTransactionResponse(long banksaladUserId,
      String organizationId, String accountNum, String seqno, LocalDateTime createdAt, int limit) {
    /* load summary entities (is_consent = true & response_code != 40305, 40404) */
    AccountSummaryEntity accountSummaryEntity = accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndTransactionResponseCodeNotInAndConsentIsTrue(
            banksaladUserId, organizationId, accountNum, seqno, INVALID_RESPONSE_CODE)
        .orElse(null);

    if (accountSummaryEntity == null) {
      return Collections.emptyList();
    }

    /* load transaction entities */
    Page<LoanAccountTransactionEntity> loanAccountTransactionEntities = loanAccountTransactionRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndCreatedAtAfter(
            banksaladUserId, organizationId, accountNum, seqno, createdAt, PageRequest.of(0, limit));

    /* load interest entities and mapping to dto */
    List<LoanAccountTransactionResponse> loanAccountTransactionResponses = new ArrayList<>();
    for (LoanAccountTransactionEntity loanAccountTransactionEntity : loanAccountTransactionEntities) {
      List<LoanAccountTransactionInterestEntity> loanAccountTransactionInterestEntities = loanAccountTransactionInterestRepository
          .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndUniqueTransNo(
              banksaladUserId, organizationId, accountNum, loanAccountTransactionEntity.getUniqueTransNo());

      List<LoanAccountTransactionInterest> loanAccountTransactionInterests = loanAccountTransactionInterestEntities
          .stream()
          .map(loanAccountTransactionInterestEntity ->
              loanAccountTransactionInterestMapper.entityToResponseDto(loanAccountTransactionInterestEntity))
          .collect(Collectors.toList());

      loanAccountTransactionResponses.add(loanAccountTransactionMapper
          .entityToResponseDto(loanAccountTransactionEntity, loanAccountTransactionInterests));
    }
    return loanAccountTransactionResponses;
  }
}
