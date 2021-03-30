package com.banksalad.collectmydata.referencebank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.banksalad.collectmydata.finance.context.annotation.EnableFinance;
import com.banksalad.collectmydata.irp.context.annotation.EnableIrp;

@SpringBootApplication
@EntityScan
@EnableJpaRepositories
@EnableFinance
@EnableIrp
public class ReferencebankApplication {

  public static void main(String[] args) {
    SpringApplication.run(ReferencebankApplication.class, args);
  }

}
