package com.bloodbank.app.controller;

import com.bloodbank.app.model.BloodGroup;
import com.bloodbank.app.model.BloodRequest;
import com.bloodbank.app.model.RequestStatus;
import com.bloodbank.app.service.BloodRequestService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/requests")
public class BloodRequestController {

    private final BloodRequestService service;

    public BloodRequestController(BloodRequestService service) {
        this.service = service;
    }

    @GetMapping
    public String list(@RequestParam(value = "group", required = false) BloodGroup group,
                       @RequestParam(value = "status", required = false) RequestStatus status,
                       Model model) {
        model.addAttribute("requests", service.filter(group, status));
        model.addAttribute("bloodGroups", BloodGroup.values());
        model.addAttribute("statuses", RequestStatus.values());
        model.addAttribute("selectedGroup", group);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("request", new BloodRequest());
        return "requests/modern";
    }

    @PostMapping
    public String submit(@Valid @ModelAttribute("request") BloodRequest request, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("requests", service.all());
            model.addAttribute("bloodGroups", BloodGroup.values());
            return "requests/modern";
        }
        service.save(request);
        return "redirect:/requests";
    }

    @PostMapping("/{id}/approve")
    public String approve(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        String approver = principal != null ? principal.getName() : null;
        boolean success = service.approve(id, approver);
        
        if (success) {
            redirectAttributes.addFlashAttribute("success", "Blood request approved successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to approve request: Insufficient blood stock available.");
        }
        
        return "redirect:/requests";
    }

    @PostMapping("/{id}/reject")
    public String reject(@PathVariable Long id) {
        service.reject(id);
        return "redirect:/requests";
    }
}
