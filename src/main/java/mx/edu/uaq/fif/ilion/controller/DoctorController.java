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
public class DoctorController {

    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;

    public DoctorController(UserRepository userRepository, AppointmentRepository appointmentRepository) {
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @GetMapping("/doctor/dashboard")
    public String doctorDashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        // Obtener el usuario actual (médico)
        User currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Cargar citas del día para el médico
        List<Appointment> appointmentsToday = appointmentRepository.findByDoctorAndDate(currentUser, java.time.LocalDate.now());
        
        // Contar citas por estado
        long confirmedCount = appointmentsToday.stream()
                .filter(a -> "CONFIRMED".equals(a.getStatus()))
                .count();
        long pendingCount = appointmentsToday.stream()
                .filter(a -> "PENDING".equals(a.getStatus()))
                .count();
        long canceledCount = appointmentsToday.stream()
                .filter(a -> "CANCELED".equals(a.getStatus()))
                .count();

        // Pasar datos al modelo
        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("doctor", currentUser);
        model.addAttribute("appointmentsToday", appointmentsToday);
        model.addAttribute("confirmedCount", confirmedCount);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("canceledCount", canceledCount);

        return "doctor/dashboard";
    }

    @GetMapping("/doctor/patients")
    public String doctorPatients(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Cargar pacientes asignados al médico
        List<User> patients = userRepository.findByAssignedDoctor(currentUser);
        
        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("doctor", currentUser);
        model.addAttribute("patients", patients);
        
        return "doctor/patients";
    }

    @GetMapping("/doctor/archives")
    public String doctorArchives(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Cargar todas las citas del médico (para historial)
        List<Appointment> allAppointments = appointmentRepository.findByDoctor(currentUser);
        
        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("doctor", currentUser);
        model.addAttribute("appointments", allAppointments);
        
        return "doctor/archives";
    }

    @GetMapping("/doctor/profile")
    public String doctorProfile(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("doctor", currentUser);
        
        return "doctor/profile";
    }
}