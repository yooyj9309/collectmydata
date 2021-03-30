package com.banksalad.collectmydata.irp.context.annotation;

import org.springframework.context.annotation.Import;

import com.banksalad.collectmydata.irp.IrpConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(IrpConfiguration.class)
public @interface EnableIrp {

}
