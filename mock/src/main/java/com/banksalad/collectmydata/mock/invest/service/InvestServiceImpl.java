package com.banksalad.collectmydata.mock.invest.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.mock.common.config.TransDtimeFormatter;
import com.banksalad.collectmydata.mock.common.db.entity.InvestAccountTransactionEntity;
import com.banksalad.collectmydata.mock.common.db.repository.InvestAccountBasicRepository;
import com.banksalad.collectmydata.mock.common.db.repository.InvestAccountSummaryRepository;
import com.banksalad.collectmydata.mock.common.db.repository.InvestAccountTransactionRepository;
import com.banksalad.collectmydata.mock.common.db.repository.InvestOrganizationUserRepository;
import com.banksalad.collectmydata.mock.common.exception.CollectmydataMockRuntimeException;
import com.banksalad.collectmydata.mock.common.exception.code.CollectmydataMockExceptionCode;
import com.banksalad.collectmydata.mock.invest.dto.InvestAccountBasic;
import com.banksalad.collectmydata.mock.invest.dto.InvestAccountBasicSearch;
import com.banksalad.collectmydata.mock.invest.dto.InvestAccountSummary;
import com.banksalad.collectmydata.mock.invest.dto.InvestAccountSummarySearch;
import com.banksalad.collectmydata.mock.invest.dto.InvestAccountTransactionPage;
import com.banksalad.collectmydata.mock.invest.dto.InvestAccountTransactionPageSearch;
import com.banksalad.collectmydata.mock.invest.service.mapper.InvestAccountBasicMapper;
import com.banksalad.collectmydata.mock.invest.service.mapper.InvestAccountSummaryMapper;
import com.banksalad.collectmydata.mock.invest.service.mapper.InvestAccountTransactionMapper;
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
  private final InvestAccountTransactionRepository investAccountTransactionRepository;

  private final InvestAccountSummaryMapper investAccountSummaryMapper = Mappers
      .getMapper(InvestAccountSummaryMapper.class);
  private final InvestAccountBasicMapper investAccountBasicMapper = Mappers.getMapper(InvestAccountBasicMapper.class);
  private final InvestAccountTransactionMapper investAccountTransactionMapper = Mappers
      .getMapper(InvestAccountTransactionMapper.class);

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

  @Override
  public InvestAccountTransactionPage getInvestAccountTransactionPage(
      InvestAccountTransactionPageSearch investAccountTransactionPageSearch) {

    int pageNumber = investAccountTransactionPageSearch.getPageNumber();
    int pageSize = investAccountTransactionPageSearch.getPageSize();
    Sort sort = Sort.by("transDtime", "transType", "transAmt").ascending();
    Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

    Page<InvestAccountTransactionEntity> investAccountTransactionEntityPage = investAccountTransactionRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndTransDtimeBetween(
            investAccountTransactionPageSearch.getBanksaladUserId(),
            investAccountTransactionPageSearch.getOrgCode(),
            investAccountTransactionPageSearch.getAccountNum(),
            investAccountTransactionPageSearch.getFromDate().format(TransDtimeFormatter.get()),
            investAccountTransactionPageSearch.getToDate().format(TransDtimeFormatter.get()),
            pageable
        );
    return InvestAccountTransactionPage.builder()
        .totalElements((int) investAccountTransactionEntityPage.getTotalElements())
        .isFirst(investAccountTransactionEntityPage.isFirst())
        .isLast(investAccountTransactionEntityPage.isLast())
        .totalPages(investAccountTransactionEntityPage.getTotalPages())
        .pageSize(investAccountTransactionEntityPage.getSize())
        .pageNumber(investAccountTransactionEntityPage.getNumber())
        .numberOfElements(investAccountTransactionEntityPage.getNumberOfElements())
        .investAccountTransaction(
            investAccountTransactionEntityPage.getContent().stream()
                .map(investAccountTransactionMapper::entityToDto).collect(Collectors.toList()))
        .build();
  }
}
