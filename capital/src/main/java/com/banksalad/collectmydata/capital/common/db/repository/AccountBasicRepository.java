package com.banksalad.collectmydata.capital.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.capital.common.db.entity.AccountBasicEntity;

import java.util.Optional;

public interface AccountBasicRepository extends JpaRepository<AccountBasicEntity, Long> {

  Optional<AccountBasicEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
      Long banksaladUserId, String organizationId, String accountNum, String seqno
  );
}
