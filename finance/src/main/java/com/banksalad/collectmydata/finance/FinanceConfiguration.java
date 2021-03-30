package com.banksalad.collectmydata.finance;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.banksalad.collectmydata.finance.common.db.entity.FinanceEntityMarker;
import com.banksalad.collectmydata.finance.common.db.repository.FinanceRepositoryMarker;

@Configuration
@ComponentScan
@EntityScan(basePackageClasses = FinanceEntityMarker.class)
@EnableJpaRepositories(basePackageClasses = FinanceRepositoryMarker.class)
public class FinanceConfiguration {

}
