package com.preventa.ordenes.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(
    name = "ordenes",
    uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "clave_solicitud"}))
public class Orden {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "usuario_id", nullable = false)
  private Long usuarioId;

  @Column(nullable = false)
  private Long eventoId;

  @Column(name = "clave_solicitud", nullable = false, length = 80)
  private String claveSolicitud;

  @Column(length = 300)
  private String observacion;

  private int cantidad;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal precioUnitario;

  @Column(nullable = false, precision = 14, scale = 2)
  private BigDecimal total;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Estado estado = Estado.PENDIENTE;

  private Instant creadaEn = Instant.now();
  @Version private Long version;

  public enum Estado {
    PENDIENTE,
    CONFIRMADA,
    CANCELADA
  }
}
