package com.preventa.eventos.controller;

import com.preventa.eventos.config.Access;
import com.preventa.eventos.dto.EventoDto.*;
import com.preventa.eventos.service.EventoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@io.swagger.v3.oas.annotations.tags.Tag(name = "ReservaController")
@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "internalKey")
@RestController
@RequestMapping("/internal/eventos")
public class ReservaController {
  private final EventoService s;
  private final Access access;

  public ReservaController(EventoService s, Access a) {
    this.s = s;
    access = a;
  }

  @GetMapping("/{id}")
  public Respuesta get(@PathVariable long id, @RequestHeader("X-Internal-Key") String key) {
    access.internal(key);
    return Respuesta.of(s.get(id));
  }

  @PostMapping("/{id}/reservas")
  public Respuesta reservar(
      @PathVariable long id,
      @RequestHeader("X-Internal-Key") String key,
      @Valid @RequestBody SolicitudReserva r) {
    access.internal(key);
    return s.reservar(id, r);
  }

  @DeleteMapping("/{id}/reservas/{ordenId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void liberar(
      @PathVariable long id,
      @PathVariable long ordenId,
      @RequestHeader("X-Internal-Key") String key) {
    access.internal(key);
    s.liberar(id, ordenId);
  }
}
