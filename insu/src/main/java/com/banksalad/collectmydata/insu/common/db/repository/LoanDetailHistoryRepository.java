package com.banksalad.collectmydata.insu.common.db.repository;

import com.banksalad.collectmydata.insu.common.db.entity.LoanDetailHistoryEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoanDetailHistoryRepository extends JpaRepository<LoanDetailHistoryEntity, Long> {

  Optional<LoanDetailHistoryEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNum(Long bankSaladUserId,
      String organizationId, String accountNum);

}
