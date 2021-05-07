package com.banksalad.collectmydata.insu.car;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import com.banksalad.collectmydata.finance.api.transaction.TransactionResponseHelper;
import com.banksalad.collectmydata.finance.api.transaction.dto.TransactionResponse;
import com.banksalad.collectmydata.finance.common.constant.FinanceConstant;
import com.banksalad.collectmydata.insu.car.dto.CarInsurance;
import com.banksalad.collectmydata.insu.car.dto.CarInsuranceTransaction;
import com.banksalad.collectmydata.insu.car.dto.ListCarInsuranceTransactionsResponse;
import com.banksalad.collectmydata.insu.car.service.CarInsuranceService;
import com.banksalad.collectmydata.insu.common.db.entity.CarInsuranceTransactionEntity;
import com.banksalad.collectmydata.insu.common.db.repository.CarInsuranceTransactionRepository;
import com.banksalad.collectmydata.insu.common.mapper.CarInsuranceTransactionMapper;

import org.springframework.stereotype.Component;

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
      /* mapping dto to entity */
      CarInsuranceTransactionEntity carInsuranceTransactionEntity = carInsuranceTransactionMapper
          .dtoToEntity(carInsuranceTransaction);
      carInsuranceTransactionEntity.setSyncedAt(executionContext.getSyncStartedAt());
      carInsuranceTransactionEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
      carInsuranceTransactionEntity.setOrganizationId(executionContext.getOrganizationId());
      carInsuranceTransactionEntity.setInsuNum(carInsurance.getInsuNum());
      carInsuranceTransactionEntity.setCarNumber(carInsurance.getCarNumber());
      carInsuranceTransactionEntity.setConsentId(executionContext.getConsentId());
      carInsuranceTransactionEntity.setSyncRequestId(executionContext.getSyncRequestId());
      carInsuranceTransactionEntity.setCreatedBy(executionContext.getRequestedBy());
      carInsuranceTransactionEntity.setUpdatedBy(executionContext.getRequestedBy());

      /* load existing entity */
      CarInsuranceTransactionEntity existingCarInsuranceTransactionEntity = carInsuranceTransactionRepository
          .findByBanksaladUserIdAndOrganizationIdAndInsuNumAndCarNumberAndTransNo(
              executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
              carInsurance.getInsuNum(), carInsurance.getCarNumber(), carInsuranceTransaction.getTransNo())
          .orElse(null);

      if (existingCarInsuranceTransactionEntity != null) {
        carInsuranceTransactionEntity.setId(existingCarInsuranceTransactionEntity.getId());
      }

      if (!ObjectComparator
          .isSame(carInsuranceTransactionEntity, existingCarInsuranceTransactionEntity,
              FinanceConstant.ENTITY_EXCLUDE_FIELD)) {
        carInsuranceTransactionRepository.save(carInsuranceTransactionEntity);
      }
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
