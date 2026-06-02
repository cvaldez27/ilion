package mx.edu.uaq.fif.ilion.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import mx.edu.uaq.fif.ilion.entity.Appointment;
import mx.edu.uaq.fif.ilion.entity.User;
import mx.edu.uaq.fif.ilion.repository.AppointmentRepository;
import mx.edu.uaq.fif.ilion.repository.UserRepository;

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

        // Citas de hoy (tabla principal)
List<Appointment> appointmentsToday =
        appointmentRepository.findByDoctorAndDate(
                currentUser,
                LocalDate.now()
        );

// Todas las citas (Medical Log)
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

        // Cargar pacientes asignados al médico para el panel de scheduling
        List<User> patients = userRepository.findByRole(User.Role.PATIENT);

        // Pasar datos al modelo - ⚠️ SIN ESPACIOS
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
        @RequestParam(required = false) String description,
        Model model) {

        User doctor = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Doctor no encontrado"));

        User patient = userRepository.findById(patientId)
        .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        // Parsear fecha y hora
        LocalDate date = LocalDate.parse(dateStr);
        LocalTime time = LocalTime.parse(timeStr);

        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setDate(date);
        appointment.setTime(time);
        appointment.setDescription(description != null ? description : "");
        appointment.setStatus(Appointment.AppointmentStatus.PENDING);

        appointmentRepository.save(appointment);

        return "redirect:/doctor/dashboard";
    }


    @GetMapping("/doctor/patients")
    public String doctorPatients(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Cargar pacientes asignados al médico
        List<User> patients = userRepository.findByAssignedDoctor(currentUser);

        // ⚠️ SIN ESPACIOS
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

        // ⚠️ SIN ESPACIOS
        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("doctor", currentUser);
        model.addAttribute("appointments", allAppointments);

        return "doctor/archives";
    }

    @GetMapping("/doctor/profile")
    public String doctorProfile(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // ⚠️ SIN ESPACIOS
        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("doctor", currentUser);

        return "doctor/profile";
    }
}