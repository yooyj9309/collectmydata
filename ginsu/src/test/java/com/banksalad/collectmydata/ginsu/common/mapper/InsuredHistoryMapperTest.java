package com.banksalad.collectmydata.ginsu.common.mapper;

import com.banksalad.collectmydata.common.util.DateUtil;
import com.banksalad.collectmydata.ginsu.common.db.entity.InsuredEntity;
import com.banksalad.collectmydata.ginsu.common.db.entity.InsuredHistoryEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.CONSENT_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.ORGANIZATION_ID;
import static com.banksalad.collectmydata.finance.test.constant.FinanceTestConstants.SYNC_REQUEST_ID;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@DisplayName("피보험자 이력 매퍼 테스트")
class InsuredHistoryMapperTest {

  private InsuredHistoryMapper insuredHistoryMapper = Mappers.getMapper(InsuredHistoryMapper.class);

  @Test
  void entityToHistoryEntityTest() {
    InsuredEntity insuredEntity = InsuredEntity.builder()
        .id(1L)
        .syncedAt(LocalDateTime.now(DateUtil.UTC_ZONE_ID))
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .insuredNo(Short.valueOf("1"))
        .insuredName("피보험자명")
        .insuNum("insuNum")
        .consentId(CONSENT_ID)
        .syncRequestId(SYNC_REQUEST_ID)
        .build();

    insuredEntity.setCreatedBy(String.valueOf(BANKSALAD_USER_ID));
    insuredEntity.setUpdatedBy(String.valueOf(BANKSALAD_USER_ID));

    InsuredHistoryEntity historyEntity = insuredHistoryMapper
        .entityToHistoryEntity(insuredEntity, InsuredHistoryEntity.builder().build());

    assertAll(
        () -> assertNotEquals(insuredEntity.getId(), historyEntity.getId()),
        () -> assertEquals(insuredEntity.getSyncedAt(), historyEntity.getSyncedAt()),
        () -> assertEquals(insuredEntity.getBanksaladUserId(), historyEntity.getBanksaladUserId()),
        () -> assertEquals(insuredEntity.getOrganizationId(), historyEntity.getOrganizationId()),
        () -> assertEquals(insuredEntity.getInsuredNo(), historyEntity.getInsuredNo()),
        () -> assertEquals(insuredEntity.getInsuredName(), historyEntity.getInsuredName()),
        () -> assertEquals(insuredEntity.getInsuNum(), historyEntity.getInsuNum()),
        () -> assertEquals(insuredEntity.getConsentId(), historyEntity.getConsentId()),
        () -> assertEquals(insuredEntity.getSyncRequestId(), historyEntity.getSyncRequestId()),
        () -> assertEquals(insuredEntity.getCreatedAt(), historyEntity.getCreatedAt()),
        () -> assertEquals(insuredEntity.getCreatedBy(), historyEntity.getCreatedBy()),
        () -> assertEquals(insuredEntity.getUpdatedAt(), historyEntity.getUpdatedAt()),
        () -> assertEquals(insuredEntity.getUpdatedBy(), historyEntity.getUpdatedBy())
    );
  }
}
