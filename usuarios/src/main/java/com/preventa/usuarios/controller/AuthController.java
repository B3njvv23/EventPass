package com.preventa.usuarios.controller;

import com.preventa.usuarios.dto.UsuarioDto.*;
import com.preventa.usuarios.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@io.swagger.v3.oas.annotations.tags.Tag(name = "AuthController")
@io.swagger.v3.oas.annotations.security.SecurityRequirements
@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final UsuarioService s;

  public AuthController(UsuarioService s) {
    this.s = s;
  }

  @PostMapping("/registro")
  public ResponseEntity<Respuesta> registro(@Valid @RequestBody Registro r) {
    Respuesta u = s.registrar(r);
    return ResponseEntity.created(java.net.URI.create("/api/usuarios/" + u.id())).body(u);
  }

  @PostMapping("/login")
  public Token login(@Valid @RequestBody Login r) {
    return s.login(r);
  }
}
