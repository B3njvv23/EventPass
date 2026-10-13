package com.preventa.eventos.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Reserva {
  @Id private Long ordenId;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "evento_id")
  private Evento evento;

  private int cantidad;
  private boolean liberada;
}
