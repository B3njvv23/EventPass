package com.preventa.eventos.repository;

import com.preventa.eventos.model.Evento;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface EventoRepository extends JpaRepository<Evento, Long> {
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select e from Evento e where e.id=:id")
  Optional<Evento> locked(@Param("id") long id);
}
