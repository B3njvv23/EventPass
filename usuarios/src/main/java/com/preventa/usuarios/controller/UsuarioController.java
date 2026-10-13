package com.preventa.usuarios.controller;

import com.preventa.usuarios.config.Access;
import com.preventa.usuarios.dto.UsuarioDto.*;
import com.preventa.usuarios.model.Usuario;
import com.preventa.usuarios.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@io.swagger.v3.oas.annotations.tags.Tag(name = "UsuarioController")
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
  private final UsuarioService s;
  private final Access access;

  public UsuarioController(UsuarioService s, Access a) {
    this.s = s;
    access = a;
  }

  @GetMapping
  public Page<Respuesta> listar(
      Authentication a,
      @org.springdoc.core.annotations.ParameterObject
          @org.springframework.data.web.PageableDefault(size = 20, sort = "id")
          Pageable p) {
    access.requireStaff(a);
    return s.listar(p);
  }

  @GetMapping("/me")
  public Respuesta me(Authentication a) {
    return Respuesta.of(s.get(access.user(a)));
  }

  @GetMapping("/{id}")
  public Respuesta get(@PathVariable long id, Authentication a) {
    access.owner(a, id);
    return Respuesta.of(s.get(id));
  }

  @PutMapping("/{id}")
  public Respuesta update(
      @PathVariable long id, @Valid @RequestBody Actualizacion r, Authentication a) {
    access.owner(a, id);
    return s.actualizar(id, r);
  }

  @PatchMapping("/{id}/rol")
  public Respuesta rol(@PathVariable long id, @RequestParam Usuario.Rol rol, Authentication a) {
    access.requireStaff(a);
    return s.rol(id, rol);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable long id, Authentication a) {
    access.owner(a, id);
    s.eliminar(id);
  }
}
