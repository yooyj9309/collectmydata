package com.banksalad.collectmydata.insu.common.db.repository;

import com.banksalad.collectmydata.insu.common.db.entity.InsuredEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InsuredRepository extends JpaRepository<InsuredEntity, Long> {

  Optional<InsuredEntity> findByBanksaladUserIdAndOrganizationIdAndInsuNumAndInsuredNo(Long banksaladUserId,
      String organizationId, String insuNum, String inusredNo);

  List<InsuredEntity> findByBanksaladUserIdAndOrganizationIdAndInsuNum(Long banksaladUserId,
      String organizationId, String insuNum);
}
