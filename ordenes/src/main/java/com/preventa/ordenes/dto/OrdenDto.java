package com.preventa.ordenes.dto;

import com.preventa.ordenes.model.Orden;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;

public class OrdenDto {
  public record Solicitud(
      @NotNull @Positive Long eventoId,
      @Min(1) @Max(20) int cantidad,
      @NotBlank @Size(max = 80) String claveSolicitud) {}

  public record Actualizacion(@NotNull @Size(max = 300) String observacion) {}

  public record Respuesta(
      Long id,
      Long usuarioId,
      Long eventoId,
      int cantidad,
      BigDecimal precioUnitario,
      BigDecimal total,
      Orden.Estado estado,
      Instant creadaEn,
      String observacion) {
    public static Respuesta of(Orden o) {
      return new Respuesta(
          o.getId(),
          o.getUsuarioId(),
          o.getEventoId(),
          o.getCantidad(),
          o.getPrecioUnitario(),
          o.getTotal(),
          o.getEstado(),
          o.getCreadaEn(),
          o.getObservacion());
    }
  }
}
