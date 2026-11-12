package com.angelbelzunce.ingresos_y_gastos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;

// La autenticación es con JWT propio: no hace falta el usuario en memoria que Spring Security crea por defecto.
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class IngresosYGastosApplication {

	public static void main(String[] args) {
		SpringApplication.run(IngresosYGastosApplication.class, args);
	}

}
