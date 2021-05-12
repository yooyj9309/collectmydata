package com.banksalad.collectmydata.insu.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banksalad.collectmydata.insu.common.db.entity.LoanSummaryEntity;
import io.grpc.MethodDescriptor;
import io.grpc.MethodDescriptor.Marshaller;

import java.util.List;
import java.util.Optional;

public interface LoanSummaryRepository extends JpaRepository<LoanSummaryEntity, Long> {

  Optional<LoanSummaryEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNum(Long bankSaladUserId,
      String organizationId, String accountNum);

  List<LoanSummaryEntity> findAllByBanksaladUserIdAndOrganizationIdAndConsentIsTrue(Long banksaladUserId,
      String organizationId);
}
