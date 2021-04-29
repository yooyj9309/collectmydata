package com.banksalad.collectmydata.mock.irp.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.mock.common.db.repository.InvestIrpAccountBasicRepository;
import com.banksalad.collectmydata.mock.common.db.repository.InvestIrpAccountDetailRepository;
import com.banksalad.collectmydata.mock.common.db.repository.InvestIrpAccountSummaryRepository;
import com.banksalad.collectmydata.mock.common.db.repository.InvestIrpAccountTransactionRepository;
import com.banksalad.collectmydata.mock.common.exception.CollectmydataMockRuntimeException;
import com.banksalad.collectmydata.mock.common.exception.code.CollectmydataMockExceptionCode;
import com.banksalad.collectmydata.mock.irp.dto.IrpAccountBasic;
import com.banksalad.collectmydata.mock.irp.dto.IrpAccountBasicSearch;
import com.banksalad.collectmydata.mock.irp.dto.IrpAccountDetail;
import com.banksalad.collectmydata.mock.irp.dto.IrpAccountDetailSearch;
import com.banksalad.collectmydata.mock.irp.dto.IrpAccountSummary;
import com.banksalad.collectmydata.mock.irp.dto.IrpAccountSummarySearch;
import com.banksalad.collectmydata.mock.irp.dto.IrpAccountTransaction;
import com.banksalad.collectmydata.mock.irp.dto.IrpAccountTransactionSearch;
import com.banksalad.collectmydata.mock.irp.service.mapper.IrpAccountBasicMapper;
import com.banksalad.collectmydata.mock.irp.service.mapper.IrpAccountDetailMapper;
import com.banksalad.collectmydata.mock.irp.service.mapper.IrpAccountSummaryMapper;
import com.banksalad.collectmydata.mock.irp.service.mapper.IrpAccountTransactionMapper;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Service("investIrpService")
@RequiredArgsConstructor
public class InvestIrpServiceImpl implements IrpService {

  private final InvestIrpAccountSummaryRepository investIrpAccountSummaryRepository;
  private final InvestIrpAccountBasicRepository investIrpAccountBasicRepository;
  private final InvestIrpAccountDetailRepository investIrpAccountDetailRepository;
  private final InvestIrpAccountTransactionRepository investIrpAccountTransactionRepository;

  private final IrpAccountSummaryMapper irpAccountSummaryMapper = Mappers.getMapper(IrpAccountSummaryMapper.class);
  private final IrpAccountBasicMapper irpAccountBasicMapper = Mappers.getMapper(IrpAccountBasicMapper.class);
  private final IrpAccountDetailMapper irpAccountDetailMapper = Mappers.getMapper(IrpAccountDetailMapper.class);
  private final IrpAccountTransactionMapper irpAccountTransactionMapper =
      Mappers.getMapper(IrpAccountTransactionMapper.class);

  @Override
  public List<IrpAccountSummary> getIrpAccountSummaryList(IrpAccountSummarySearch irpAccountSummarySearch) {
    return investIrpAccountSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndUpdatedAtGreaterThan(
            irpAccountSummarySearch.getBanksaladUserId(),
            irpAccountSummarySearch.getOrganizationId(),
            irpAccountSummarySearch.getUpdatedAt())
        .stream()
        .map(irpAccountSummaryMapper::entityToDto)
        .collect(Collectors.toList());
  }

  @Override
  public IrpAccountBasic getIrpAccountBasic(IrpAccountBasicSearch irpAccountBasicSearch) {
    return investIrpAccountBasicRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(irpAccountBasicSearch.getBanksaladUserId(),
            irpAccountBasicSearch.getOrganizationId(),
            irpAccountBasicSearch.getAccountNum(),
            irpAccountBasicSearch.getSeqno())
        .map(irpAccountBasicMapper::entityToDto)
        .orElseThrow(() -> new CollectmydataMockRuntimeException(CollectmydataMockExceptionCode.NOT_FOUND_ASSETS));
  }

  @Override
  public int getIrpAccountDetailCount(IrpAccountDetailSearch irpAccountDetailSearch) {
    return investIrpAccountDetailRepository
        .countByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndUpdatedAtGreaterThan(
            irpAccountDetailSearch.getBanksaladUserId(),
            irpAccountDetailSearch.getOrganizationId(),
            irpAccountDetailSearch.getAccountNum(),
            irpAccountDetailSearch.getSeqno(),
            irpAccountDetailSearch.getUpdatedAt()
        );
  }

  @Override
  public List<IrpAccountDetail> getIrpAccountDetailList(IrpAccountDetailSearch irpAccountDetailSearch) {
    Pageable pageable = PageRequest.of(irpAccountDetailSearch.getPageNumber(),
        irpAccountDetailSearch.getPageSize(), Sort.by("irpName", "openDate").ascending());
    return investIrpAccountDetailRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndUpdatedAtGreaterThan(
            irpAccountDetailSearch.getBanksaladUserId(),
            irpAccountDetailSearch.getOrganizationId(),
            irpAccountDetailSearch.getAccountNum(),
            irpAccountDetailSearch.getSeqno(),
            irpAccountDetailSearch.getUpdatedAt(),
            pageable
        ).stream()
        .map(irpAccountDetailMapper::entityToDto)
        .collect(Collectors.toList());
  }

  @Override
  public int getIrpAccountTransactionCount(IrpAccountTransactionSearch irpAccountTransactionSearch) {
    return investIrpAccountTransactionRepository
        .countByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndUpdatedAtGreaterThanAndCreatedAtBetween(
            irpAccountTransactionSearch.getBanksaladUserId(),
            irpAccountTransactionSearch.getOrganizationId(),
            irpAccountTransactionSearch.getAccountNum(),
            irpAccountTransactionSearch.getSeqno(),
            irpAccountTransactionSearch.getUpdatedAt(),
            irpAccountTransactionSearch.getFromCreatedAt(),
            irpAccountTransactionSearch.getToCreatedAt()
        );
  }

  @Override
  public List<IrpAccountTransaction> getIrpAccountTransactionList(
      IrpAccountTransactionSearch irpAccountTransactionSearch) {
    Pageable pageable = PageRequest.of(irpAccountTransactionSearch.getPageNumber(),
        irpAccountTransactionSearch.getPageSize(), Sort.by("transDtime", "transType", "transAmt").ascending());
    return investIrpAccountTransactionRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndUpdatedAtGreaterThanAndCreatedAtBetween(
            irpAccountTransactionSearch.getBanksaladUserId(),
            irpAccountTransactionSearch.getOrganizationId(),
            irpAccountTransactionSearch.getAccountNum(),
            irpAccountTransactionSearch.getSeqno(),
            irpAccountTransactionSearch.getUpdatedAt(),
            irpAccountTransactionSearch.getFromCreatedAt(),
            irpAccountTransactionSearch.getToCreatedAt(),
            pageable
        ).stream()
        .map(irpAccountTransactionMapper::entityToDto)
        .collect(Collectors.toList());
  }
}
