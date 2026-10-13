package com.preventa.eventos.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Evento {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 160)
  private String nombre;

  @Column(nullable = false, length = 240)
  private String lugar;

  @Column(nullable = false)
  private OffsetDateTime fecha;

  @Column(nullable = false)
  private int capacidad;

  @Column(nullable = false)
  private int reservados;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal precio;

  private boolean activo = true;
  @Version private Long version;
}
