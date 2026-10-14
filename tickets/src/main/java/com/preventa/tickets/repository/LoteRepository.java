package com.preventa.tickets.repository;

import com.preventa.tickets.model.Lote;
import jakarta.persistence.LockModeType;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface LoteRepository extends JpaRepository<Lote, Long> {
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select l from Lote l where l.ordenId=:id")
  Optional<Lote> locked(@Param("id") Long id);
}
