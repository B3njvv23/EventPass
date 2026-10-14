package com.preventa.usuarios.service;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.preventa.usuarios.dto.UsuarioDto.*;
import com.preventa.usuarios.exception.ApiException;
import com.preventa.usuarios.model.Usuario;
import com.preventa.usuarios.repository.UsuarioRepository;
import java.time.Instant;
import java.util.*;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UsuarioService {
  private final UsuarioRepository repo;
  private final PasswordEncoder passwords;
  private final JwtEncoder encoder;

  public UsuarioService(UsuarioRepository repo, PasswordEncoder passwords, SecretKeySpec key) {
    this.repo = repo;
    this.passwords = passwords;
    this.encoder = new NimbusJwtEncoder(new ImmutableSecret<>(key));
  }

  public Respuesta registrar(Registro r) {
    Usuario u = new Usuario();
    u.setNombre(r.nombre().trim());
    u.setEmail(r.email().trim().toLowerCase(Locale.ROOT));
    u.setPasswordHash(passwords.encode(r.password()));
    return Respuesta.of(repo.saveAndFlush(u));
  }

  public Token login(Login r) {
    Usuario u =
        repo.findByEmail(r.email().trim().toLowerCase(Locale.ROOT))
            .filter(Usuario::isActivo)
            .filter(x -> passwords.matches(r.password(), x.getPasswordHash()))
            .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Credenciales invalidas"));
    Instant now = Instant.now();
    JwtClaimsSet claims =
        JwtClaimsSet.builder()
            .issuer("eventpass")
            .subject(u.getId().toString())
            .issuedAt(now)
            .expiresAt(now.plusSeconds(3600))
            .claim("roles", List.of(u.getRol().name()))
            .build();
    String token =
        encoder
            .encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims))
            .getTokenValue();
    return new Token(token, "Bearer", 3600, Respuesta.of(u));
  }

  public Usuario get(long id) {
    return repo.findById(id).orElseThrow(ApiException::missing);
  }

  public org.springframework.data.domain.Page<Respuesta> listar(
      org.springframework.data.domain.Pageable p) {
    com.preventa.usuarios.config.PaginationValidator.validate(p);
    return repo.findAll(p).map(Respuesta::of);
  }

  public Respuesta actualizar(long id, Actualizacion r) {
    Usuario u = get(id);
    u.setNombre(r.nombre().trim());
    u.setEmail(r.email().trim().toLowerCase(Locale.ROOT));
    return Respuesta.of(repo.saveAndFlush(u));
  }

  public Respuesta rol(long id, Usuario.Rol rol) {
    Usuario u = get(id);
    u.setRol(rol);
    return Respuesta.of(u);
  }

  public void eliminar(long id) {
    Usuario u = get(id);
    u.setActivo(false);
  }

  /** Crea o actualiza la cuenta staff configurada para el entorno local. */
  public void bootstrap(String email, String password) {
    if (email.isBlank() || password.isBlank()) return;
    if (password.length() > 72)
      throw new IllegalArgumentException("ADMIN_PASSWORD no puede superar 72 caracteres");
    String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
    Usuario admin =
        repo.findByEmail(normalizedEmail)
            .orElseGet(
                () -> {
                  Usuario nuevo = new Usuario();
                  nuevo.setNombre("Administrador");
                  nuevo.setEmail(normalizedEmail);
                  return nuevo;
                });
    if (admin.getPasswordHash() == null || !passwords.matches(password, admin.getPasswordHash())) {
      admin.setPasswordHash(passwords.encode(password));
    }
    admin.setRol(Usuario.Rol.STAFF);
    admin.setActivo(true);
    repo.saveAndFlush(admin);
  }
}
