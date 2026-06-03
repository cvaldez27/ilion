package mx.edu.uaq.fif.ilion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import mx.edu.uaq.fif.ilion.entity.ProgressNote;
import mx.edu.uaq.fif.ilion.entity.User;

public interface ProgressNoteRepository extends JpaRepository<ProgressNote, Long> {

    List<ProgressNote> findByPatientOrderByDateDesc(User patient);

}