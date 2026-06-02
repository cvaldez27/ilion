package mx.edu.uaq.fif.ilion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import mx.edu.uaq.fif.ilion.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.assignedDoctor = :doctor")
    List<User> findByAssignedDoctor(@Param("doctor") User doctor);

    // Obtiene todos los usuarios con rol PATIENT
    List<User> findByRole(User.Role role);

}