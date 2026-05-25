package mx.edu.uaq.fif.ilion.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import mx.edu.uaq.fif.ilion.entity.Appointment;
import mx.edu.uaq.fif.ilion.entity.User;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Métodos existentes (los que ya tenías)
    List<Appointment> findByDoctorAndDate(User doctor, LocalDate date);

    // ✅ Método nuevo que necesitas para el paciente
    List<Appointment> findByPatientOrderByDateAsc(User patient);

    // Otros métodos que ya tenías
    List<Appointment> findByDoctor(User doctor);
    
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctor = :doctor AND a.status = :status")
    long countByDoctorAndStatus(@Param("doctor") User doctor, @Param("status") String status);
}