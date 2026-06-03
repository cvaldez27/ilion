package mx.edu.uaq.fif.ilion.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

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

    @JsonIgnore
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
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "assigned_doctor_id")
    private User assignedDoctor;

    @JsonIgnore
    @OneToMany(mappedBy = "assignedDoctor", cascade = CascadeType.ALL)
    private List<User> patients;

    @JsonIgnore
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL)
    private List<Appointment> appointmentsAsDoctor;

    @JsonIgnore
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    private List<Appointment> appointmentsAsPatient;

    @JsonIgnore
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL)
    private List<Prescription> prescriptions;

    @JsonIgnore
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL)
    private List<ProgressNote> progressNotes;

    @JsonIgnore
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL)
    private List<MedicalFile> medicalFiles;

    @JsonIgnore
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    private List<MedicalFile> patientFiles;

    @JsonIgnore
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL)
    private List<Schedule> schedules;

    public enum Role {
        DOCTOR, PATIENT
    }

    public String getFullName() {
        if (name != null && lastName != null) {
            return name + " " + lastName;
        }
        return name != null ? name : email;
    }

    public String getName() {
        return name != null ? name : email;
    }
}