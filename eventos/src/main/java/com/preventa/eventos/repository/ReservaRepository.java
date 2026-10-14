package com.preventa.eventos.repository;

import com.preventa.eventos.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
  boolean existsByEventoId(Long id);
}
