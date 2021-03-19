package com.banksalad.collectmydata.insu.common.service;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.common.exception.CollectRuntimeException;
import com.banksalad.collectmydata.insu.common.db.entity.LoanSummaryEntity;
import com.banksalad.collectmydata.insu.common.db.repository.LoanSummaryRepository;
import com.banksalad.collectmydata.insu.common.mapper.LoanSummaryMapper;
import com.banksalad.collectmydata.insu.summary.dto.LoanSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanSummaryServiceImpl implements LoanSummaryService {

  private final LoanSummaryRepository loanSummaryRepository;
  private final LoanSummaryMapper loanSummaryMapper = Mappers.getMapper(LoanSummaryMapper.class);

  @Override
  public List<LoanSummary> listLoanSummaries(long banksaladUserId, String organizationId) {
    return loanSummaryRepository.findAllByBanksaladUserIdAndOrganizationId(banksaladUserId, organizationId).stream()
        .map(loanSummaryMapper::entityToDto)
        .collect(Collectors.toList());
  }

  @Override
  public void updateBasicSearchTimestamp(long banksaladUserId, String organizationId, String accountNum,
      long searchTimestamp) {
    LoanSummaryEntity entity = getLoanSummaryEntity(banksaladUserId, organizationId, accountNum);

    entity.setBasicSearchTimestamp(searchTimestamp);
    loanSummaryRepository.save(entity);
  }

  @Override
  public void updateBasicResponseCode(long banksaladUserId, String organizationId, String accountNum, String rspCode) {
    LoanSummaryEntity entity = getLoanSummaryEntity(banksaladUserId, organizationId, accountNum);

    entity.setBasicResponseCode(rspCode);
    loanSummaryRepository.save(entity);
  }

  @Override
  public void updateDetailSearchTimestamp(long banksaladUserId, String organizationId, String accountNum,
      long searchTimestamp) {
    LoanSummaryEntity entity = getLoanSummaryEntity(banksaladUserId, organizationId, accountNum);

    entity.setDetailSearchTimestamp(searchTimestamp);
    loanSummaryRepository.save(entity);
  }

  @Override
  public void updateDetailResponseCode(long banksaladUserId, String organizationId, String accountNum, String rspCode) {
    LoanSummaryEntity entity = getLoanSummaryEntity(banksaladUserId, organizationId, accountNum);

    entity.setDetailResponseCode(rspCode);
    loanSummaryRepository.save(entity);
  }

  @Override
  public void updateTransactionSyncedAt(long banksaladUserId, String organizationId, String accountNum,
      LocalDateTime transactionSyncedAt) {
    LoanSummaryEntity entity = getLoanSummaryEntity(banksaladUserId, organizationId, accountNum);

    entity.setTransactionSyncedAt(transactionSyncedAt);
    loanSummaryRepository.save(entity);
  }

  @Override
  public void updateTransactionResponseCode(long banksaladUserId, String organizationId, String accountNum,
      String rspCode) {
    LoanSummaryEntity entity = getLoanSummaryEntity(banksaladUserId, organizationId, accountNum);

    entity.setTransactionResponseCode(rspCode);
    loanSummaryRepository.save(entity);
  }

  private LoanSummaryEntity getLoanSummaryEntity(long banksaladUserId, String organizationId, String accountNum) {
    return loanSummaryRepository.findByBanksaladUserIdAndOrganizationIdAndAccountNum(
        banksaladUserId,
        organizationId,
        accountNum
    ).orElseThrow(() -> new CollectRuntimeException("No data LoanSummaryEntity"));
  }
}
