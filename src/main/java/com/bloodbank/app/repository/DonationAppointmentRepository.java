package com.bloodbank.app.repository;

import com.bloodbank.app.model.DonationAppointment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DonationAppointmentRepository extends JpaRepository<DonationAppointment, Long> {
}
