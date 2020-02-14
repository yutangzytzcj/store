package cn.tedu.store;

import java.math.BigDecimal;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("cn.tedu.store.mapper")
public class StoreApplication {
	

	public static void main(String[] args) {
		SpringApplication.run(StoreApplication.class, args);
	   System.err.println("started");
	}

}
