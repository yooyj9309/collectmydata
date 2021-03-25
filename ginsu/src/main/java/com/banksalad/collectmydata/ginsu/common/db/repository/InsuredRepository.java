package com.banksalad.collectmydata.ginsu.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.banksalad.collectmydata.ginsu.common.db.entity.InsuredEntity;

public interface InsuredRepository extends JpaRepository<InsuredEntity, Long> {

  @Modifying
  @Query("delete from InsuredEntity ie where ie.banksaladUserId = ?1 and ie.organizationId = ?2 and ie.insuNum = ?3")
  void deleteInsuredByBanksaladUserIdAndOrganizationIdAndInsuNum(
      long banaksaladUserId,
      String organizationId,
      String insuNum
  );
}
