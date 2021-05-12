package com.banksalad.collectmydata.insu.publishment.loan;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.insu.common.db.repository.LoanBasicRepository;
import com.banksalad.collectmydata.insu.common.db.repository.LoanDetailRepository;
import com.banksalad.collectmydata.insu.common.db.repository.LoanTransactionInterestRepository;
import com.banksalad.collectmydata.insu.common.db.repository.LoanTransactionRepository;
import com.banksalad.collectmydata.insu.common.mapper.LoanBasicMapper;
import com.banksalad.collectmydata.insu.common.mapper.LoanDetailMapper;
import com.banksalad.collectmydata.insu.common.mapper.LoanTransactionInterestMapper;
import com.banksalad.collectmydata.insu.common.mapper.LoanTransactionMapper;
import com.banksalad.collectmydata.insu.publishment.loan.dto.LoanBasicPublishmentResponse;
import com.banksalad.collectmydata.insu.publishment.loan.dto.LoanDetailPublishmentResponse;
import com.banksalad.collectmydata.insu.publishment.loan.dto.LoanTransactionInterestPublishmentResponse;
import com.banksalad.collectmydata.insu.publishment.loan.dto.LoanTransactionPublishmentResponse;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanPublishmentServiceImpl implements LoanPublishmentService {

  private final LoanBasicRepository loanBasicRepository;
  private final LoanDetailRepository loanDetailRepository;
  private final LoanTransactionRepository loanTransactionRepository;
  private final LoanTransactionInterestRepository loanTransactionInterestRepository;

  private final LoanBasicMapper loanBasicMapper = Mappers.getMapper(LoanBasicMapper.class);
  private final LoanDetailMapper loanDetailMapper = Mappers.getMapper(LoanDetailMapper.class);
  private final LoanTransactionMapper loanTransactionMapper = Mappers.getMapper(LoanTransactionMapper.class);
  private final LoanTransactionInterestMapper loanTransactionInterestMapper = Mappers
      .getMapper(LoanTransactionInterestMapper.class);


  @Override
  public List<LoanBasicPublishmentResponse> getLoanBasicResponses(long banksaladUserId, String organizationId) {
    return loanBasicRepository
        .findAllByBanksaladUserIdAndOrganizationId(banksaladUserId, organizationId)
        .stream()
        .map(loanBasicMapper::entityToPublishmentDto)
        .collect(Collectors.toList());
  }

  @Override
  public List<LoanDetailPublishmentResponse> getLoanDetailResponses(long banksaladUserId, String organizationId) {
    return loanDetailRepository
        .findAllByBanksaladUserIdAndOrganizationId(banksaladUserId, organizationId)
        .stream()
        .map(loanDetailMapper::entityToPublishmentDto)
        .collect(Collectors.toList());
  }

  @Override
  public List<LoanTransactionPublishmentResponse> getLoanTransactionResponses(long banksaladUserId,
      String organizationId, String accountNum, LocalDateTime createdAt, int limit) {
    List<LoanTransactionPublishmentResponse> transactionPublishmentResponses = loanTransactionRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndCreatedAtAfter(
            banksaladUserId, organizationId, accountNum, createdAt, PageRequest.of(0, limit))
        .stream()
        .map(loanTransactionMapper::entityToPublishmentDto)
        .collect(Collectors.toList());

    for (LoanTransactionPublishmentResponse transactionPublishmentResponse : transactionPublishmentResponses) {
      List<LoanTransactionInterestPublishmentResponse> interestPublishmentResponses = loanTransactionInterestRepository
          .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndTransDtimeAndTransNoAndTransactionYearMonth(
              banksaladUserId, organizationId, accountNum, transactionPublishmentResponse.getTransDtime(),
              transactionPublishmentResponse.getTransNo(),
              Integer.valueOf(transactionPublishmentResponse.getTransDtime().substring(0, 6)))
          .stream()
          .map(loanTransactionInterestMapper::entityToPublishmentDto)
          .collect(Collectors.toList());

      transactionPublishmentResponse.getInterestPublishmentResponses().addAll(interestPublishmentResponses);
    }

    return transactionPublishmentResponses;
  }
}
