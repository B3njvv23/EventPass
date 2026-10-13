package com.preventa.ordenes.controller;

import com.preventa.ordenes.config.Access;
import com.preventa.ordenes.dto.OrdenDto.Respuesta;
import com.preventa.ordenes.service.OrdenService;
import org.springframework.web.bind.annotation.*;

@io.swagger.v3.oas.annotations.tags.Tag(name = "ProcesamientoController")
@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "internalKey")
@RestController
@RequestMapping("/internal/ordenes")
public class ProcesamientoController {
  private final OrdenService s;
  private final Access access;

  public ProcesamientoController(OrdenService s, Access access) {
    this.s = s;
    this.access = access;
  }

  @PostMapping("/{id}/procesar")
  public Respuesta procesar(@PathVariable long id, @RequestHeader("X-Internal-Key") String key) {
    access.internal(key);
    return s.procesar(id);
  }
}
