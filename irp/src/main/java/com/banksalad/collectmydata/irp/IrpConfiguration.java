package com.banksalad.collectmydata.irp;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.banksalad.collectmydata.irp.common.db.entity.IrpEntityMarker;
import com.banksalad.collectmydata.irp.common.db.repository.IrpRepositoryMarker;

@Configuration
@ComponentScan
@EntityScan(basePackageClasses = IrpEntityMarker.class)
@EnableJpaRepositories(basePackageClasses = IrpRepositoryMarker.class)
public class IrpConfiguration {

}
