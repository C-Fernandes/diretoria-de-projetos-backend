package com.ufrn.imd.diretoriadeprojetos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource(value = "classpath:secret.properties", ignoreResourceNotFound = false)
public class DiretoriadeprojetosApplication {
	public static void main(String[] args) {
		SpringApplication.run(DiretoriadeprojetosApplication.class, args);
	}
}
