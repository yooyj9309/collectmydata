package com.banksalad.collectmydata.insu.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.insu.common.db.entity.LoanBasicEntity;

import java.util.List;
import java.util.Optional;

public interface LoanBasicRepository extends JpaRepository<LoanBasicEntity, Long> {

  Optional<LoanBasicEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNum(Long banksaladUserId,
      String organizationId, String accountNum);

  List<LoanBasicEntity> findAllByBanksaladUserIdAndOrganizationId(Long banksaladUserId, String organizationId);
}
