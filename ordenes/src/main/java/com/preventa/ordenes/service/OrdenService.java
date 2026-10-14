package com.preventa.ordenes.service;

import com.preventa.ordenes.client.MicroservicesClient;
import com.preventa.ordenes.dto.OrdenDto.*;
import com.preventa.ordenes.exception.ApiException;
import com.preventa.ordenes.model.CompraPendiente;
import com.preventa.ordenes.model.Orden;
import com.preventa.ordenes.repository.CompraPendienteRepository;
import com.preventa.ordenes.repository.OrdenRepository;
import java.math.BigDecimal;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrdenService {
  private final OrdenRepository repo;
  private final MicroservicesClient client;
  private final CompraPendienteRepository pendientes;

  public OrdenService(
      OrdenRepository r, MicroservicesClient c, CompraPendienteRepository pendientes) {
    repo = r;
    client = c;
    this.pendientes = pendientes;
  }

  public Respuesta crear(long user, Solicitud r) {
    Orden existing = repo.findByUsuarioIdAndClaveSolicitud(user, r.claveSolicitud()).orElse(null);
    if (existing != null) {
      if (!existing.getEventoId().equals(r.eventoId()) || existing.getCantidad() != r.cantidad())
        throw ApiException.conflict("Clave de solicitud utilizada con otros datos");
      return Respuesta.of(existing);
    }
    var event = client.evento(r.eventoId());
    if (event == null || !event.activo()) throw ApiException.conflict("Evento no disponible");
    Orden o = new Orden();
    o.setUsuarioId(user);
    o.setEventoId(r.eventoId());
    o.setCantidad(r.cantidad());
    o.setClaveSolicitud(r.claveSolicitud());
    o.setPrecioUnitario(event.precio());
    o.setTotal(event.precio().multiply(BigDecimal.valueOf(r.cantidad())));
    repo.saveAndFlush(o);
    CompraPendiente job = new CompraPendiente();
    job.setOrden(o);
    pendientes.save(job);
    return Respuesta.of(o);
  }

  public Respuesta get(long id) {
    return Respuesta.of(repo.findById(id).orElseThrow(ApiException::missing));
  }

  public Page<Respuesta> listar(Long user, Pageable p) {
    com.preventa.ordenes.config.PaginationValidator.validate(p);
    return (user == null ? repo.findAll(p) : repo.findByUsuarioId(user, p)).map(Respuesta::of);
  }

  public Respuesta actualizar(long id, Actualizacion r) {
    Orden o = repo.locked(id).orElseThrow(ApiException::missing);
    if (o.getEstado() != Orden.Estado.PENDIENTE)
      throw ApiException.conflict("Solo se puede editar una orden pendiente");
    o.setObservacion(r.observacion().trim());
    return Respuesta.of(o);
  }

  public Respuesta procesar(long id) {
    Orden o = repo.locked(id).orElseThrow(ApiException::missing);
    if (o.getEstado() == Orden.Estado.CONFIRMADA) return Respuesta.of(o);
    if (o.getEstado() == Orden.Estado.CANCELADA) throw ApiException.conflict("Orden cancelada");
    client.reservar(o.getEventoId(), id, o.getCantidad());
    client.generar(id, o.getUsuarioId(), o.getEventoId(), o.getCantidad());
    o.setEstado(Orden.Estado.CONFIRMADA);
    return Respuesta.of(o);
  }

  public void cancelar(long id) {
    Orden o = repo.locked(id).orElseThrow(ApiException::missing);
    if (o.getEstado() == Orden.Estado.CANCELADA) return;
    client.cancelarTickets(id);
    client.liberar(o.getEventoId(), id);
    o.setEstado(Orden.Estado.CANCELADA);
  }
}
