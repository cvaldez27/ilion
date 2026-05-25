package mx.edu.uaq.fif.ilion.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
public class ProgressNote {
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
    
    @Column(columnDefinition = "TEXT")
    private String subjective;
    
    @Column(columnDefinition = "TEXT")
    private String objective;
    
    @Column(columnDefinition = "TEXT")
    private String assessment;
    
    @Column(columnDefinition = "TEXT")
    private String plan;
}