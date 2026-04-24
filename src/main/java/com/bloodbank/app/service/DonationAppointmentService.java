package com.bloodbank.app.service;

import com.bloodbank.app.model.*;
import com.bloodbank.app.repository.DonationAppointmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DonationAppointmentService {
    private final DonationAppointmentRepository repo;
    private final DonorService donorService;
    private final BloodStockService stockService;

    public DonationAppointmentService(DonationAppointmentRepository repo, DonorService donorService, BloodStockService stockService) {
        this.repo = repo;
        this.donorService = donorService;
        this.stockService = stockService;
    }

    public List<DonationAppointment> all() { return repo.findAll(); }
    public DonationAppointment save(DonationAppointment a) { return repo.save(a); }

    public boolean canScheduleFor(Donor donor, LocalDateTime dateTime) {
        if (!donorService.canDonate(donor)) return false;
        // ensure scheduled not in the past
        return dateTime != null && dateTime.isAfter(LocalDateTime.now());
    }

    public void complete(Long id) {
        repo.findById(id).ifPresent(a -> {
            if (a.getStatus() == AppointmentStatus.SCHEDULED) {
                a.setStatus(AppointmentStatus.COMPLETED);
                // update donor eligibility and stock
                Donor d = a.getDonor();
                d.setLastDonationDate(LocalDate.now());
                donorService.save(d);
                stockService.addUnits(d.getBloodGroup(), Math.max(1, a.getUnits()));
                repo.save(a);
            }
        });
    }

    public void cancel(Long id) {
        repo.findById(id).ifPresent(a -> {
            if (a.getStatus() == AppointmentStatus.SCHEDULED) {
                a.setStatus(AppointmentStatus.CANCELLED);
                repo.save(a);
            }
        });
    }
    
    public List<DonationAppointment> getUpcomingAppointments(int limit) {
        return repo.findAll().stream()
                .filter(a -> a.getStatus() == AppointmentStatus.SCHEDULED)
                .filter(a -> a.getScheduledAt().isAfter(LocalDateTime.now()))
                .sorted((a1, a2) -> a1.getScheduledAt().compareTo(a2.getScheduledAt()))
                .limit(limit)
                .toList();
    }
}
