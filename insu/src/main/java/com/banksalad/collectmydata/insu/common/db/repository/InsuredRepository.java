package com.banksalad.collectmydata.insu.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.insu.common.db.entity.InsuredEntity;

import java.util.Optional;

public interface InsuredRepository extends JpaRepository<InsuredEntity, Long> {

  Optional<InsuredEntity> findByBanksaladUserIdAndOrganizationIdAndInsuNumAndInsuredNo(Long banksaladUserId,
      String organizationId, String insuNum, String inusredNo);
}
