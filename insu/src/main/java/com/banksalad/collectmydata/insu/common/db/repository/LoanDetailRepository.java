package com.banksalad.collectmydata.insu.common.db.repository;

import com.banksalad.collectmydata.insu.common.db.entity.LoanDetailEntity;
import io.grpc.MethodDescriptor;
import io.grpc.MethodDescriptor.Marshaller;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanDetailRepository extends JpaRepository<LoanDetailEntity, Long> {

  Optional<LoanDetailEntity> findByBanksaladUserIdAndOrganizationIdAndAccountNum(Long bankSaladUserId,
      String organizationId, String accountNum);

  List<LoanDetailEntity> findAllByBanksaladUserIdAndOrganizationId(long banksaladUserId, String organizationId);
}
