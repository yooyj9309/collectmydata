package com.banksalad.collectmydata.capital.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.capital.common.db.entity.AccountBasicEntity;

public interface AccountBasicRepository extends JpaRepository<AccountBasicEntity, Long> {

  AccountBasicEntity findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
      Long banksaladUserId, String organizationId, String accountNum, String seqno
  );
}
