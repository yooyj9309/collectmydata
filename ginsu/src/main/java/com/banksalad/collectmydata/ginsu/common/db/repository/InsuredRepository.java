package com.banksalad.collectmydata.ginsu.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.ginsu.common.db.entity.InsuredEntity;

import java.util.List;

public interface InsuredRepository extends JpaRepository<InsuredEntity, Long> {

  @Transactional
  @Modifying
  @Query("delete from InsuredEntity ie where ie.banksaladUserId = ?1 and ie.organizationId = ?2 and ie.insuNum = ?3")
  void deleteInsuredByBanksaladUserIdAndOrganizationIdAndInsuNum(
      long banaksaladUserId,
      String organizationId,
      String insuNum
  );

  List<InsuredEntity> findByBanksaladUserIdAndOrganizationIdAndInsuNum(
      long banaksaladUserId,
      String organizationId,
      String insuNum
  );
}
