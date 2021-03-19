package com.banksalad.collectmydata.capital.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.capital.common.db.entity.AccountDetailEntity;
import com.banksalad.collectmydata.capital.common.db.entity.AccountDetailHistoryEntity;

import java.util.List;
import java.util.Optional;

public interface AccountDetailHistoryRepository extends JpaRepository<AccountDetailHistoryEntity, Long> {

  List<AccountDetailHistoryEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNumAndSeqno(
      Long banksaladUserId, String organizationId, String accountNum, String seqno
  );
}
