package com.preventa.tickets.repository;

import com.preventa.tickets.model.Ticket;
import jakarta.persistence.LockModeType;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
  Optional<Ticket> findByCodigo(String codigo);

  Page<Ticket> findByLoteUsuarioId(Long id, Pageable p);

  List<Ticket> findByLoteOrdenId(Long id);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select t from Ticket t where t.codigo=:codigo")
  Optional<Ticket> locked(@Param("codigo") String codigo);
}
