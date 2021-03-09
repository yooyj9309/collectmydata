package com.banksalad.collectmydata.capital.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.capital.common.db.entity.AccountDetailEntity;

import java.util.Optional;

public interface AccountDetailRepository extends JpaRepository<AccountDetailEntity, Long> {

  Optional<AccountDetailEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
      Long banksaladUserId, String organizationId, String accountNum, String seqno
  );
}
