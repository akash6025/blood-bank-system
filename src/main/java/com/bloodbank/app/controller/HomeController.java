package com.bloodbank.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import com.bloodbank.app.service.DonorService;
import com.bloodbank.app.service.BloodRequestService;
import com.bloodbank.app.service.BloodStockService;
import com.bloodbank.app.service.DonationAppointmentService;
import com.bloodbank.app.model.RequestStatus;

@Controller
public class HomeController {
    private final DonorService donorService;
    private final BloodRequestService requestService;
    private final BloodStockService stockService;
    private final DonationAppointmentService appointmentService;

    public HomeController(DonorService donorService, BloodRequestService requestService, BloodStockService stockService, DonationAppointmentService appointmentService) {
        this.donorService = donorService;
        this.requestService = requestService;
        this.stockService = stockService;
        this.appointmentService = appointmentService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("donorCount", donorService.count());
        model.addAttribute("pendingRequests", requestService.countPending());
        model.addAttribute("approvedRequests", requestService.countByStatus(RequestStatus.APPROVED));
        model.addAttribute("totalUnits", stockService.totalUnits());
        model.addAttribute("recentRequests", requestService.recent(5));
        model.addAttribute("stocks", stockService.all());
        
        // Add real notification data
        model.addAttribute("lowStockAlerts", stockService.getLowStockAlerts());
        model.addAttribute("recentDonors", donorService.recentDonors(3));
        model.addAttribute("upcomingAppointments", appointmentService.getUpcomingAppointments(3));
        
        return "index";
    }
}
