package com.bloodbank.app.controller;

import com.bloodbank.app.model.RequestStatus;
import com.bloodbank.app.service.BloodRequestService;
import com.bloodbank.app.service.BloodStockService;
import com.bloodbank.app.service.DonorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final DonorService donorService;
    private final BloodRequestService requestService;
    private final BloodStockService stockService;

    public AdminController(DonorService donorService, BloodRequestService requestService, BloodStockService stockService) {
        this.donorService = donorService;
        this.requestService = requestService;
        this.stockService = stockService;
    }

    @GetMapping({"", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("donorCount", donorService.count());
        model.addAttribute("pendingRequests", requestService.countPending());
        model.addAttribute("approvedRequests", requestService.countByStatus(RequestStatus.APPROVED));
        model.addAttribute("totalUnits", stockService.totalUnits());
        model.addAttribute("stocks", stockService.all());
        return "admin/dashboard";
    }
}
