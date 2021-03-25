package com.banksalad.collectmydata.ginsu.common.mapper;

import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.ginsu.common.db.entity.InsuredEntity;
import com.banksalad.collectmydata.ginsu.common.db.entity.InsuredHistoryEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@DisplayName("피보험자 이력 매퍼 테스트")
class InsuredHistoryMapperTest {

  private InsuredHistoryMapper insuredHistoryMapper = Mappers.getMapper(InsuredHistoryMapper.class);

  @Test
  void toHistoryEntity() {
    InsuredEntity insuredEntity = InsuredEntity.builder()
        .id(1L)
        .syncedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
        .banksaladUserId(1L)
        .organizationId("organizationId")
        .insuredNo(Short.valueOf("1"))
        .insuredName("피보험자명")
        .insuNum("insuNum")
        .build();

    InsuredHistoryEntity historyEntity = insuredHistoryMapper.toHistoryEntity(insuredEntity);

    assertAll(
        () -> assertNotEquals(insuredEntity.getId(), historyEntity.getId()),
        () -> assertEquals(insuredEntity.getSyncedAt(), historyEntity.getSyncedAt()),
        () -> assertEquals(insuredEntity.getBanksaladUserId(), historyEntity.getBanksaladUserId()),
        () -> assertEquals(insuredEntity.getOrganizationId(), historyEntity.getOrganizationId()),
        () -> assertEquals(insuredEntity.getInsuredNo(), historyEntity.getInsuredNo()),
        () -> assertEquals(insuredEntity.getInsuredName(), historyEntity.getInsuredName()),
        () -> assertEquals(insuredEntity.getInsuNum(), historyEntity.getInsuNum())
    );
  }
}
