package com.preventa.tickets.controller;

import com.preventa.tickets.config.Access;
import com.preventa.tickets.dto.TicketDto.*;
import com.preventa.tickets.service.TicketService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@io.swagger.v3.oas.annotations.tags.Tag(name = "GeneracionController")
@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "internalKey")
@RestController
@RequestMapping("/internal/tickets")
public class GeneracionController {
  private final TicketService s;
  private final Access access;

  public GeneracionController(TicketService s, Access a) {
    this.s = s;
    access = a;
  }

  @PostMapping
  public List<Respuesta> generar(
      @RequestHeader("X-Internal-Key") String key, @Valid @RequestBody Generacion r) {
    access.internal(key);
    return s.generar(r);
  }

  @DeleteMapping("/ordenes/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void cancelar(@PathVariable long id, @RequestHeader("X-Internal-Key") String key) {
    access.internal(key);
    s.cancelarOrden(id);
  }
}
