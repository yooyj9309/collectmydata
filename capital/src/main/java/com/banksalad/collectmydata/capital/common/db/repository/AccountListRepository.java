package com.banksalad.collectmydata.capital.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.capital.common.db.entity.AccountListEntity;

import java.util.Optional;

public interface AccountListRepository extends JpaRepository<AccountListEntity, Long> {

  Optional<AccountListEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(Long banksaladUserId,
      String organizationId, String accountNum, Integer seqno);

}
