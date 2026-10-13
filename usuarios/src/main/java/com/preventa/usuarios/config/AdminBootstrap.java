package com.preventa.usuarios.config;

import com.preventa.usuarios.service.UsuarioService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;

@Configuration
public class AdminBootstrap {
  @Bean
  CommandLineRunner admin(
      UsuarioService s,
      @Value("${eventpass.bootstrap.email}") String email,
      @Value("${eventpass.bootstrap.password}") String password) {
    return args -> s.bootstrap(email, password);
  }
}
