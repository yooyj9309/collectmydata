package com.banksalad.collectmydata.efin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.banksalad.collectmydata.finance.context.annotation.EnableFinance;

@SpringBootApplication
@EntityScan
@EnableJpaRepositories
@EnableFinance
public class EfinApplication {

  public static void main(String[] args) {
    SpringApplication.run(EfinApplication.class, args);
  }

}
