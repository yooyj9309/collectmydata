package com.banksalad.collectmydata.mock.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.mock.common.db.entity.InvestAccountBasicEntity;

import java.util.Optional;

public interface InvestAccountBasicRepository extends JpaRepository<InvestAccountBasicEntity, Long> {

  Optional<InvestAccountBasicEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNum(Long banksaladUserId,
      String organizationId, String accountNum);
}
