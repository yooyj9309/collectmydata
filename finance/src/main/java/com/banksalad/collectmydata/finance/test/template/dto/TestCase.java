package com.banksalad.collectmydata.finance.test.template.dto;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.common.db.entity.UserSyncStatusEntity;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
public class TestCase {

  // Mandatory: 이 테스트케이스를 위한 @DisplayName으로 지정된다.
  @NotNull
  private String displayName;

  /* Prepare */

  // Mandatory: 해당 API의 USS 테이블의 초기 레코드를 담는다. 보통 1건만 담는다.
  @NotNull
  private List<UserSyncStatusEntity> userSyncStatusEntities;

  // Optional: 자산목록 테이블의 초기 레코드를 담는다.
  private List<Object> summaryEntities;

  // Optional: 직계부모가 summary가 아닌 경우 설정한다. 즉 자신의 search_timestamp나 transaction_at을 갖는 부모이다.
  private List<Object> parentEntities;

  // Mandatory: 테스트 대상 테이블의 레코드를 담는다. 만일 대상이 transaction이면 transaction 테이블이 된다.
  @NotNull
  private List<Object> mainEntities;

  private List<Object> childEntities;

  /* Input */

  @NotNull
  private Execution execution;

  @NotNull
  private ExecutionContext executionContext;

  private List<BareRequest> requestParams;

  /* Expected */
  
  private boolean errorOccurred;

  private Class<?> expectedExceptionClazz;

  private String expectedExceptionMessage;
  // Mandotory: 응답코드 등 최소한의 응답 객체
  @NotNull
  private List<BareResponse> expectedResponses;

  // Mandatory: USS.syncedAt 결과값을 지정한다. 그냥 executionContext.syncStartedAt 값을 설정하면 된다.
  @NotNull
  private LocalDateTime expectedUserSyncStatusSyncedAt;

  private long expectedUserSyncStatusSearchTimestamp;

  // Optional: 부모 테이블의 결과 레코드 상태를 지정한다. 간단히 하기 위해 syncedAt만 설정한다.
  private List<BareParent> expectedParents;

  // Optional: 테스트 대상 테이블의 결과 레코드 상태를 지정한다. 간단히 하기 위해 syncedAt만 설정한다.
  private List<BareMain> expectedMains;

  // Optional: 테스트 대상 테이블의 결과 레코드 상태를 지정한다. 전체 필드를 설정한다.
  private List<Object> expectedMainEntities;

  // Optional: 테스트 대상 테이블의 자식 결과 레코드 상태를 지정한다. 간단히 하기 위해 syncedAt만 설정한다.
  private List<BareChild> expectedChild;

  private List<Object> expectedChildEntities;
}
