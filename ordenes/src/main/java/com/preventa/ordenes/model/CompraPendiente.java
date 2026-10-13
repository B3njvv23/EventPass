package com.preventa.ordenes.model;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class CompraPendiente {
  @Id private Long ordenId;

  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @MapsId
  @JoinColumn(name = "orden_id")
  private Orden orden;

  private boolean enviada;
  private int intentos;
  private Instant siguienteIntento = Instant.now();

  @Column(length = 300)
  private String ultimoError;
}
