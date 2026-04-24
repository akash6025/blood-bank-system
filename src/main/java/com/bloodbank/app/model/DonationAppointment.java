package com.bloodbank.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Max;

import java.time.LocalDateTime;

@Entity
public class DonationAppointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Donor donor;

    @NotNull(message = "Scheduled date and time is required")
    private LocalDateTime scheduledAt;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status = AppointmentStatus.SCHEDULED;

    @Min(value = 1, message = "At least 1 unit must be donated")
    @Max(value = 2, message = "Maximum 2 units can be donated at once")
    private int units = 1; // units to be collected when completed

    private String notes;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Donor getDonor() { return donor; }
    public void setDonor(Donor donor) { this.donor = donor; }
    public LocalDateTime getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(LocalDateTime scheduledAt) { this.scheduledAt = scheduledAt; }
    public AppointmentStatus getStatus() { return status; }
    public void setStatus(AppointmentStatus status) { this.status = status; }
    public int getUnits() { return units; }
    public void setUnits(int units) { this.units = units; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
