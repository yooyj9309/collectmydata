package com.banksalad.collectmydata.capital.common.db.entity.mapper;

import com.banksalad.collectmydata.capital.common.db.entity.OperatingLeaseEntity;
import com.banksalad.collectmydata.capital.common.dto.AccountSummary;
import com.banksalad.collectmydata.capital.oplease.dto.OperatingLease;
import com.banksalad.collectmydata.capital.oplease.dto.OperatingLeaseBasicResponse;
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
          @Mapping(source = "context.syncStartedAt", target = "syncedAt"),
          @Mapping(source = "response.issueDate", target = "issueDate", dateFormat = "yyyyMMdd"),
          @Mapping(source = "response.expDate", target = "expDate", dateFormat = "yyyyMMdd"),
          @Mapping(source = "response.nextRepayDate", target = "nextRepayDate", dateFormat = "yyyyMMdd"),
      }
  )
  void merge(ExecutionContext context, AccountSummary accountSummary, OperatingLeaseBasicResponse response,
      @MappingTarget OperatingLeaseEntity entity);

  @Mappings(
      value = {
          @Mapping(target = "issueDate", dateFormat = "yyyyMMdd"),
          @Mapping(target = "expDate", dateFormat = "yyyyMMdd"),
          @Mapping(target = "nextRepayDate", dateFormat = "yyyyMMdd"),
      }
  )
  OperatingLeaseBasicResponse entityToOperatingLeaseBasicResponse(OperatingLeaseEntity entity);

  OperatingLease operatingLeaseAssembler(OperatingLeaseBasicResponse response, AccountSummary accountSummary);
}
