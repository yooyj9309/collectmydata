package com.banksalad.collectmydata.insu.publishment.car;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.insu.common.db.repository.CarInsuranceRepository;
import com.banksalad.collectmydata.insu.common.db.repository.CarInsuranceTransactionRepository;
import com.banksalad.collectmydata.insu.common.mapper.CarInsuranceMapper;
import com.banksalad.collectmydata.insu.common.mapper.CarInsuranceTransactionMapper;
import com.banksalad.collectmydata.insu.publishment.car.dto.CarInsurancePublishmentResponse;
import com.banksalad.collectmydata.insu.publishment.car.dto.CarInsuranceTransactionPublishmentResponse;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarInsurancePublishmentServiceImpl implements CarInsurancePublishmentService {

  private final CarInsuranceRepository carInsuranceRepository;
  private final CarInsuranceTransactionRepository carInsuranceTransactionRepository;

  private final CarInsuranceMapper carInsuranceMapper = Mappers.getMapper(CarInsuranceMapper.class);
  private final CarInsuranceTransactionMapper carInsuranceTransactionMapper = Mappers
      .getMapper(CarInsuranceTransactionMapper.class);

  @Override
  public List<CarInsurancePublishmentResponse> getCarInsuranceResponses(long banksaladUserId, String organizationId) {
    return carInsuranceRepository
        .findByBanksaladUserIdAndOrganizationId(banksaladUserId, organizationId)
        .stream()
        .map(carInsuranceMapper::entityToPublishmentDto)
        .collect(Collectors.toList());
  }

  @Override
  public List<CarInsuranceTransactionPublishmentResponse> getCarInsuranceTransactionResponses(long banksaladUserId,
      String organizationId, String insuNum, LocalDateTime createdAt, int limit) {
    return carInsuranceTransactionRepository
        .findByBanksaladUserIdAndOrganizationIdAndInsuNumAndCreatedAtAfter(
            banksaladUserId, organizationId, insuNum, createdAt, PageRequest.of(0, limit))
        .stream()
        .map(carInsuranceTransactionMapper::entityToPublishmentDto)
        .collect(Collectors.toList());
  }
}
