package com.bloodbank.app.repository;

import com.bloodbank.app.model.BloodRequest;
import com.bloodbank.app.model.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BloodRequestRepository extends JpaRepository<BloodRequest, Long> {
    List<BloodRequest> findByStatus(RequestStatus status);
}
