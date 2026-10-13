package com.preventa.eventos.controller;

import com.preventa.eventos.config.Access;
import com.preventa.eventos.dto.EventoDto.*;
import com.preventa.eventos.service.EventoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@io.swagger.v3.oas.annotations.tags.Tag(name = "EventoController")
@RestController
@RequestMapping("/api/eventos")
public class EventoController {
  private final EventoService s;
  private final Access access;

  public EventoController(EventoService s, Access a) {
    this.s = s;
    access = a;
  }

  @GetMapping
  public Page<Respuesta> listar(
      @org.springdoc.core.annotations.ParameterObject
          @org.springframework.data.web.PageableDefault(size = 20, sort = "id")
          Pageable p) {
    return s.listar(p);
  }

  @GetMapping("/{id}")
  public Respuesta get(@PathVariable long id) {
    return Respuesta.of(s.get(id));
  }

  @GetMapping("/{id}/disponibilidad")
  public Respuesta disponibilidad(@PathVariable long id) {
    return get(id);
  }

  @PostMapping
  public ResponseEntity<Respuesta> create(@Valid @RequestBody Solicitud r, Authentication a) {
    access.requireStaff(a);
    Respuesta e = s.guardar(null, r);
    return ResponseEntity.created(java.net.URI.create("/api/eventos/" + e.id())).body(e);
  }

  @PutMapping("/{id}")
  public Respuesta update(
      @PathVariable long id, @Valid @RequestBody Solicitud r, Authentication a) {
    access.requireStaff(a);
    return s.guardar(id, r);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable long id, Authentication a) {
    access.requireStaff(a);
    s.eliminar(id);
  }
}
