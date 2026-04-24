package com.bloodbank.app.service;

import com.bloodbank.app.model.BloodGroup;
import com.bloodbank.app.model.BloodStock;
import com.bloodbank.app.repository.BloodStockRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BloodStockService {
    private static final Logger logger = LoggerFactory.getLogger(BloodStockService.class);
    
    private final BloodStockRepository bloodStockRepository;

    public BloodStockService(BloodStockRepository bloodStockRepository) {
        this.bloodStockRepository = bloodStockRepository;
    }

    public List<BloodStock> all() { return bloodStockRepository.findAll(); }

    public BloodStock getOrCreate(BloodGroup group) {
        return bloodStockRepository.findByBloodGroup(group)
                .orElseGet(() -> {
                    BloodStock s = new BloodStock();
                    s.setBloodGroup(group);
                    s.setUnitsAvailable(0);
                    return bloodStockRepository.save(s);
                });
    }
    
    public BloodStock findByBloodGroup(BloodGroup group) {
        return bloodStockRepository.findByBloodGroup(group).orElse(null);
    }

    public void addUnits(BloodGroup group, int units) {
        BloodStock s = getOrCreate(group);
        s.setUnitsAvailable(s.getUnitsAvailable() + units);
        bloodStockRepository.save(s);
        logger.info("Added {} units of {} to stock. New total: {}", 
                   units, group, s.getUnitsAvailable());
    }

    @Transactional
    public boolean consumeUnits(BloodGroup group, int units) {
        BloodStock s = getOrCreate(group);
        if (s.getUnitsAvailable() >= units) {
            int previousUnits = s.getUnitsAvailable();
            s.setUnitsAvailable(s.getUnitsAvailable() - units);
            bloodStockRepository.save(s);
            logger.info("Deducted {} units of {} from stock. Previous: {}, New total: {}", 
                       units, group, previousUnits, s.getUnitsAvailable());
            return true;
        }
        logger.error("Failed to deduct {} units of {} from stock. Available: {}", 
                   units, group, s.getUnitsAvailable());
        return false;
    }

    public int totalUnits() {
        return all().stream().mapToInt(BloodStock::getUnitsAvailable).sum();
    }
    
    public List<BloodStock> getLowStockAlerts() {
        return all().stream()
                .filter(stock -> stock.getUnitsAvailable() <= 5)
                .toList();
    }
}
