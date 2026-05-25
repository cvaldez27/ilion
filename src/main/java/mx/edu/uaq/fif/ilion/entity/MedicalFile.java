package mx.edu.uaq.fif.ilion.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
public class MedicalFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor;
    
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;
    
    private LocalDate date;
    private String fileName;
    private String filePath;
    
    @Enumerated(EnumType.STRING)
    private MedicalFileType type;
    
    @Enumerated(EnumType.STRING)
    private MedicalFileCategory category;
    
    public enum MedicalFileType {
        IMAGE, PDF, DOC, OTHER
    }
    
    public enum MedicalFileCategory {
        RADIOGRAPHS, LABORATORIES, STUDIES, ANALYSES, EXAMS
    }
}