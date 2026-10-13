package com.preventa.tickets.model;

import jakarta.persistence.*;
import java.util.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Lote {
  @Id private Long ordenId;
  private Long usuarioId;
  private Long eventoId;
  private int cantidad;

  @OneToMany(mappedBy = "lote", cascade = CascadeType.ALL)
  private List<Ticket> tickets = new ArrayList<>();
}
