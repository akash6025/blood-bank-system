package com.bloodbank.app.repository;

import com.bloodbank.app.model.BloodGroup;
import com.bloodbank.app.model.BloodStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BloodStockRepository extends JpaRepository<BloodStock, Long> {
    Optional<BloodStock> findByBloodGroup(BloodGroup bloodGroup);
}
