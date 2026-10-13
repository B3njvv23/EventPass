package com.preventa.ordenes.repository;

import com.preventa.ordenes.model.Orden;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface OrdenRepository extends JpaRepository<Orden, Long> {
  Optional<Orden> findByUsuarioIdAndClaveSolicitud(Long user, String key);

  Page<Orden> findByUsuarioId(Long user, Pageable p);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select o from Orden o where o.id=:id")
  Optional<Orden> locked(@Param("id") long id);
}
