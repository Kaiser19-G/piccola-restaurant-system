package com.piccola.tuproyecto.repository;

import com.piccola.tuproyecto.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByEmail(String email);
    
    Optional<Usuario> findByEmailAndActivoTrue(String email);
    
    boolean existsByEmail(String email);
    
    List<Usuario> findByRoleAndActivoTrueOrderByNombreAsc(Usuario.Role role);
    
    List<Usuario> findByActivoTrueOrderByFechaRegistroDesc();
    
    @Query("SELECT u FROM Usuario u WHERE u.activo = true AND " +
           "(LOWER(u.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(u.apellido) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :busqueda, '%')))")
    List<Usuario> buscarUsuarios(@Param("busqueda") String busqueda);
    
    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.role = :role AND u.activo = true")
    Long countByRoleAndActivoTrue(@Param("role") Usuario.Role role);
    
    @Query("SELECT u FROM Usuario u WHERE u.fechaRegistro >= :fechaInicio")
    List<Usuario> findUsuariosRegistradosDespueDe(@Param("fechaInicio") LocalDateTime fechaInicio);
      @Query("SELECT COUNT(o) FROM Orden o WHERE o.usuario.id = :usuarioId")
    Long countOrdenesByClienteId(@Param("usuarioId") Long usuarioId);
}
