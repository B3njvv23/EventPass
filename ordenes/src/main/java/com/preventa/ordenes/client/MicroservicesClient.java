package com.preventa.ordenes.client;

import com.preventa.ordenes.exception.ApiException;
import java.math.BigDecimal;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;

@Component
public class MicroservicesClient {
  private final RestClient eventos, tickets;

  public MicroservicesClient(
      @Value("${eventpass.eventos-url}") String events,
      @Value("${eventpass.tickets-url}") String ticketUrl,
      @Value("${eventpass.internal-key}") String key) {
    SimpleClientHttpRequestFactory f = new SimpleClientHttpRequestFactory();
    f.setConnectTimeout(Duration.ofSeconds(5));
    f.setReadTimeout(Duration.ofSeconds(10));
    eventos =
        RestClient.builder()
            .baseUrl(events)
            .requestFactory(f)
            .defaultHeader("X-Internal-Key", key)
            .build();
    tickets =
        RestClient.builder()
            .baseUrl(ticketUrl)
            .requestFactory(f)
            .defaultHeader("X-Internal-Key", key)
            .build();
  }

  public record Evento(Long id, int disponibles, BigDecimal precio, boolean activo) {}

  public record Reserva(Long ordenId, int cantidad) {}

  public record Generacion(Long ordenId, Long usuarioId, Long eventoId, int cantidad) {}

  public Evento evento(long id) {
    try {
      return eventos.get().uri("/internal/eventos/{id}", id).retrieve().body(Evento.class);
    } catch (RestClientResponseException ex) {
      throw map(ex);
    }
  }

  public Evento reservar(long evento, long orden, int cantidad) {
    try {
      return eventos
          .post()
          .uri("/internal/eventos/{id}/reservas", evento)
          .body(new Reserva(orden, cantidad))
          .retrieve()
          .body(Evento.class);
    } catch (RestClientResponseException ex) {
      throw map(ex);
    }
  }

  public void liberar(long evento, long orden) {
    try {
      eventos
          .delete()
          .uri("/internal/eventos/{id}/reservas/{orden}", evento, orden)
          .retrieve()
          .toBodilessEntity();
    } catch (RestClientResponseException ex) {
      if (ex.getStatusCode().value() != 404) throw map(ex);
    }
  }

  public void generar(long orden, long user, long evento, int cantidad) {
    try {
      tickets
          .post()
          .uri("/internal/tickets")
          .body(new Generacion(orden, user, evento, cantidad))
          .retrieve()
          .toBodilessEntity();
    } catch (RestClientResponseException ex) {
      throw map(ex);
    }
  }

  public void cancelarTickets(long orden) {
    try {
      tickets.delete().uri("/internal/tickets/ordenes/{id}", orden).retrieve().toBodilessEntity();
    } catch (RestClientResponseException ex) {
      throw map(ex);
    }
  }

  private ApiException map(RestClientResponseException e) {
    int status = e.getStatusCode().value();
    return new ApiException(
        status == 404
            ? HttpStatus.NOT_FOUND
            : status == 409 ? HttpStatus.CONFLICT : HttpStatus.BAD_GATEWAY,
        status == 409
            ? "Operacion rechazada: aforo insuficiente, tickets utilizados o estado incompatible"
            : status == 404
                ? "Recurso remoto no encontrado"
                : "Error de comunicacion con microservicio");
  }
}
