package com.preventa.eventos.service;

import com.preventa.eventos.dto.EventoDto.*;
import com.preventa.eventos.exception.ApiException;
import com.preventa.eventos.model.*;
import com.preventa.eventos.repository.*;
import java.time.OffsetDateTime;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EventoService {
  private final EventoRepository repo;
  private final ReservaRepository reservas;

  public EventoService(EventoRepository r, ReservaRepository rr) {
    repo = r;
    reservas = rr;
  }

  public Evento get(long id) {
    return repo.findById(id).orElseThrow(ApiException::missing);
  }

  public Page<Respuesta> listar(Pageable p) {
    com.preventa.eventos.config.PaginationValidator.validate(p);
    return repo.findAll(p).map(Respuesta::of);
  }

  public Respuesta guardar(Long id, Solicitud r) {
    Evento e = id == null ? new Evento() : repo.locked(id).orElseThrow(ApiException::missing);
    if (r.capacidad() < e.getReservados())
      throw ApiException.conflict("Capacidad menor que las entradas reservadas");
    e.setNombre(r.nombre().trim());
    e.setLugar(r.lugar().trim());
    e.setFecha(r.fecha());
    e.setCapacidad(r.capacidad());
    e.setPrecio(r.precio());
    return Respuesta.of(repo.save(e));
  }

  public void eliminar(long id) {
    Evento e = repo.locked(id).orElseThrow(ApiException::missing);
    if (reservas.existsByEventoId(id)) {
      e.setActivo(false);
    } else repo.delete(e);
  }

  public Respuesta reservar(long id, SolicitudReserva r) {
    Evento e = repo.locked(id).orElseThrow(ApiException::missing);
    Reserva previous = reservas.findById(r.ordenId()).orElse(null);
    if (previous != null) {
      if (previous.isLiberada()
          || !previous.getEvento().getId().equals(id)
          || previous.getCantidad() != r.cantidad())
        throw ApiException.conflict("Reserva incompatible con la orden");
      return Respuesta.of(e);
    }
    if (!e.isActivo() || !e.getFecha().isAfter(OffsetDateTime.now()))
      throw ApiException.conflict("Evento no disponible");
    if (e.getCapacidad() - e.getReservados() < r.cantidad())
      throw ApiException.conflict("Aforo insuficiente");
    e.setReservados(e.getReservados() + r.cantidad());
    Reserva reservation = new Reserva();
    reservation.setOrdenId(r.ordenId());
    reservation.setEvento(e);
    reservation.setCantidad(r.cantidad());
    reservas.saveAndFlush(reservation);
    return Respuesta.of(e);
  }

  public void liberar(long id, long orden) {
    Evento e = repo.locked(id).orElseThrow(ApiException::missing);
    Reserva r = reservas.findById(orden).orElseThrow(ApiException::missing);
    if (!r.getEvento().getId().equals(id)) throw ApiException.conflict("Reserva de otro evento");
    if (!r.isLiberada()) {
      e.setReservados(e.getReservados() - r.getCantidad());
      r.setLiberada(true);
    }
  }
}
