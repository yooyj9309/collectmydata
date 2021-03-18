package com.banksalad.collectmydata.irp.common.db.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.irp.common.db.entity.IrpAccountTransactionEntity;

import java.util.Optional;

public interface IrpAccountTransactionRepository extends JpaRepository<IrpAccountTransactionEntity, Long> {

  Optional<IrpAccountTransactionEntity> findByTransactionYearMonthAndBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqnoAndUniqueTransNo(
      Integer transactionYearMonth,
      Long banksaladUserId, String organizationId, String accountNum, String seqno, String uniqueTransNo);
}
