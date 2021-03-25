package com.banksalad.collectmydata.ginsu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(
    scanBasePackages = {
        "com.banksalad.collectmydata.finance",
        "com.banksalad.collectmydata.ginsu"
    }
)
@EnableJpaRepositories(
    basePackages = {
        "com.banksalad.collectmydata.finance",
        "com.banksalad.collectmydata.ginsu"
    }
)
public class GinsuApplication {

  public static void main(String[] args) {
    SpringApplication.run(GinsuApplication.class, args);
  }
}
