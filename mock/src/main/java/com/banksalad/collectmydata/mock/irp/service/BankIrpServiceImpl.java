package com.banksalad.collectmydata.mock.irp.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.mock.common.config.TransDtimeFormatter;
import com.banksalad.collectmydata.mock.common.db.repository.BankIrpAccountBasicRepository;
import com.banksalad.collectmydata.mock.common.db.repository.BankIrpAccountDetailRepository;
import com.banksalad.collectmydata.mock.common.db.repository.BankIrpAccountSummaryRepository;
import com.banksalad.collectmydata.mock.common.db.repository.BankIrpAccountTransactionRepository;
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

@Service("bankIrpService")
@RequiredArgsConstructor
public class BankIrpServiceImpl implements IrpService {

  private final BankIrpAccountSummaryRepository bankIrpAccountSummaryRepository;
  private final BankIrpAccountBasicRepository bankIrpAccountBasicRepository;
  private final BankIrpAccountDetailRepository bankIrpAccountDetailRepository;
  private final BankIrpAccountTransactionRepository bankIrpAccountTransactionRepository;

  private final IrpAccountSummaryMapper irpAccountSummaryMapper = Mappers.getMapper(IrpAccountSummaryMapper.class);
  private final IrpAccountBasicMapper irpAccountBasicMapper = Mappers.getMapper(IrpAccountBasicMapper.class);
  private final IrpAccountDetailMapper irpAccountDetailMapper = Mappers.getMapper(IrpAccountDetailMapper.class);
  private final IrpAccountTransactionMapper irpAccountTransactionMapper =
      Mappers.getMapper(IrpAccountTransactionMapper.class);

  @Override
  public List<IrpAccountSummary> getIrpAccountSummaryList(IrpAccountSummarySearch irpAccountSummarySearch) {
    return bankIrpAccountSummaryRepository
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
    return bankIrpAccountBasicRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(irpAccountBasicSearch.getBanksaladUserId(),
            irpAccountBasicSearch.getOrganizationId(),
            irpAccountBasicSearch.getAccountNum(),
            irpAccountBasicSearch.getSeqno())
        .map(irpAccountBasicMapper::entityToDto)
        .orElseThrow(() -> new CollectmydataMockRuntimeException(CollectmydataMockExceptionCode.NOT_FOUND_ASSETS));
  }

  @Override
  public int getIrpAccountDetailCount(IrpAccountDetailSearch irpAccountDetailSearch) {
    return bankIrpAccountDetailRepository
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
        irpAccountDetailSearch.getPageSize(), Sort.by("irpName").ascending());
    return bankIrpAccountDetailRepository
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
    return bankIrpAccountTransactionRepository
        .countByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndUpdatedAtGreaterThanAndTransDtimeBetween(
            irpAccountTransactionSearch.getBanksaladUserId(),
            irpAccountTransactionSearch.getOrganizationId(),
            irpAccountTransactionSearch.getAccountNum(),
            irpAccountTransactionSearch.getSeqno(),
            irpAccountTransactionSearch.getUpdatedAt(),
            irpAccountTransactionSearch.getFromDate().format(TransDtimeFormatter.get()),
            irpAccountTransactionSearch.getToDate().format(TransDtimeFormatter.get())
        );
  }

  @Override
  public List<IrpAccountTransaction> getIrpAccountTransactionList(
      IrpAccountTransactionSearch irpAccountTransactionSearch) {
    Pageable pageable = PageRequest.of(irpAccountTransactionSearch.getPageNumber(),
        irpAccountTransactionSearch.getPageSize(), Sort.by("transDtime", "transType", "transAmt").ascending());
    return bankIrpAccountTransactionRepository
        .findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndUpdatedAtGreaterThanAndTransDtimeBetween(
            irpAccountTransactionSearch.getBanksaladUserId(),
            irpAccountTransactionSearch.getOrganizationId(),
            irpAccountTransactionSearch.getAccountNum(),
            irpAccountTransactionSearch.getSeqno(),
            irpAccountTransactionSearch.getUpdatedAt(),
            irpAccountTransactionSearch.getFromDate().format(TransDtimeFormatter.get()),
            irpAccountTransactionSearch.getToDate().format(TransDtimeFormatter.get()),
            pageable
        ).stream()
        .map(irpAccountTransactionMapper::entityToDto)
        .collect(Collectors.toList());
  }
}
