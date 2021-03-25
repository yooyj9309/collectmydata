package com.banksalad.collectmydata.ginsu.common.mapper;

import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.ginsu.common.db.entity.InsuranceBasicEntity;
import com.banksalad.collectmydata.ginsu.common.db.entity.InsuranceBasicHistoryEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@DisplayName("보증보험 기본정보 이력 매퍼 테스트")
class InsuranceBasicHistoryMapperTest {

  private InsuranceBasicHistoryMapper insuranceBasicHistoryMapper = Mappers
      .getMapper(InsuranceBasicHistoryMapper.class);

  @Test
  void toHistoryEntityTest() {
    InsuranceBasicEntity insuranceBasicEntity = InsuranceBasicEntity.builder()
        .id(1L)
        .syncedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
        .banksaladUserId(1L)
        .organizationId("organizationId")
        .insuNum("insuNum")
        .issueDate("20210301")
        .expDate("20300415")
        .faceAmt(BigDecimal.valueOf(180000, 3))
        .payDue("02")
        .payAmt(BigDecimal.valueOf(300000000, 3))
        .build();

    InsuranceBasicHistoryEntity historyEntity = insuranceBasicHistoryMapper
        .toHistoryEntity(insuranceBasicEntity);

    assertAll(
        () -> assertNotEquals(insuranceBasicEntity.getId(), historyEntity.getId()),
        () -> assertEquals(insuranceBasicEntity.getSyncedAt(), historyEntity.getSyncedAt()),
        () -> assertEquals(insuranceBasicEntity.getBanksaladUserId(), historyEntity.getBanksaladUserId()),
        () -> assertEquals(insuranceBasicEntity.getOrganizationId(), historyEntity.getOrganizationId()),
        () -> assertEquals(insuranceBasicEntity.getInsuNum(), historyEntity.getInsuNum()),
        () -> assertEquals(insuranceBasicEntity.getIssueDate(), historyEntity.getIssueDate()),
        () -> assertEquals(insuranceBasicEntity.getExpDate(), historyEntity.getExpDate()),
        () -> assertEquals(insuranceBasicEntity.getFaceAmt(), historyEntity.getFaceAmt()),
        () -> assertEquals(insuranceBasicEntity.getPayDue(), historyEntity.getPayDue()),
        () -> assertEquals(insuranceBasicEntity.getPayAmt(), historyEntity.getPayAmt())
    );
  }
}
