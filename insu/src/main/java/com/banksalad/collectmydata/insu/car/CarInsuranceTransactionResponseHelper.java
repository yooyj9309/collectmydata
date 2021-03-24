package com.banksalad.collectmydata.insu.car;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.transaction.TransactionResponseHelper;
import com.banksalad.collectmydata.finance.api.transaction.dto.TransactionResponse;
import com.banksalad.collectmydata.insu.car.dto.CarInsurance;
import com.banksalad.collectmydata.insu.car.dto.CarInsuranceTransaction;
import com.banksalad.collectmydata.insu.car.dto.ListCarInsuranceTransactionsResponse;
import com.banksalad.collectmydata.insu.car.service.CarInsuranceService;
import com.banksalad.collectmydata.insu.common.db.entity.CarInsuranceTransactionEntity;
import com.banksalad.collectmydata.insu.common.db.repository.CarInsuranceTransactionRepository;
import com.banksalad.collectmydata.insu.common.mapper.CarInsuranceTransactionMapper;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CarInsuranceTransactionResponseHelper implements
    TransactionResponseHelper<CarInsurance, CarInsuranceTransaction> {

  private final CarInsuranceService carInsuranceService;
  private final CarInsuranceTransactionRepository carInsuranceTransactionRepository;

  private final CarInsuranceTransactionMapper carInsuranceTransactionMapper = Mappers
      .getMapper(CarInsuranceTransactionMapper.class);

  @Override
  public List<CarInsuranceTransaction> getTransactionsFromResponse(TransactionResponse transactionResponse) {
    return ((ListCarInsuranceTransactionsResponse) transactionResponse).getCarInsuranceTransactions();
  }

  @Override
  public void saveTransactions(ExecutionContext executionContext, CarInsurance carInsurance,
      List<CarInsuranceTransaction> carInsuranceTransactions) {

    for (CarInsuranceTransaction carInsuranceTransaction : carInsuranceTransactions) {
      /* load existing entity */
      CarInsuranceTransactionEntity carInsuranceTransactionEntity = carInsuranceTransactionRepository
          .findByBanksaladUserIdAndOrganizationIdAndInsuNumAndCarNumberAndTransNo(
              executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
              carInsurance.getInsuNum(), carInsurance.getCarNumber(), carInsuranceTransaction.getTransNo())
          .orElseGet(() -> CarInsuranceTransactionEntity.builder().build());

      /* mapping dto to entity */
      carInsuranceTransactionEntity = carInsuranceTransactionMapper
          .dtoToEntity(carInsuranceTransaction, carInsuranceTransactionEntity);

      carInsuranceTransactionEntity.setSyncedAt(executionContext.getSyncStartedAt());
      carInsuranceTransactionEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
      carInsuranceTransactionEntity.setOrganizationId(executionContext.getOrganizationId());
      carInsuranceTransactionEntity.setInsuNum(carInsurance.getInsuNum());
      carInsuranceTransactionEntity.setCarNumber(carInsurance.getCarNumber());

      /* upsert entity */
      carInsuranceTransactionRepository.save(carInsuranceTransactionEntity);
    }
  }

  @Override
  public void saveTransactionSyncedAt(ExecutionContext executionContext, CarInsurance carInsurance,
      LocalDateTime syncStartedAt) {
    carInsuranceService.updateTransactionSyncedAt(executionContext.getBanksaladUserId(),
        executionContext.getOrganizationId(), carInsurance, syncStartedAt);
  }

  @Override
  public void saveResponseCode(ExecutionContext executionContext, CarInsurance carInsurance, String responseCode) {
    carInsuranceService.updateTransactionResponseCode(executionContext.getBanksaladUserId(),
        executionContext.getOrganizationId(), carInsurance, responseCode);
  }
}
