package com.bloodbank.app.controller;

import com.bloodbank.app.model.BloodGroup;
import com.bloodbank.app.service.BloodStockService;
import com.bloodbank.app.service.BloodRequestService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BloodStockController {

    private final BloodStockService stockService;
    private final BloodRequestService requestService;

    public BloodStockController(BloodStockService stockService, BloodRequestService requestService) {
        this.stockService = stockService;
        this.requestService = requestService;
    }

    @GetMapping("/stock")
    public String list(Model model) {
        model.addAttribute("stocks", stockService.all());
        model.addAttribute("groups", BloodGroup.values());
        model.addAttribute("pendingRequests", requestService.countPending());
        return "stock/simple";
    }

    @PostMapping("/stock/add")
    public String add(@RequestParam("group") String groupStr, 
                     @RequestParam("units") int units,
                     Model model) {
        try {
            // Validate units
            if (units < 1) {
                model.addAttribute("error", "At least 1 unit must be added");
                model.addAttribute("stocks", stockService.all());
                model.addAttribute("groups", BloodGroup.values());
                model.addAttribute("pendingRequests", requestService.countPending());
                return "stock/simple";
            }
            
            // Convert string to BloodGroup enum
            BloodGroup group;
            try {
                group = BloodGroup.valueOf(groupStr);
            } catch (IllegalArgumentException e) {
                model.addAttribute("error", "Invalid blood group selected");
                model.addAttribute("stocks", stockService.all());
                model.addAttribute("groups", BloodGroup.values());
                model.addAttribute("pendingRequests", requestService.countPending());
                return "stock/simple";
            }
            
            stockService.addUnits(group, units);
            model.addAttribute("success", "Successfully added " + units + " units of " + group + " blood to stock");
            model.addAttribute("stocks", stockService.all());
            model.addAttribute("groups", BloodGroup.values());
            model.addAttribute("pendingRequests", requestService.countPending());
            return "stock/simple";
            
        } catch (Exception e) {
            model.addAttribute("error", "Failed to add blood units: " + e.getMessage());
            model.addAttribute("stocks", stockService.all());
            model.addAttribute("groups", BloodGroup.values());
            model.addAttribute("pendingRequests", requestService.countPending());
            return "stock/simple";
        }
    }
}
