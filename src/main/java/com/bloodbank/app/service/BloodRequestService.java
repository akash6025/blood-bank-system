package com.bloodbank.app.service;

import com.bloodbank.app.model.BloodRequest;
import com.bloodbank.app.model.RequestStatus;
import com.bloodbank.app.repository.BloodRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BloodRequestService {
    private static final Logger logger = LoggerFactory.getLogger(BloodRequestService.class);
    
    private final BloodRequestRepository repo;
    private final BloodStockService stockService;

    public BloodRequestService(BloodRequestRepository repo, BloodStockService stockService) {
        this.repo = repo;
        this.stockService = stockService;
    }

    public List<BloodRequest> all() { return repo.findAll(); }
    
    public BloodRequest save(BloodRequest r) { 
        logger.info("Creating new blood request: {} units of {} for patient {} at {}", 
                   r.getUnitsRequested(), r.getBloodGroup(), r.getPatientName(), r.getHospitalName());
        return repo.save(r); 
    }

    public boolean approve(Long id) { return approve(id, null); }
    
    @Transactional
    public boolean approve(Long id, String approvedBy) {
        return repo.findById(id).map(req -> {
            if (req.getStatus() != RequestStatus.PENDING) {
                logger.warn("Attempted to approve non-pending request {}: status is {}", id, req.getStatus());
                return false;
            }
            
            // Check stock availability first
            com.bloodbank.app.model.BloodStock stock = stockService.findByBloodGroup(req.getBloodGroup());
            if (stock == null || stock.getUnitsAvailable() < req.getUnitsRequested()) {
                int available = stock != null ? stock.getUnitsAvailable() : 0;
                logger.error("Insufficient stock for request {}: {} available, {} requested for {}", 
                           id, available, req.getUnitsRequested(), req.getBloodGroup());
                return false;
            }
            
            logger.info("Approving blood request {}: {} units of {} by {}", 
                       id, req.getUnitsRequested(), req.getBloodGroup(), approvedBy);
            
            // Deduct units from stock
            stockService.consumeUnits(req.getBloodGroup(), req.getUnitsRequested());
            
            // Update request status
            req.setStatus(RequestStatus.APPROVED);
            if (approvedBy != null) {
                req.setApprovedBy(approvedBy);
            }
            repo.save(req);
            
            logger.info("Successfully approved request {} and deducted {} units of {} from stock", 
                       id, req.getUnitsRequested(), req.getBloodGroup());
            
            return true;
        }).orElse(false);
    }

    public void reject(Long id) {
        repo.findById(id).ifPresent(r -> { 
            r.setStatus(RequestStatus.REJECTED); 
            repo.save(r);
            logger.info("Rejected blood request {}: {} units of {} for patient {}", 
                       id, r.getUnitsRequested(), r.getBloodGroup(), r.getPatientName());
        });
    }

    public long countByStatus(RequestStatus status) {
        return repo.findByStatus(status).size();
    }

    public long countPending() { return countByStatus(RequestStatus.PENDING); }

    public List<BloodRequest> filter(com.bloodbank.app.model.BloodGroup group, RequestStatus status) {
        return all().stream()
                .filter(r -> group == null || r.getBloodGroup() == group)
                .filter(r -> status == null || r.getStatus() == status)
                .collect(Collectors.toList());
    }

    public List<BloodRequest> recent(int n) {
        return all().stream()
                .sorted((a,b) -> b.getRequestedAt().compareTo(a.getRequestedAt()))
                .limit(n)
                .collect(Collectors.toList());
    }
}
