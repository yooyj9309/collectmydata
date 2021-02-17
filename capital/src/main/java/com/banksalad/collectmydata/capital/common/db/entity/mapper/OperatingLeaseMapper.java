package com.banksalad.collectmydata.capital.common.db.entity.mapper;

import com.banksalad.collectmydata.capital.account.dto.Account;
import com.banksalad.collectmydata.capital.common.db.entity.OperatingLeaseEntity;
import com.banksalad.collectmydata.capital.lease.dto.OperatingLeaseBasicResponse;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OperatingLeaseMapper {

  @Mappings(
      value = {
          @Mapping(target = "operatingLeaseId", ignore = true),
          @Mapping(source = "account.accountNum", target = "accountNum"),
          @Mapping(source = "account.seqno", target = "seqno"),

          @Mapping(source = "context.syncStartedAt", target = "syncedAt"),
          @Mapping(source = "response.holderName", target = "holderName"),
          @Mapping(source = "response.issueDate", target = "issueDate", dateFormat = "yyyyMMdd"),
          @Mapping(source = "response.expDate", target = "expDate", dateFormat = "yyyyMMdd"),
          @Mapping(source = "response.repayDate", target = "repayDate"),
          @Mapping(source = "response.repayMethod", target = "repayMethod"),
          @Mapping(source = "response.repayOrgCode", target = "repayOrgCode"),
          @Mapping(source = "response.repayAccountNum", target = "repayAccountNum"),
          @Mapping(source = "response.nextRepayDate", target = "nextRepayDate", dateFormat = "yyyyMMdd"),
      }
  )
  void merge(ExecutionContext context, Account account, OperatingLeaseBasicResponse response,
      @MappingTarget OperatingLeaseEntity entity);

  @Mappings(
      value = {
          @Mapping(target = "rspCode", ignore = true),
          @Mapping(target = "rspMsg", ignore = true),
          @Mapping(target = "searchTimestamp", ignore = true),
          @Mapping(target = "issueDate", dateFormat = "yyyyMMdd"),
          @Mapping(target = "expDate", dateFormat = "yyyyMMdd"),
          @Mapping(target = "nextRepayDate", dateFormat = "yyyyMMdd"),
      }
  )
  OperatingLeaseBasicResponse entityToOperatingLeaseBasicResponse(OperatingLeaseEntity entity);
}
