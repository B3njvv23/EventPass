package com.preventa.ordenes.controller;

import com.preventa.ordenes.config.Access;
import com.preventa.ordenes.dto.OrdenDto.*;
import com.preventa.ordenes.service.OrdenService;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@io.swagger.v3.oas.annotations.tags.Tag(name = "OrdenController")
@RestController
@RequestMapping("/api/ordenes")
public class OrdenController {
  private final OrdenService s;
  private final Access access;

  public OrdenController(OrdenService s, Access a) {
    this.s = s;
    access = a;
  }

  @GetMapping
  public Page<Respuesta> listar(
      Authentication a,
      @org.springdoc.core.annotations.ParameterObject
          @org.springframework.data.web.PageableDefault(size = 20, sort = "id")
          Pageable p) {
    return s.listar(access.staff(a) ? null : access.user(a), p);
  }

  @GetMapping("/{id}")
  public Respuesta get(@PathVariable long id, Authentication a) {
    Respuesta o = s.get(id);
    access.owner(a, o.usuarioId());
    return o;
  }

  @PostMapping
  public ResponseEntity<Respuesta> create(@Valid @RequestBody Solicitud r, Authentication a) {
    Respuesta o = s.crear(access.user(a), r);
    return ResponseEntity.created(java.net.URI.create("/api/ordenes/" + o.id())).body(o);
  }

  @PutMapping("/{id}")
  public Respuesta update(
      @PathVariable long id, @Valid @RequestBody Actualizacion r, Authentication a) {
    get(id, a);
    return s.actualizar(id, r);
  }

  @PostMapping("/{id}/procesar")
  public Respuesta process(@PathVariable long id, Authentication a) {
    get(id, a);
    return s.procesar(id);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void cancel(@PathVariable long id, Authentication a) {
    get(id, a);
    s.cancelar(id);
  }
}
