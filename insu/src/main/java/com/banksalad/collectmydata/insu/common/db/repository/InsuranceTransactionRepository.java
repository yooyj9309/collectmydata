package com.banksalad.collectmydata.insu.common.db.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.insu.common.db.entity.InsuranceTransactionEntity;

import java.time.LocalDateTime;
import java.util.Optional;

public interface InsuranceTransactionRepository extends JpaRepository<InsuranceTransactionEntity, Long> {

  Optional<InsuranceTransactionEntity> findByBanksaladUserIdAndOrganizationIdAndInsuNumAndTransNoAndTransactionYearMonth(
      Long banksaladUserId, String organizationId, String insuNum, Integer transNo, Integer transactionsYearMonth);

  Page<InsuranceTransactionEntity> findByBanksaladUserIdAndOrganizationIdAndInsuNumAndCreatedAtAfter(
      long banksaladUserId, String organizationId, String insuNum, LocalDateTime createdAt, PageRequest of);
}
