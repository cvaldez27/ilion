package mx.edu.uaq.fif.ilion.controller;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import mx.edu.uaq.fif.ilion.entity.Appointment;
import mx.edu.uaq.fif.ilion.entity.MedicalFile;
import mx.edu.uaq.fif.ilion.entity.User;
import mx.edu.uaq.fif.ilion.repository.AppointmentRepository;
import mx.edu.uaq.fif.ilion.repository.MedicalFileRepository;
import mx.edu.uaq.fif.ilion.repository.UserRepository;

@Controller
public class PatientController {

    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final MedicalFileRepository medicalFileRepository;

    public PatientController(
            UserRepository userRepository,
            AppointmentRepository appointmentRepository,
            MedicalFileRepository medicalFileRepository) {

        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
        this.medicalFileRepository = medicalFileRepository;
    }

    @GetMapping("/patient/dashboard")
    public String patientDashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Obtener citas próximas del paciente
        List<Appointment> appointments = appointmentRepository.findByPatientOrderByDateAsc(currentUser);

        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("appointments", appointments);
        model.addAttribute("patient", currentUser);

        return "patient/dashboard";
    }

    @GetMapping("/patient/appointments")
    public String patientAppointments(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        return patientDashboard(model, userDetails);
    }

    @GetMapping("/patient/medications")
    public String patientMedications(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("username", userDetails.getUsername());
        return "patient/medications"; // A crear después
    }

    @GetMapping("/patient/profile")
    public String patientProfile(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("patient", currentUser);
        return "patient/profile"; // A crear después
    }

    @PostMapping("/patient/appointment/{id}/confirm")
    public String confirmAppointment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        User currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (!appointment.getPatient().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Unauthorized action");
        }

        appointment.setStatus(Appointment.AppointmentStatus.CONFIRMED);
        appointmentRepository.save(appointment);

        return "redirect:/patient/dashboard";
    }

    @PostMapping("/patient/appointment/{id}/cancel")
    public String cancelAppointment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        User currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (!appointment.getPatient().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Unauthorized action");
        }

        appointment.setStatus(Appointment.AppointmentStatus.CANCELED);
        appointmentRepository.save(appointment);

        return "redirect:/patient/dashboard";
    }

    @GetMapping("/patient/files")
    @ResponseBody
    public List<MedicalFile> getPatientFiles(
            @AuthenticationPrincipal UserDetails userDetails) {

        User currentUser = userRepository.findByEmail(
                userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return medicalFileRepository.findByPatientOrderByDateDesc(currentUser);
    }

    @GetMapping("/patient/file/{id}")
    public ResponseEntity<Resource> viewPatientFile(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) throws IOException {

        User currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        MedicalFile file = medicalFileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found"));

        if (!file.getPatient().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        Path path = Paths.get(file.getFilePath());
        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists()) {
            throw new RuntimeException("File does not exist on server");
        }

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + file.getFileName() + "\""
                )
                .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                .body(resource);
    }
}