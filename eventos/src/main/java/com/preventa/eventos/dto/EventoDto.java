package com.preventa.eventos.dto;

import com.preventa.eventos.model.Evento;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class EventoDto {
  public record Solicitud(
      @NotBlank @Size(max = 160) String nombre,
      @NotBlank @Size(max = 240) String lugar,
      @NotNull @Future OffsetDateTime fecha,
      @Min(1) int capacidad,
      @NotNull @DecimalMin("0.00") @Digits(integer = 10, fraction = 2) BigDecimal precio) {}

  public record Respuesta(
      Long id,
      String nombre,
      String lugar,
      OffsetDateTime fecha,
      int capacidad,
      int disponibles,
      BigDecimal precio,
      boolean activo) {
    public static Respuesta of(Evento e) {
      return new Respuesta(
          e.getId(),
          e.getNombre(),
          e.getLugar(),
          e.getFecha(),
          e.getCapacidad(),
          e.getCapacidad() - e.getReservados(),
          e.getPrecio(),
          e.isActivo());
    }
  }

  public record SolicitudReserva(@NotNull @Positive Long ordenId, @Min(1) int cantidad) {}
}
