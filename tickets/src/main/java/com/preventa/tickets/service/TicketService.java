package com.preventa.tickets.service;

import com.preventa.tickets.dto.TicketDto.*;
import com.preventa.tickets.exception.ApiException;
import com.preventa.tickets.model.*;
import com.preventa.tickets.repository.*;
import java.time.Instant;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TicketService {
  private final TicketRepository repo;
  private final LoteRepository lotes;

  public TicketService(TicketRepository r, LoteRepository l) {
    repo = r;
    lotes = l;
  }

  public Respuesta get(long id) {
    return Respuesta.of(repo.findById(id).orElseThrow(ApiException::missing));
  }

  public Page<Respuesta> listar(Long user, Pageable p) {
    com.preventa.tickets.config.PaginationValidator.validate(p);
    return (user == null ? repo.findAll(p) : repo.findByLoteUsuarioId(user, p)).map(Respuesta::of);
  }

  public List<Respuesta> generar(Generacion r) {
    Lote l = lotes.locked(r.ordenId()).orElse(null);
    if (l != null) {
      if (!l.getUsuarioId().equals(r.usuarioId())
          || !l.getEventoId().equals(r.eventoId())
          || l.getCantidad() != r.cantidad())
        throw ApiException.conflict("Datos incompatibles con el lote existente");
      if (l.getTickets().stream().anyMatch(t -> t.getEstado() == Ticket.Estado.CANCELADO))
        throw ApiException.conflict("Lote cancelado");
      return l.getTickets().stream().map(Respuesta::of).toList();
    }
    l = new Lote();
    l.setOrdenId(r.ordenId());
    l.setUsuarioId(r.usuarioId());
    l.setEventoId(r.eventoId());
    l.setCantidad(r.cantidad());
    for (int i = 0; i < r.cantidad(); i++) {
      Ticket t = new Ticket();
      t.setCodigo(UUID.randomUUID().toString());
      t.setLote(l);
      l.getTickets().add(t);
    }
    lotes.saveAndFlush(l);
    return l.getTickets().stream().map(Respuesta::of).toList();
  }

  public Respuesta actualizar(long id, Actualizacion r) {
    Ticket initial = repo.findById(id).orElseThrow(ApiException::missing);
    lotes.locked(initial.getLote().getOrdenId()).orElseThrow(ApiException::missing);
    Ticket t = repo.locked(initial.getCodigo()).orElseThrow(ApiException::missing);
    if (t.getEstado() != Ticket.Estado.EMITIDO)
      throw ApiException.conflict("Solo se puede editar un ticket emitido");
    t.setTitular(r.titular().trim());
    return Respuesta.of(t);
  }

  public void eliminar(long id) {
    Ticket initial = repo.findById(id).orElseThrow(ApiException::missing);
    lotes.locked(initial.getLote().getOrdenId()).orElseThrow(ApiException::missing);
    Ticket t = repo.locked(initial.getCodigo()).orElseThrow(ApiException::missing);
    if (t.getEstado() == Ticket.Estado.USADO) throw ApiException.conflict("Ticket utilizado");
    t.setEstado(Ticket.Estado.CANCELADO);
  }

  public Respuesta validar(Validacion r, long staff) {
    Ticket initial = repo.findByCodigo(r.codigo()).orElseThrow(ApiException::missing);
    lotes.locked(initial.getLote().getOrdenId()).orElseThrow(ApiException::missing);
    Ticket t = repo.locked(r.codigo()).orElseThrow(ApiException::missing);
    if (!t.getLote().getEventoId().equals(r.eventoId()))
      throw ApiException.conflict("El ticket pertenece a otro evento");
    if (t.getEstado() != Ticket.Estado.EMITIDO)
      throw ApiException.conflict("Ticket ya utilizado o cancelado");
    t.setEstado(Ticket.Estado.USADO);
    t.setUsadoEn(Instant.now());
    t.setValidadoPor(staff);
    return Respuesta.of(t);
  }

  public void cancelarOrden(long id) {
    Lote l = lotes.locked(id).orElse(null);
    if (l == null) return;
    List<Ticket> tickets = repo.findByLoteOrdenId(id);
    if (tickets.stream().anyMatch(t -> t.getEstado() == Ticket.Estado.USADO))
      throw ApiException.conflict("La orden tiene tickets utilizados");
    tickets.forEach(t -> t.setEstado(Ticket.Estado.CANCELADO));
  }
}
