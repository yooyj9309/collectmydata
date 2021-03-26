package com.banksalad.collectmydata.ginsu.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.ginsu.common.db.entity.InsuranceTransactionEntity;

public interface InsuranceTransactionRepository extends JpaRepository<InsuranceTransactionEntity, Long> {

  @Transactional
  @Modifying
  @Query("delete from InsuranceTransactionEntity ie where ie.banksaladUserId = ?1 and ie.organizationId = ?2 and ie.insuNum = ?3")
  void deleteInsuranceTransactionsByBanksaladUserIdAndOrganizationIdAndInsuNum(
      long banaksaladUserId,
      String organizationId,
      String insuNum
  );

}
