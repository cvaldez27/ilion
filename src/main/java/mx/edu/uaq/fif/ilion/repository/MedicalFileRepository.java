package mx.edu.uaq.fif.ilion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import mx.edu.uaq.fif.ilion.entity.MedicalFile;
import mx.edu.uaq.fif.ilion.entity.User;

public interface MedicalFileRepository extends JpaRepository<MedicalFile, Long> {

    List<MedicalFile> findByPatientOrderByDateDesc(User patient);

    List<MedicalFile> findByPatientAndCategoryOrderByDateDesc(
            User patient,
            MedicalFile.MedicalFileCategory category
    );
}