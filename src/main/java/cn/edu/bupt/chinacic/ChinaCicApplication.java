package cn.edu.bupt.chinacic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class ChinaCicApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChinaCicApplication.class, args);
	}
}
