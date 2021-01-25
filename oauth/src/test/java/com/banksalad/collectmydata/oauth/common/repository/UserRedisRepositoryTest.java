package com.banksalad.collectmydata.oauth.common.repository;

import com.banksalad.collectmydata.oauth.common.config.TestRedisConfiguration;
import com.banksalad.collectmydata.oauth.common.db.UserEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestRedisConfiguration.class)
@DisplayName("UserRedisRepository Test")
public class UserRedisRepositoryTest {

  @Autowired
  private UserRedisRepository userRedisRepository;


  private LocalDateTime now = LocalDateTime.now();
  private String key = "key";
  private Long userId = 1L;
  private String organizationId = "shinhancard";
  private String organizationCode = "000";
  private String os = "android";

  @Test
  @DisplayName("redis 저장 및 조회 테스트- 정상 플로우 테스트")
  public void userRedisRepository_success() {
    UserEntity entity = generateUserEntity();
    userRedisRepository.setUserInfo(key, entity);

    UserEntity responseEntity = userRedisRepository.getUserInfo(key).get();
    assertThat(responseEntity).usingRecursiveComparison().isEqualTo(generateUserEntity());
  }

  @Test
  @DisplayName("redis 저장 및 조회 테스트 - 저장하지 않은 키로 조회하는 예외처리 테스트")
  public void userRedisRepository_failure() {
    UserEntity entity = generateUserEntity();
    userRedisRepository.setUserInfo(key, entity);

    String invalidKey = "invalidKey";
    Exception responseException = assertThrows(
        Exception.class,
        () -> userRedisRepository.getUserInfo(invalidKey).get()
    );

    assertThat(responseException).isInstanceOf(NullPointerException.class);
  }

  private UserEntity generateUserEntity() {
    return UserEntity.builder()
        .banksaladUserId(userId)
        .organizationId(organizationId)
        .organizationCode(organizationCode)
        .os(os)
        .createdAt(now)
        .build();
  }
}
