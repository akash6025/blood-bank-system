package com.bloodbank.app.controller;

import com.bloodbank.app.model.BloodGroup;
import com.bloodbank.app.model.Donor;
import com.bloodbank.app.service.DonorService;
import com.bloodbank.app.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class DonorController {

    private final DonorService donorService;

    public DonorController(DonorService donorService) {
        this.donorService = donorService;
    }

    @GetMapping("/donors")
    public String list(@RequestParam(value = "group", required = false) BloodGroup group,
                       @RequestParam(value = "city", required = false) String city,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "size", defaultValue = "10") int size,
                       Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Donor> donorPage = donorService.findAll(pageable);
        model.addAttribute("donors", donorPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", donorPage.getTotalPages());
        model.addAttribute("totalItems", donorPage.getTotalElements());
        model.addAttribute("bloodGroups", BloodGroup.values());
        model.addAttribute("selectedGroup", group);
        model.addAttribute("selectedCity", city);
        model.addAttribute("donor", new Donor());
        return "donors/modern";
    }

    @PostMapping("/donors")
    public String save(@Valid @ModelAttribute("donor") Donor donor, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("donors", donorService.findAll());
            model.addAttribute("bloodGroups", BloodGroup.values());
            return "donors/modern";
        }
        donorService.save(donor);
        return "redirect:/donors";
    }
    
    @GetMapping("/donors/{id}")
    public String getDonor(@PathVariable Long id, Model model) {
        Donor donor = donorService.findAll().stream()
            .filter(d -> d.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("Donor not found with id: " + id));
        model.addAttribute("donor", donor);
        return "donors/detail";
    }
}
