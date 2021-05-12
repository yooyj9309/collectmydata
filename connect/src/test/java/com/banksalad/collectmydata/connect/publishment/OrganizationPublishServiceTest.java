package com.banksalad.collectmydata.connect.publishment;

import com.banksalad.collectmydata.connect.common.db.entity.ConnectOrganizationEntity;
import com.banksalad.collectmydata.connect.common.db.entity.OauthTokenEntity;
import com.banksalad.collectmydata.connect.common.db.repository.ConnectOrganizationRepository;
import com.banksalad.collectmydata.connect.common.db.repository.OauthTokenRepository;
import com.banksalad.collectmydata.connect.publishment.organization.dto.OrganizationForFinance;
import com.banksalad.collectmydata.connect.publishment.organization.service.OrganizationPublishService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.github.banksalad.idl.apis.v1.collectmydata.CollectmydataconnectProto.ListConnectedFinanceOrganizationsRequest;
import javax.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static com.banksalad.collectmydata.connect.common.ConnectConstant.ACCESS_TOKEN;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.ACCESS_TOKEN_EXPIRES_AT;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.ACCESS_TOKEN_EXPIRES_IN;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.AUTHORIZATION_CODE;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.BANKSALAD_USER_ID;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.CONSENT_ID;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.DOMAIN;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.INDUSTRY;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.ORGANIZATION_CODE;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.ORGANIZATION_GUID;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.ORGANIZATION_ID;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.ORGANIZATION_STATUS;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.REFRESH_TOKEN;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.REFRESH_TOKEN_EXPIRES_AT;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.REFRESH_TOKEN_EXPIRES_IN;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.SCOPE;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.SECTOR;
import static com.banksalad.collectmydata.connect.common.ConnectConstant.TOKEN_TYPE;
import static com.banksalad.collectmydata.connect.common.constant.OrganizationConstants.AUTH_URI;
import static java.lang.Boolean.FALSE;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class OrganizationPublishServiceTest {

  @Autowired
  private OrganizationPublishService organizationPublishService;

  @Autowired
  private ConnectOrganizationRepository connectOrganizationRepository;

  @Autowired
  private OauthTokenRepository oauthTokenRepository;

  @Test
  @DisplayName("전체 기관 목록 가져오기 for finance")
  void listFinanceOrganizations_success() {
    // Given
    ConnectOrganizationEntity connectOrganizationEntity = getConnectOrganizationEntity();
    connectOrganizationRepository.save(connectOrganizationEntity);

    // When
    List<OrganizationForFinance> organizationForFinances = organizationPublishService.listFinanceOrganizations();

    // Then
    assertThat(organizationForFinances.size()).isEqualTo(1);
    assertThat(organizationForFinances).usingRecursiveComparison().isEqualTo(List.of(getOrganizationForFinance()));
  }

  @Test
  @DisplayName("특정 사용자의 연결된 기관 목록 가져오기 for finance")
  void listConnectedFinanceOrganizations_success() {
    // given
    ConnectOrganizationEntity connectOrganizationEntity = getConnectOrganizationEntity();
    connectOrganizationRepository.save(connectOrganizationEntity);

    OauthTokenEntity oauthTokenEntity = getOauthTokenEntity();
    oauthTokenRepository.save(oauthTokenEntity);

    // when
    List<OrganizationForFinance> organizationForFinances = organizationPublishService
        .listConnectedFinanceOrganizations(BANKSALAD_USER_ID);

    // then
    assertThat(organizationForFinances.size()).isEqualTo(1);
    assertThat(organizationForFinances).usingRecursiveComparison().isEqualTo(List.of(getOrganizationForFinance()));
  }

  private ConnectOrganizationEntity getConnectOrganizationEntity() {
    return ConnectOrganizationEntity.builder()
        .sector(SECTOR)
        .industry(INDUSTRY)
        .organizationId(ORGANIZATION_ID)
        .organizationGuid(ORGANIZATION_GUID)
        .orgCode(ORGANIZATION_CODE)
        .organizationStatus(ORGANIZATION_STATUS)
        .domain(DOMAIN)
        .deleted(FALSE)
        .build();
  }

  private OauthTokenEntity getOauthTokenEntity() {
    return OauthTokenEntity.builder()
        .syncedAt(LocalDateTime.now().minusDays(1))
        .banksaladUserId(BANKSALAD_USER_ID)
        .organizationId(ORGANIZATION_ID)
        .consentId(CONSENT_ID)
        .authorizationCode(AUTHORIZATION_CODE)
        .accessToken(ACCESS_TOKEN)
        .refreshToken(REFRESH_TOKEN)
        .accessTokenExpiresAt(ACCESS_TOKEN_EXPIRES_AT)
        .accessTokenExpiresIn(ACCESS_TOKEN_EXPIRES_IN)
        .refreshTokenExpiresAt(REFRESH_TOKEN_EXPIRES_AT)
        .refreshTokenExpiresIn(REFRESH_TOKEN_EXPIRES_IN)
        .tokenType(TOKEN_TYPE)
        .scope(SCOPE)
        .issuedAt(LocalDateTime.now().minusDays(1))
        .refreshedAt(LocalDateTime.now().minusDays(1))
        .build();
  }

  private OrganizationForFinance getOrganizationForFinance() {
    return OrganizationForFinance.builder()
        .organizationGuid(ORGANIZATION_GUID)
        .organizationId(ORGANIZATION_ID)
        .authUrl(DOMAIN + AUTH_URI)
        .build();
  }
}
