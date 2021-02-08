package com.banksalad.collectmydata.capital;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.capital.account.AccountService;
import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.capital.lease.service.LeaseService;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.logging.CollectLogbackJsonLayout;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

@Slf4j
@Service
@RequiredArgsConstructor
public class CapitalServiceImpl implements CapitalService {

  private final AccountService accountService;
  private final LeaseService leaseService;
  private final CapitalPublishService capitalPublishService;

  /**
   * kafka consumer하는 서비스에서 호출될 메서드
   *
   * @param banksaladUserId
   * @param organizationId
   */
  @Override
  public void sync(long banksaladUserId, String organizationId) {
    try {
      // Organization 정보 조히 ( domain setting, scope조회를 위해서라도 필요)
      // 토큰갱신
      // ExecutionContext 생성
      // 서비스 호출

      Organization organization = null;
      ExecutionContext executionContext = ExecutionContext.builder().build();
      MDC.put(CollectLogbackJsonLayout.JSON_KEY_BANKSALAD_USER_ID, String.valueOf(banksaladUserId));
      MDC.put(CollectLogbackJsonLayout.JSON_KEY_ORGANIZATION_ID, organizationId);
      log.info("CapitalService.sync start");

      accountService.syncAccounts(executionContext, organization);
      /**
       * (scope에서 OK인 경우에만.
       * 6.7.1 조회 -> 6.7.2, 6.7.3 조회 (계좌) 계좌정보  6.7.4, 6.7.5, 6.7.6 사용
       * 6.7.4 조회(거래내역 조회)
       * 6.7.5 조회 -> 6.7.6 (운용리스)
       */

      // publish 서비스
      // client 리턴 -> 이부분 갑자기 기억이 안나네요, collect에 주기위해 다시 producer 하는게 맞는지..?

    } catch (Exception e) {
      log.error("Sync user error: {}", e.getMessage(), e);
    } finally {
      log.info("CollectbankGrpcService.syncUser end");
      MDC.clear();
    }
  }
}
