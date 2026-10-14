package com.preventa.tickets.controller;

import com.preventa.tickets.config.Access;
import com.preventa.tickets.dto.TicketDto.*;
import com.preventa.tickets.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@io.swagger.v3.oas.annotations.tags.Tag(name = "TicketController")
@RestController
@RequestMapping("/api/tickets")
public class TicketController {
  private final TicketService s;
  private final Access access;

  public TicketController(TicketService s, Access a) {
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
    Respuesta t = s.get(id);
    access.owner(a, t.usuarioId());
    return t;
  }

  @PutMapping("/{id}")
  public Respuesta update(
      @PathVariable long id, @Valid @RequestBody Actualizacion r, Authentication a) {
    get(id, a);
    return s.actualizar(id, r);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable long id, Authentication a) {
    access.requireStaff(a);
    s.eliminar(id);
  }

  @PostMapping("/validaciones")
  public Respuesta validar(@Valid @RequestBody Validacion r, Authentication a) {
    access.requireStaff(a);
    return s.validar(r, access.user(a));
  }
}
