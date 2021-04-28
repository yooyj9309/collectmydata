package com.banksalad.collectmydata.mock.irp.service;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.mock.common.db.repository.InvestIrpAccountBasicRepository;
import com.banksalad.collectmydata.mock.common.db.repository.InvestIrpAccountSummaryRepository;
import com.banksalad.collectmydata.mock.common.exception.CollectmydataMockRuntimeException;
import com.banksalad.collectmydata.mock.common.exception.code.CollectmydataMockExceptionCode;
import com.banksalad.collectmydata.mock.irp.dto.IrpAccountBasic;
import com.banksalad.collectmydata.mock.irp.dto.IrpAccountBasicSearch;
import com.banksalad.collectmydata.mock.irp.dto.IrpAccountSummary;
import com.banksalad.collectmydata.mock.irp.dto.IrpAccountSummarySearch;
import com.banksalad.collectmydata.mock.irp.service.mapper.IrpAccountBasicMapper;
import com.banksalad.collectmydata.mock.irp.service.mapper.IrpAccountSummaryMapper;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Service("investIrpService")
@RequiredArgsConstructor
public class InvestIrpServiceImpl implements IrpService {

  private final InvestIrpAccountSummaryRepository investIrpAccountSummaryRepository;
  private final InvestIrpAccountBasicRepository investIrpAccountBasicRepository;

  private final IrpAccountSummaryMapper irpAccountSummaryMapper = Mappers.getMapper(IrpAccountSummaryMapper.class);
  private final IrpAccountBasicMapper irpAccountBasicMapper = Mappers.getMapper(IrpAccountBasicMapper.class);

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
}
