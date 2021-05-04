package com.banksalad.collectmydata.insu.common.db.repository;

import com.banksalad.collectmydata.insu.common.db.entity.InsuredEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InsuredRepository extends JpaRepository<InsuredEntity, Long> {

  @Modifying
  @Query("delete from InsuredEntity ie where ie.banksaladUserId = ?1 and ie.organizationId = ?2 and ie.insuNum = ?3")
  void deleteInsuredByBanksaladUserIdAndOrganizationIdAndInsuNum(long banksaladUserId, String organizationId,
      String insuNum);

  Optional<InsuredEntity> findByBanksaladUserIdAndOrganizationIdAndInsuNumAndInsuredNo(Long banksaladUserId,
      String organizationId, String insuNum, String inusredNo);

  List<InsuredEntity> findByBanksaladUserIdAndOrganizationIdAndInsuNum(Long banksaladUserId,
      String organizationId, String insuNum);
}
