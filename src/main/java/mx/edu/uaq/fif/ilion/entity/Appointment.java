package mx.edu.uaq.fif.ilion.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

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

    // Getters personalizados para Thymeleaf
    public String getHour() {
        return time != null ? time.format(DateTimeFormatter.ofPattern("HH:mm")) : "";
    }

    public String getDayOfWeek() {
        if (date != null) {
            return date.format(DateTimeFormatter.ofPattern("EEEE", java.util.Locale.forLanguageTag("es-ES")));
        }
        return "";
    }

    public String getMonth() {
        if (date != null) {
            return date.format(DateTimeFormatter.ofPattern("MMMM", java.util.Locale.forLanguageTag("es-ES")));
        }
        return "";
    }

    public String getYear() {
        return date != null ? String.valueOf(date.getYear()) : "";
    }

    public String getFullDate() {
        if (date != null) {
            return date.format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", java.util.Locale.forLanguageTag("es-ES")));
        }
        return "";
    }
}