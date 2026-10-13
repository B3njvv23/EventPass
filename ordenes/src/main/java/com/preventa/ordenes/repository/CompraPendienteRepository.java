package com.preventa.ordenes.repository;

import com.preventa.ordenes.model.CompraPendiente;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompraPendienteRepository extends JpaRepository<CompraPendiente, Long> {
  List<CompraPendiente> findTop20ByEnviadaFalseAndSiguienteIntentoBeforeOrderByOrdenIdAsc(
      Instant now);
}
