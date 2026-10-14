package com.preventa.tickets.dto;

import com.preventa.tickets.model.Ticket;
import jakarta.validation.constraints.*;
import java.time.Instant;

public class TicketDto {
  public record Generacion(
      @NotNull @Positive Long ordenId,
      @NotNull @Positive Long usuarioId,
      @NotNull @Positive Long eventoId,
      @Min(1) @Max(20) int cantidad) {}

  public record Actualizacion(@NotBlank @Size(max = 120) String titular) {}

  public record Validacion(@NotBlank String codigo, @NotNull @Positive Long eventoId) {}

  public record Respuesta(
      Long id,
      String codigo,
      Long ordenId,
      Long usuarioId,
      Long eventoId,
      String titular,
      Ticket.Estado estado,
      Instant creadoEn,
      Instant usadoEn,
      Long validadoPor) {
    public static Respuesta of(Ticket t) {
      return new Respuesta(
          t.getId(),
          t.getCodigo(),
          t.getLote().getOrdenId(),
          t.getLote().getUsuarioId(),
          t.getLote().getEventoId(),
          t.getTitular(),
          t.getEstado(),
          t.getCreadoEn(),
          t.getUsadoEn(),
          t.getValidadoPor());
    }
  }
}
