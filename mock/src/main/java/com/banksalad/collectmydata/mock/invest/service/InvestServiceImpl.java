package com.banksalad.collectmydata.mock.invest.service;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.mock.common.db.repository.InvestAccountBasicRepository;
import com.banksalad.collectmydata.mock.common.db.repository.InvestAccountSummaryRepository;
import com.banksalad.collectmydata.mock.common.db.repository.InvestOrganizationUserRepository;
import com.banksalad.collectmydata.mock.common.exception.CollectmydataMockRuntimeException;
import com.banksalad.collectmydata.mock.common.exception.code.CollectmydataMockExceptionCode;
import com.banksalad.collectmydata.mock.invest.dto.InvestAccountBasic;
import com.banksalad.collectmydata.mock.invest.dto.InvestAccountBasicSearch;
import com.banksalad.collectmydata.mock.invest.dto.InvestAccountSummary;
import com.banksalad.collectmydata.mock.invest.dto.InvestAccountSummarySearch;
import com.banksalad.collectmydata.mock.invest.service.mapper.InvestAccountBasicMapper;
import com.banksalad.collectmydata.mock.invest.service.mapper.InvestAccountSummaryMapper;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvestServiceImpl implements InvestService {

  private final InvestOrganizationUserRepository investOrganizationUserRepository;
  private final InvestAccountSummaryRepository investAccountSummaryRepository;
  private final InvestAccountBasicRepository investAccountBasicRepository;

  private final InvestAccountSummaryMapper investAccountSummaryMapper = Mappers
      .getMapper(InvestAccountSummaryMapper.class);
  private final InvestAccountBasicMapper investAccountBasicMapper = Mappers.getMapper(InvestAccountBasicMapper.class);

  @Override
  public String getRegistrationDate(InvestAccountSummarySearch investAccountSummarySearch) {
    return investOrganizationUserRepository
        .findByBanksaladUserIdAndOrganizationId(
            investAccountSummarySearch.getBanksaladUserId(),
            investAccountSummarySearch.getOrganizationId())
        .orElseThrow(() -> new CollectmydataMockRuntimeException(CollectmydataMockExceptionCode.NOT_FOUND_ASSETS))
        .getRegDate();
  }

  @Override
  public List<InvestAccountSummary> getInvestAccountList(InvestAccountSummarySearch investAccountSummarySearch) {
    return investAccountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndUpdatedAtGreaterThan(
            investAccountSummarySearch.getBanksaladUserId(),
            investAccountSummarySearch.getOrganizationId(),
            investAccountSummarySearch.getUpdatedAt())
        .stream()
        .map(investAccountSummaryMapper::entityToDto)
        .collect(Collectors.toList());
  }

  @Override
  public InvestAccountBasic getInvestAccountBasic(InvestAccountBasicSearch investAccountBasicSearch) {
    return investAccountBasicRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNum(
            investAccountBasicSearch.getBanksaladUserId(),
            investAccountBasicSearch.getOrganizationId(),
            investAccountBasicSearch.getAccountNum()
        ).map(investAccountBasicMapper::entityToDto)
        .orElseThrow(() -> new CollectmydataMockRuntimeException(CollectmydataMockExceptionCode.NOT_FOUND_ASSETS));
  }
}
