package com.preventa.usuarios.config;

import com.preventa.usuarios.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class Access {
  private final String key;

  public Access(@Value("${eventpass.internal-key}") String key) {
    this.key = key;
  }

  public void internal(String supplied) {
    if (supplied == null
        || !java.security.MessageDigest.isEqual(
            key.getBytes(java.nio.charset.StandardCharsets.UTF_8),
            supplied.getBytes(java.nio.charset.StandardCharsets.UTF_8)))
      throw new ApiException(HttpStatus.FORBIDDEN, "Clave interna invalida");
  }

  public boolean staff(Authentication a) {
    return a.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_STAFF"));
  }

  public void requireStaff(Authentication a) {
    if (!staff(a)) throw new ApiException(HttpStatus.FORBIDDEN, "Se requiere rol STAFF");
  }

  public long user(Authentication a) {
    return Long.parseLong(((Jwt) a.getPrincipal()).getSubject());
  }

  public void owner(Authentication a, long id) {
    if (!staff(a) && user(a) != id)
      throw new ApiException(HttpStatus.FORBIDDEN, "No tienes acceso a este recurso");
  }
}
