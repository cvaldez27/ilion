package mx.edu.uaq.fif.ilion.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
public class Appointment {
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
    private LocalTime time;
    private String description;
    
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;
    
    public enum AppointmentStatus {
        CONFIRMED, PENDING, CANCELED
    }
}