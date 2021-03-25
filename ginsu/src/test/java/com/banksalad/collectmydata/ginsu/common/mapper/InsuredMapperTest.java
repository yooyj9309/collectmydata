package com.banksalad.collectmydata.ginsu.common.mapper;

import com.banksalad.collectmydata.ginsu.common.db.entity.InsuredEntity;
import com.banksalad.collectmydata.ginsu.insurance.dto.Insured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("피보험자 매퍼 테스트")
class InsuredMapperTest {

  private InsuredMapper insuredMapper = Mappers.getMapper(InsuredMapper.class);

  @Test
  void dtoToEntityTest() {
    Insured insured = Insured.builder()
        .insuredName("피보험자명")
        .build();

    InsuredEntity insuredEntity = insuredMapper.dtoToEntity(insured);

    assertThat(insuredEntity.getInsuredName()).isEqualTo(insured.getInsuredName());
  }
}
