package com.alura.literalura;

import com.alura.literalura.service.MainService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LiteraluraApplication {

	@Autowired
	private MainService mainService;

	public static void main(String[] args) {
		SpringApplication.run(LiteraluraApplication.class, args);
	}

	@PostConstruct
	public void run(){
		mainService.muestraMenu();
	}

}
