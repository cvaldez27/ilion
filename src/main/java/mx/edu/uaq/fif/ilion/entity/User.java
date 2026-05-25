package mx.edu.uaq.fif.ilion.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String lastName;
    private String email;
    private String password;
    
    @Enumerated(EnumType.STRING)
    private Role role;
    
    // Campos específicos para DOCTOR
    private String phone;
    private String specialty;
    private String licenseNumber;
    private String profilePhotoPath;
    
    // Campos específicos para PATIENT
    private String dateOfBirth;
    private String bloodType;
    private String allergies;
    private String chronicConditions;
    private String familyHistory;
    private String cancerHistory;
    private String geneticDisorders;
    private String socialHistorySmoking;
    private String socialHistoryAlcohol;
    private String socialHistoryDrugs;
    private String socialHistoryPhysicalActivity;
    private String socialHistoryOccupation;
    private String socialHistoryLivingSituation;
    
    // Relaciones
    @ManyToOne
    @JoinColumn(name = "assigned_doctor_id")
    private User assignedDoctor;
    
    @OneToMany(mappedBy = "assignedDoctor", cascade = CascadeType.ALL)
    private List<User> patients;
    
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL)
    private List<Appointment> appointmentsAsDoctor;
    
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    private List<Appointment> appointmentsAsPatient;
    
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL)
    private List<Prescription> prescriptions;
    
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL)
    private List<ProgressNote> progressNotes;
    
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL)
    private List<MedicalFile> medicalFiles;
    
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    private List<MedicalFile> patientFiles;
    
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL)
    private List<Schedule> schedules;
    
    public enum Role {
        DOCTOR, PATIENT
    }
}
