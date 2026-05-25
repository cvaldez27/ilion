package mx.edu.uaq.fif.ilion.controller;

import mx.edu.uaq.fif.ilion.entity.Appointment;
import mx.edu.uaq.fif.ilion.entity.User;
import mx.edu.uaq.fif.ilion.repository.AppointmentRepository;
import mx.edu.uaq.fif.ilion.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class PatientController {

    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;

    public PatientController(UserRepository userRepository, AppointmentRepository appointmentRepository) {
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
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
}