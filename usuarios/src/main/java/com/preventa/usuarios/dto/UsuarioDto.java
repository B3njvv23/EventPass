package com.preventa.usuarios.dto;

import com.preventa.usuarios.model.Usuario;
import jakarta.validation.constraints.*;

public class UsuarioDto {
  public record Registro(
      @NotBlank @Size(max = 120) String nombre,
      @NotBlank @Email @Size(max = 160) String email,
      @NotBlank @Size(min = 8, max = 72) String password) {}

  public record Login(@NotBlank @Email String email, @NotBlank String password) {}

  public record Actualizacion(
      @NotBlank @Size(max = 120) String nombre, @NotBlank @Email @Size(max = 160) String email) {}

  public record Respuesta(Long id, String nombre, String email, Usuario.Rol rol, boolean activo) {
    public static Respuesta of(Usuario u) {
      return new Respuesta(u.getId(), u.getNombre(), u.getEmail(), u.getRol(), u.isActivo());
    }
  }

  public record Token(String accessToken, String tokenType, long expiresIn, Respuesta usuario) {}
}
