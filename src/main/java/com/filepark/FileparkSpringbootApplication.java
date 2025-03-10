package com.filepark;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({ "controller", "service", "config", "dto", "middleware" })
@MapperScan({ "mapper" })
public class FileparkSpringbootApplication {
	public static void main(String[] args) {
		SpringApplication.run(FileparkSpringbootApplication.class, args);
	}
}
