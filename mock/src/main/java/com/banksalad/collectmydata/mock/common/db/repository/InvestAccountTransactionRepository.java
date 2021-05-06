package com.banksalad.collectmydata.mock.common.db.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.mock.common.db.entity.InvestAccountTransactionEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface InvestAccountTransactionRepository extends JpaRepository<InvestAccountTransactionEntity, Long> {

  Page<InvestAccountTransactionEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNumAndTransDtimeBetween(
      Long banksaladUserId, String organizationId, String accountNum, String fromDate, String toDate,
      Pageable pageable);
}
