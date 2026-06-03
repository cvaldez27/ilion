package mx.edu.uaq.fif.ilion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import mx.edu.uaq.fif.ilion.entity.Prescription;
import mx.edu.uaq.fif.ilion.entity.User;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    List<Prescription> findByPatientOrderByDateDesc(User patient);

}