package com.banksalad.collectmydata.insu.publishment.insurance;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.insu.common.db.repository.InsuranceBasicRepository;
import com.banksalad.collectmydata.insu.common.db.repository.InsuranceContractRepository;
import com.banksalad.collectmydata.insu.common.db.repository.InsurancePaymentRepository;
import com.banksalad.collectmydata.insu.common.db.repository.InsuranceTransactionRepository;
import com.banksalad.collectmydata.insu.common.db.repository.InsuredRepository;
import com.banksalad.collectmydata.insu.common.mapper.InsuranceBasicMapper;
import com.banksalad.collectmydata.insu.common.mapper.InsuranceContractMapper;
import com.banksalad.collectmydata.insu.common.mapper.InsurancePaymentMapper;
import com.banksalad.collectmydata.insu.common.mapper.InsuranceTransactionMapper;
import com.banksalad.collectmydata.insu.common.mapper.InsuredMapper;
import com.banksalad.collectmydata.insu.publishment.insurance.dto.InsuranceBasicPublishmentResponse;
import com.banksalad.collectmydata.insu.publishment.insurance.dto.InsuranceContractPublishmentResponse;
import com.banksalad.collectmydata.insu.publishment.insurance.dto.InsurancePaymentPublishmentResponse;
import com.banksalad.collectmydata.insu.publishment.insurance.dto.InsuranceTransactionPublishmentResponse;
import com.banksalad.collectmydata.insu.publishment.insurance.dto.InsuredPublishmentResponse;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InsurancePublishmentServiceImpl implements InsurancePublishmentService {

  private final InsuranceBasicRepository insuranceBasicRepository;
  private final InsuredRepository insuredRepository;
  private final InsuranceContractRepository insuranceContractRepository;
  private final InsurancePaymentRepository insurancePaymentRepository;
  private final InsuranceTransactionRepository insuranceTransactionRepository;

  private final InsuranceBasicMapper insuranceBasicMapper = Mappers.getMapper(InsuranceBasicMapper.class);
  private final InsuredMapper insuredMapper = Mappers.getMapper(InsuredMapper.class);
  private final InsuranceContractMapper insuranceContractMapper = Mappers.getMapper(InsuranceContractMapper.class);
  private final InsurancePaymentMapper insurancePaymentMapper = Mappers.getMapper(InsurancePaymentMapper.class);
  private final InsuranceTransactionMapper insuranceTransactionMapper = Mappers
      .getMapper(InsuranceTransactionMapper.class);

  @Override
  public List<InsuranceBasicPublishmentResponse> getInsuranceBasicResponses(long banksaladUserId,
      String organizationId) {
    List<InsuranceBasicPublishmentResponse> basicPublishmentResponses = insuranceBasicRepository
        .findAllByBanksaladUserIdAndOrganizationId(banksaladUserId, organizationId)
        .stream()
        .map(insuranceBasicMapper::entityToPublishmentDto)
        .collect(Collectors.toList());

    for (InsuranceBasicPublishmentResponse basicPublishmentResponse : basicPublishmentResponses) {
      List<InsuredPublishmentResponse> insuredPublishmentResponses = insuredRepository
          .findByBanksaladUserIdAndOrganizationIdAndInsuNum(banksaladUserId, organizationId,
              basicPublishmentResponse.getInsuNum())
          .stream()
          .map(insuredMapper::entityToPublishmentDto)
          .collect(Collectors.toList());

      basicPublishmentResponse.getInsuredPublishmentResponse().addAll(insuredPublishmentResponses);
    }

    return basicPublishmentResponses;
  }

  @Override
  public List<InsuranceContractPublishmentResponse> getInsuranceContractResponses(long banksaladUserId,
      String organizationId) {
    return insuranceContractRepository
        .findAllByBanksaladUserIdAndOrganizationId(banksaladUserId, organizationId)
        .stream()
        .map(insuranceContractMapper::entityToPublishmentDto)
        .collect(Collectors.toList());
  }

  @Override
  public List<InsurancePaymentPublishmentResponse> getInsurancePaymentResponses(long banksaladUserId,
      String organizationId) {
    return insurancePaymentRepository
        .findByBanksaladUserIdAndOrganizationId(banksaladUserId, organizationId)
        .stream()
        .map(insurancePaymentMapper::entityToPublishmentDto)
        .collect(Collectors.toList());
  }

  @Override
  public List<InsuranceTransactionPublishmentResponse> getInsuranceTransactionResponses(long banksaladUserId,
      String organizationId, String insuNum, LocalDateTime createdAt, int limit) {
    return insuranceTransactionRepository
        .findByBanksaladUserIdAndOrganizationIdAndInsuNumAndCreatedAtAfter(banksaladUserId, organizationId, insuNum,
            createdAt, PageRequest.of(0, limit))
        .stream()
        .map(insuranceTransactionMapper::entityToPublishmentDto)
        .collect(Collectors.toList());
  }
}
