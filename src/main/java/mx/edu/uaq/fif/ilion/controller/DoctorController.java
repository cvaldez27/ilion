package mx.edu.uaq.fif.ilion.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import mx.edu.uaq.fif.ilion.entity.Appointment;
import mx.edu.uaq.fif.ilion.entity.Prescription;
import mx.edu.uaq.fif.ilion.entity.ProgressNote;
import mx.edu.uaq.fif.ilion.entity.User;
import mx.edu.uaq.fif.ilion.repository.AppointmentRepository;
import mx.edu.uaq.fif.ilion.repository.PrescriptionRepository;
import mx.edu.uaq.fif.ilion.repository.ProgressNoteRepository;
import mx.edu.uaq.fif.ilion.repository.UserRepository;

@Controller
public class DoctorController {

    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final ProgressNoteRepository progressNoteRepository;

    public DoctorController(
            UserRepository userRepository,
            AppointmentRepository appointmentRepository,
            PrescriptionRepository prescriptionRepository,
            ProgressNoteRepository progressNoteRepository) {

        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.progressNoteRepository = progressNoteRepository;
    }

    @GetMapping("/doctor/dashboard")
    public String doctorDashboard(Model model,
                                  @AuthenticationPrincipal UserDetails userDetails) {

        User currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Appointment> appointmentsToday =
                appointmentRepository.findByDoctorAndDate(currentUser, LocalDate.now());

        List<Appointment> allAppointments =
                appointmentRepository.findByDoctor(currentUser);

        long confirmedCount = allAppointments.stream()
                .filter(a -> a.getStatus() == Appointment.AppointmentStatus.CONFIRMED)
                .count();

        long pendingCount = allAppointments.stream()
                .filter(a -> a.getStatus() == Appointment.AppointmentStatus.PENDING)
                .count();

        long canceledCount = allAppointments.stream()
                .filter(a -> a.getStatus() == Appointment.AppointmentStatus.CANCELED)
                .count();

        List<User> patients = userRepository.findByRole(User.Role.PATIENT);

        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("doctor", currentUser);
        model.addAttribute("appointmentsToday", appointmentsToday);
        model.addAttribute("confirmedCount", confirmedCount);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("canceledCount", canceledCount);
        model.addAttribute("patients", patients);

        return "doctor/dashboard";
    }

    @PostMapping("/doctor/schedule")
    public String scheduleAppointment(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long patientId,
            @RequestParam String dateStr,
            @RequestParam String timeStr,
            @RequestParam(required = false) String description) {

        User doctor = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Doctor no encontrado"));

        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setDate(LocalDate.parse(dateStr));
        appointment.setTime(LocalTime.parse(timeStr));
        appointment.setDescription(description != null ? description : "");
        appointment.setStatus(Appointment.AppointmentStatus.PENDING);

        appointmentRepository.save(appointment);

        return "redirect:/doctor/dashboard";
    }

    @GetMapping("/doctor/patient/{id}")
    @ResponseBody
    public User getPatient(@PathVariable Long id) {
        return getPatientOrThrow(id);
    }

    @PostMapping("/doctor/patient/{id}/update")
    @ResponseBody
    public User updatePatient(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String dateOfBirth,
            @RequestParam(required = false) String bloodType,
            @RequestParam(required = false) String allergies,
            @RequestParam(required = false) String chronicConditions,
            @RequestParam(required = false) String familyHistory,
            @RequestParam(required = false) String cancerHistory,
            @RequestParam(required = false) String geneticDisorders) {

        User patient = getPatientOrThrow(id);

        patient.setName(name);
        patient.setLastName(lastName);
        patient.setEmail(email);
        patient.setPhone(phone);
        patient.setDateOfBirth(dateOfBirth);
        patient.setBloodType(bloodType);
        patient.setAllergies(allergies);
        patient.setChronicConditions(chronicConditions);
        patient.setFamilyHistory(familyHistory);
        patient.setCancerHistory(cancerHistory);
        patient.setGeneticDisorders(geneticDisorders);

        return userRepository.save(patient);
    }

    @GetMapping("/doctor/patient/{id}/prescriptions")
    @ResponseBody
    public List<Prescription> getPatientPrescriptions(@PathVariable Long id) {
        User patient = getPatientOrThrow(id);
        return prescriptionRepository.findByPatientOrderByDateDesc(patient);
    }

    @PostMapping("/doctor/patient/{id}/prescription")
    @ResponseBody
    public Prescription createPrescription(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestParam String diagnosis,
            @RequestParam String medications,
            @RequestParam String dosage,
            @RequestParam String instructions,
            @RequestParam(required = false) String notes) {

        User doctor = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Doctor no encontrado"));

        User patient = getPatientOrThrow(id);

        Prescription prescription = new Prescription();
        prescription.setDoctor(doctor);
        prescription.setPatient(patient);
        prescription.setDate(LocalDate.now());
        prescription.setDiagnosis(diagnosis);
        prescription.setMedications(medications);
        prescription.setDosage(dosage);
        prescription.setInstructions(instructions);
        prescription.setNotes(notes != null ? notes : "");

        return prescriptionRepository.save(prescription);
    }

    @GetMapping("/doctor/patient/{id}/progress-notes")
    @ResponseBody
    public List<ProgressNote> getPatientProgressNotes(@PathVariable Long id) {
        User patient = getPatientOrThrow(id);
        return progressNoteRepository.findByPatientOrderByDateDesc(patient);
    }

    @PostMapping("/doctor/patient/{id}/progress-note")
    @ResponseBody
    public ProgressNote createProgressNote(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestParam String subjective,
            @RequestParam String objective,
            @RequestParam String assessment,
            @RequestParam String plan) {

        User doctor = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Doctor no encontrado"));

        User patient = getPatientOrThrow(id);

        ProgressNote note = new ProgressNote();
        note.setDoctor(doctor);
        note.setPatient(patient);
        note.setDate(LocalDate.now());
        note.setSubjective(subjective);
        note.setObjective(objective);
        note.setAssessment(assessment);
        note.setPlan(plan);

        return progressNoteRepository.save(note);
    }

    @GetMapping("/doctor/patients")
    public String doctorPatients(Model model,
                                 @AuthenticationPrincipal UserDetails userDetails) {

        User currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<User> patients = userRepository.findByRole(User.Role.PATIENT);

        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("doctor", currentUser);
        model.addAttribute("patients", patients);

        return "doctor/patients";
    }

    @GetMapping("/doctor/archives")
    public String doctorArchives(Model model,
                                 @AuthenticationPrincipal UserDetails userDetails) {

        User currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Appointment> allAppointments =
                appointmentRepository.findByDoctor(currentUser);

        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("doctor", currentUser);
        model.addAttribute("appointments", allAppointments);

        return "doctor/archives";
    }

    @GetMapping("/doctor/profile")
    public String doctorProfile(Model model,
                                @AuthenticationPrincipal UserDetails userDetails) {

        User currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("doctor", currentUser);

        return "doctor/profile";
    }

    private User getPatientOrThrow(Long id) {
        return userRepository.findById(id)
                .filter(user -> user.getRole() == User.Role.PATIENT)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
    }
}