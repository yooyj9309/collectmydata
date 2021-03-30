package com.banksalad.collectmydata.capital;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.banksalad.collectmydata.finance.context.annotation.EnableFinance;

@SpringBootApplication
@EntityScan
@EnableJpaRepositories
@EnableFinance
public class CapitalApplication {

  public static void main(String[] args) {
    SpringApplication.run(CapitalApplication.class, args);
  }

}
