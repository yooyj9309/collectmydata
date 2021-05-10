package com.banksalad.collectmydata.card.common.db.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.card.common.db.entity.ApprovalDomesticEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ApprovalDomesticRepository extends JpaRepository<ApprovalDomesticEntity, Long> {

  Optional<ApprovalDomesticEntity> findByBanksaladUserIdAndOrganizationIdAndApprovalYearMonthAndCardIdAndApprovedNumAndStatus(
      Long banksaladUserId, String organizationId, Integer approvalYearMonth, String cardId, String approvedNum,
      String status);

  Page<ApprovalDomesticEntity> findAllByBanksaladUserIdAndOrganizationIdAndCardIdAndCreatedAtAfter(Long banksaladUserId,
      String organizationId, String cardId, LocalDateTime createdAt, Pageable pageable
  );
}
