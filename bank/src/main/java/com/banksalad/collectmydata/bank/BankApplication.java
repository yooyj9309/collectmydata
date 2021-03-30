package com.banksalad.collectmydata.bank;

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
public class BankApplication {

  public static void main(String[] args) {
    SpringApplication.run(BankApplication.class, args);
  }

}
