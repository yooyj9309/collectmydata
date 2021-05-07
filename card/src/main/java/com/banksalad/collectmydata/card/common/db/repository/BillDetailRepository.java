package com.banksalad.collectmydata.card.common.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.banksalad.collectmydata.card.common.db.entity.BillDetailEntity;

import java.util.List;

public interface BillDetailRepository extends JpaRepository<BillDetailEntity, Long> {

  List<BillDetailEntity> findByBanksaladUserIdAndOrganizationIdAndChargeMonthAndSeqno(
      Long banksaladUserId, String organizationId, Integer chargeMonth, String seqno);

  /**
   * @Modifiying는 Insert, update, delete, DDL 등 DB 데이터를 변경하는 @Query와 함께 붙여야하는 어노테이션.
   * clearAutomatically = true를 준 이유는 @Query를 이용하면 영속성컨텍스트를 거치지 않기 때문에
   * DB와 영속성 컨텍스트 데이터의 불일치 발생 가능성이 있어서 연산 직후 영속성 컨텍스트를 clear한다.
   *
   * :seqno is null 옵션은 null이 올 수 있는 필드를 동적으로 구분.
   *
   * @author : hyunjun
   */
  @Query("delete from BillDetailEntity b where b.banksaladUserId = :banksaladUserId and b.organizationId = :organizationId "
      + "and b.chargeMonth = :chargeMonth and (:seqno is null or b.seqno = :seqno)")
  @Modifying(clearAutomatically = true)
  void deleteAllByBanksaladUserIdAndOrganizationIdAndChargeMonthAndSeqnoInQuery(
      @Param("banksaladUserId") long banksaladUserId, @Param("organizationId") String organizationId,
      @Param("chargeMonth") int chargeMonth, @Param("seqno") String seqno
  );
}
