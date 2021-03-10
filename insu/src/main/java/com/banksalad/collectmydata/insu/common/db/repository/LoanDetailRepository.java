package com.banksalad.collectmydata.insu.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.insu.common.db.entity.LoanDetailEntity;

import java.util.Optional;

public interface LoanDetailRepository extends JpaRepository<LoanDetailEntity, Long> {

  Optional<LoanDetailEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNum(Long bankSaladUserId,
      String organizationId, String accountNum);
}
