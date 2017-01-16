package com.connectionchecker;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import com.connectionchecker.service.DirectConnectionChecker;

/**
 * Welcome to the bus route finder service :)
 *
 */
@SpringBootApplication
public class DirectConnectionChekerApp {
	
	@Autowired
	private Environment env;
	
	public static void main( String[] args ) {
		SpringApplication.run(DirectConnectionChekerApp.class, args);
	}

	@Bean
	InitializingBean saveData(DirectConnectionChecker directConnectionChecker){
		return () -> directConnectionChecker.loadData(env.getProperty("datafile"));
	}
}
