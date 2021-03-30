package com.banksalad.collectmydata.finance.context.annotation;

import org.springframework.context.annotation.Import;

import com.banksalad.collectmydata.finance.FinanceConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(FinanceConfiguration.class)
public @interface EnableFinance {

}
