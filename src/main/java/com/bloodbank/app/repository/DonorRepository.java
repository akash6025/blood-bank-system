package com.bloodbank.app.repository;

import com.bloodbank.app.model.Donor;
import com.bloodbank.app.model.BloodGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DonorRepository extends JpaRepository<Donor, Long> {
    List<Donor> findByBloodGroup(BloodGroup bloodGroup);
    List<Donor> findByCityIgnoreCase(String city);
}
