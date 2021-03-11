package com.banksalad.collectmydata.insu.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.banksalad.collectmydata.insu.common.db.entity.InsuranceContractEntity;

import java.util.List;
import java.util.Optional;

public interface InsuranceContractRepository extends JpaRepository<InsuranceContractEntity, Long> {

  Optional<InsuranceContractEntity> findByBanksaladUserIdAndOrganizationIdAndInsuNumAndInsuredNo(Long banksaladUserId,
      String organizationId, String insuNum, String insuredNo);

  List<InsuranceContractEntity> findAllByBanksaladUserIdAndOrganizationIdAndInsuNum(Long banksaladUserId,
      String organizationId, String insuNum);

  @Transactional
  void deleteAllByBanksaladUserIdAndOrganizationIdAndInsuNumAndInsuredNo(Long banksaladUserId,
      String organizationId, String insuNum, String insuredNo);
}
