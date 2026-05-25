package mx.edu.uaq.fif.ilion.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
public class Prescription {
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
    private String diagnosis;
    private String medications;
    private String dosage;
    private String instructions;
    private String notes;
}