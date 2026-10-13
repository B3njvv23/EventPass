package com.preventa.tickets.config;

import com.preventa.tickets.exception.ApiException;
import java.util.Set;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

/** Permite solo propiedades conocidas antes de construir la consulta JPA. */
public final class PaginationValidator {
  private static final Set<String> SORT_FIELDS =
      Set.of(
          "id",
          "codigo",
          "titular",
          "estado",
          "creadoEn",
          "usadoEn",
          "validadoPor",
          "lote.ordenId",
          "lote.usuarioId",
          "lote.eventoId");

  private PaginationValidator() {}

  public static void validate(Pageable pageable) {
    for (var order : pageable.getSort()) {
      if (!SORT_FIELDS.contains(order.getProperty())) {
        throw new ApiException(
            HttpStatus.BAD_REQUEST,
            "Campo de ordenamiento invalido: "
                + order.getProperty()
                + ". Usa sort=campo,asc o sort=campo,desc. Campos permitidos: "
                + String.join(", ", new java.util.TreeSet<>(SORT_FIELDS)));
      }
    }
  }
}
