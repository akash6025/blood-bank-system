package com.bloodbank.app.service;

import com.bloodbank.app.model.Donor;
import com.bloodbank.app.model.BloodGroup;
import com.bloodbank.app.repository.DonorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class DonorService {
    private static final Logger logger = LoggerFactory.getLogger(DonorService.class);
    
    private final DonorRepository donorRepository;

    public DonorService(DonorRepository donorRepository) {
        this.donorRepository = donorRepository;
    }

    public List<Donor> findAll() { return donorRepository.findAll(); }
    
    public Page<Donor> findAll(Pageable pageable) { 
        return donorRepository.findAll(pageable); 
    }
    
    public Donor save(Donor d) { 
        logger.info("Registering new donor: {} ({}, {} years old, {} blood group)", 
                   d.getName(), d.getBloodGroup(), d.getAge(), d.getCity());
        Donor savedDonor = donorRepository.save(d);
        logger.info("Successfully registered donor: {} with ID: {}", d.getName(), savedDonor.getId());
        return savedDonor;
    }
    public List<Donor> findByGroup(BloodGroup group) { return donorRepository.findByBloodGroup(group); }

    public long count() { return donorRepository.count(); }

    public List<Donor> filter(BloodGroup group, String city) {
        List<Donor> base = donorRepository.findAll();
        return base.stream()
                .filter(d -> group == null || d.getBloodGroup() == group)
                .filter(d -> city == null || city.isBlank() || (d.getCity() != null && d.getCity().equalsIgnoreCase(city)))
                .collect(Collectors.toList());
    }

    public boolean canDonate(Donor d) {
        if (d.getAge() == null || d.getAge() < 18 || d.getAge() > 65) return false;
        LocalDate last = d.getLastDonationDate();
        if (last == null) return true;
        long days = ChronoUnit.DAYS.between(last, LocalDate.now());
        return days >= 90;
    }

    public LocalDate nextEligibleDate(Donor d) {
        LocalDate last = d.getLastDonationDate();
        if (last == null) return LocalDate.now();
        return last.plusDays(90);
    }
    
    public List<Donor> recentDonors(int limit) {
        return donorRepository.findAll().stream()
                .sorted((d1, d2) -> d2.getId().compareTo(d1.getId())) // Sort by ID descending as proxy for recent
                .limit(limit)
                .collect(Collectors.toList());
    }
}
