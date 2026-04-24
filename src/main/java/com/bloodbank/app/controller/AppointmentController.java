package com.bloodbank.app.controller;

import com.bloodbank.app.model.DonationAppointment;
import com.bloodbank.app.model.Donor;
import com.bloodbank.app.service.DonationAppointmentService;
import com.bloodbank.app.service.DonorService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/appointments")
public class AppointmentController {

    private final DonationAppointmentService appointmentService;
    private final DonorService donorService;

    public AppointmentController(DonationAppointmentService appointmentService, DonorService donorService) {
        this.appointmentService = appointmentService;
        this.donorService = donorService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("appointments", appointmentService.all());
        model.addAttribute("donors", donorService.findAll());
        model.addAttribute("appointment", new DonationAppointment());
        return "appointments/modern";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("appointment") DonationAppointment appointment,
                         BindingResult result,
                         @RequestParam("donorId") Long donorId,
                         @RequestParam("scheduledAt") String scheduledAtStr,
                         Model model) {
        List<Donor> donors = donorService.findAll();
        if (result.hasErrors()) {
            model.addAttribute("appointments", appointmentService.all());
            model.addAttribute("donors", donors);
            return "appointments/modern";
        }
        Donor donor = donors.stream().filter(d -> d.getId().equals(donorId)).findFirst().orElse(null);
        if (donor == null) {
            result.rejectValue("donor", "invalid", "Invalid donor");
        }
        LocalDateTime dt;
        try {
            dt = LocalDateTime.parse(scheduledAtStr);
        } catch (Exception e) {
            result.rejectValue("scheduledAt", "invalid", "Invalid date/time format (use YYYY-MM-DDThh:mm)");
            dt = null;
        }
        if (donor == null || dt == null || !appointmentService.canScheduleFor(donor, dt)) {
            model.addAttribute("error", "Cannot schedule: verify donor eligibility (>=90 days) and future time");
            model.addAttribute("appointments", appointmentService.all());
            model.addAttribute("donors", donors);
            return "appointments/modern";
        }
        appointment.setDonor(donor);
        appointment.setScheduledAt(dt);
        appointmentService.save(appointment);
        return "redirect:/appointments";
    }

    @PostMapping("/{id}/complete")
    public String complete(@PathVariable Long id) {
        appointmentService.complete(id);
        return "redirect:/appointments";
    }

    @PostMapping("/{id}/cancel")
    public String cancel(@PathVariable Long id) {
        appointmentService.cancel(id);
        return "redirect:/appointments";
    }
}
