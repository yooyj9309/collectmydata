package com.banksalad.collectmydata.invest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(
    scanBasePackages = {
        "com.banksalad.collectmydata.finance",
        "com.banksalad.collectmydata.invest"
    }
)
@EnableJpaRepositories(
    basePackages = {
        "com.banksalad.collectmydata.finance",
        "com.banksalad.collectmydata.invest"
    }
)
public class InvestApplication {

  public static void main(String[] args) {
    SpringApplication.run(InvestApplication.class, args);
  }

}
