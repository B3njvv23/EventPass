package com.preventa.tickets.model;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Ticket {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 36)
  private String codigo;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "orden_id")
  private Lote lote;

  @Column(length = 120)
  private String titular;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Estado estado = Estado.EMITIDO;

  private Instant creadoEn = Instant.now();
  private Instant usadoEn;
  private Long validadoPor;

  public enum Estado {
    EMITIDO,
    USADO,
    CANCELADO
  }
}
