package com.banksalad.collectmydata.bank.publishment.deposit;

import com.banksalad.collectmydata.bank.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.bank.common.db.entity.DepositAccountTransactionEntity;
import com.banksalad.collectmydata.bank.common.db.repository.AccountSummaryRepository;
import com.banksalad.collectmydata.bank.common.db.repository.DepositAccountBasicRepository;
import com.banksalad.collectmydata.bank.common.db.repository.DepositAccountDetailRepository;
import com.banksalad.collectmydata.bank.common.db.repository.DepositAccountTransactionRepository;
import com.banksalad.collectmydata.bank.common.mapper.DepositAccountBasicMapper;
import com.banksalad.collectmydata.bank.common.mapper.DepositAccountDetailMapper;
import com.banksalad.collectmydata.bank.common.mapper.DepositAccountTransactionMapper;
import com.banksalad.collectmydata.bank.publishment.deposit.dto.DepositAccountBasicResponse;
import com.banksalad.collectmydata.bank.publishment.deposit.dto.DepositAccountDetailResponse;
import com.banksalad.collectmydata.bank.publishment.deposit.dto.DepositAccountTransactionResponse;
import com.banksalad.collectmydata.finance.common.grpc.CollectmydataConnectClientService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.ListBankDepositAccountBasicsRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.ListBankDepositAccountDetailsRequest;
import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydatabankProto.ListBankDepositAccountTransactionsRequest;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.INVALID_RESPONSE_CODE;

@Service
@RequiredArgsConstructor
public class DepositAccountPublishServiceImpl implements DepositAccountPublishService {

  private final AccountSummaryRepository accountSummaryRepository;
  private final DepositAccountBasicRepository depositAccountBasicRepository;
  private final DepositAccountDetailRepository depositAccountDetailRepository;
  private final DepositAccountTransactionRepository depositAccountTransactionRepository;
  private final CollectmydataConnectClientService collectmydataConnectClientService;

  private final DepositAccountBasicMapper depositAccountBasicMapper = Mappers.getMapper(DepositAccountBasicMapper.class);
  private final DepositAccountDetailMapper depositAccountDetailMapper = Mappers.getMapper(DepositAccountDetailMapper.class);
  private final DepositAccountTransactionMapper depositAccountTransactionMapper = Mappers
      .getMapper(DepositAccountTransactionMapper.class);

  @Override
  public List<DepositAccountBasicResponse> getDepositAccountBasicResponses(ListBankDepositAccountBasicsRequest request) {
    /* type casting */
    long banksaladUserId = Long.parseLong(request.getBanksaladUserId());
    String organizationId = collectmydataConnectClientService
        .getOrganizationByOrganizationObjectid(request.getOrganizationObjectid()).getOrganizationId();

    /* load summary entities (is_consent = true & response_code != 40305, 40404) */
    List<AccountSummaryEntity> accountSummaryEntities = accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndBasicResponseCodeNotInAndConsentIsTrue(
            banksaladUserId, organizationId, INVALID_RESPONSE_CODE);

    /* load basic entity and mapping to dto */
    List<DepositAccountBasicResponse> depositAccountBasicResponses = new ArrayList<>();
    for (AccountSummaryEntity accountSummaryEntity : accountSummaryEntities) {
      depositAccountBasicRepository.findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
          banksaladUserId, organizationId, accountSummaryEntity.getAccountNum(), accountSummaryEntity.getSeqno())
          .stream()
          .map(depositAccountBasicEntity -> depositAccountBasicResponses
              .add(depositAccountBasicMapper.entityToResponseDto(depositAccountBasicEntity)))
          .collect(Collectors.toList());
    }
    return depositAccountBasicResponses;
  }

  @Override
  public List<DepositAccountDetailResponse> getDepositAccountDetailResponses(ListBankDepositAccountDetailsRequest request) {
    /* type casting */
    long banksaladUserId = Long.parseLong(request.getBanksaladUserId());
    String organizationId = collectmydataConnectClientService
        .getOrganizationByOrganizationObjectid(request.getOrganizationObjectid()).getOrganizationId();

    /* load summary entities (is_consent = true & response_code != 40305, 40404) */
    List<AccountSummaryEntity> accountSummaryEntities = accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndDetailResponseCodeNotInAndConsentIsTrue(
            banksaladUserId, organizationId, INVALID_RESPONSE_CODE);

    /* load detail entity and mapping to dto */
    List<DepositAccountDetailResponse> depositAccountDetailResponses = new ArrayList<>();
    for (AccountSummaryEntity accountSummaryEntity : accountSummaryEntities) {
      depositAccountDetailRepository
          .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
              banksaladUserId, organizationId, accountSummaryEntity.getAccountNum(), accountSummaryEntity.getSeqno())
          .stream()
          .map(depositAccountDetailEntity -> depositAccountDetailResponses
              .add(depositAccountDetailMapper.entityToResponseDto(depositAccountDetailEntity)))
          .collect(Collectors.toList());
    }
    return depositAccountDetailResponses;
  }

  @Override
  public List<DepositAccountTransactionResponse> getDepositAccountTransactionResponses(
      ListBankDepositAccountTransactionsRequest request) {
    /* type casting */
    long banksaladUserId = Long.parseLong(request.getBanksaladUserId());
    String organizationId = collectmydataConnectClientService
        .getOrganizationByOrganizationObjectid(request.getOrganizationObjectid()).getOrganizationId();
    LocalDateTime createdAt = LocalDateTime.ofEpochSecond(request.getCreatedAfterMs(), 0, ZoneOffset.UTC);
    int limit = Long.valueOf(request.getLimit()).intValue();

    /* load summary entities (is_consent = true & response_code != 40305, 40404) */
    AccountSummaryEntity accountSummaryEntity = accountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndTransactionResponseCodeNotInAndConsentIsTrue(
            banksaladUserId, organizationId, request.getAccountNum(), request.getSeqno().getValue(),
            INVALID_RESPONSE_CODE)
        .orElse(null); // TODO : check if null, response should be empty list? or throw exception?

    /* load transaction entities and mapping to dto */
    List<DepositAccountTransactionResponse> depositAccountTransactionResponses = new ArrayList<>();
    if (accountSummaryEntity != null) {
      Page<DepositAccountTransactionEntity> depositAccountTransactionEntities = depositAccountTransactionRepository
          .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndCreatedAtAfter(
              banksaladUserId, organizationId, request.getAccountNum(), request.getSeqno().getValue(), createdAt,
              PageRequest.of(0, limit));

      depositAccountTransactionResponses = depositAccountTransactionEntities.stream()
          .map(depositAccountTransactionEntity ->
              depositAccountTransactionMapper.entityToResponseDto(depositAccountTransactionEntity))
          .collect(Collectors.toList());
    }
    return depositAccountTransactionResponses;
  }
}
