package fr.chevallier31.my_batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MyBatchApplication {

	public static void main(String[] args) {
		System.exit(SpringApplication.exit(
				SpringApplication.run(MyBatchApplication.class, args)));
	}

}
