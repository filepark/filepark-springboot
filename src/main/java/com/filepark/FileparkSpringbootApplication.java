package com.filepark;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({ "controller", "service", "config", "dto", "kakao" })
@MapperScan({ "mapper" })
public class FileparkSpringbootApplication {
	public static void main(String[] args) {
		SpringApplication.run(FileparkSpringbootApplication.class, args);
	}
}
