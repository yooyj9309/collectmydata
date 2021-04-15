package com.banksalad.collectmydata.finance.test.template.dto;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.common.db.entity.UserSyncStatusEntity;

import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@ToString
@Builder
@Getter
@Setter
public class TestCase<GParent, Parent, Main, Child> {

  private int id;

  // 이 테스트케이스를 위한 @DisplayName으로 지정된다.
  @NotNull
  private String displayName;

  /* Prepare */

  // 일부 API의 경우 summary가 조부모가 된다 (예: car insurance)
  private List<GParent> gParentEntities;

  // 부모는 메인을 위한 search_timestamp나 transaction_at을 갖는다. Summary의 경우 UserSyncStatus가 부모이다.
  private List<Parent> parentEntities;

  // 메인(테스트 대상) 테이블의 초기 레코드를 담는다.
  @NotNull
  private List<Main> mainEntities;

  private List<Child> childEntities;

  /* Input */

  @NotNull
  private Execution execution;

  @NotNull
  private ExecutionContext executionContext;

  private List<BareRequest> requestParams;

  /* Expected */

  // 응답코드 등 최소한의 응답 객체
  @NotNull
  private List<BareResponse> expectedResponses;

  // 부모 테이블의 예상 결과 레코드 상태를 지정한다.
  private List<Parent> expectedParentEntities;

  // 메인 엔터티의 예상 결과를 지정한다.
  private List<Main> expectedMainEntities;

  // 자식 엔터티의 예상 결과를 지정한다.
  private List<Child> expectedChildEntities;

  /* Deprecated */

  @Deprecated
  private List<UserSyncStatusEntity> userSyncStatusEntities;
  @Deprecated
  private List<Object> summaryEntities;
  @Deprecated
  private LocalDateTime expectedUserSyncStatusSyncedAt;
  @Deprecated
  private BareUserSyncStatus expectedUserSyncStatus;
  @Deprecated
  private long expectedUserSyncStatusSearchTimestamp;
  @Deprecated
  private List<BareParent> expectedParents;
  @Deprecated
  private List<BareMain> expectedMains;
  @Deprecated
  private List<BareChild> expectedChildren;
}
