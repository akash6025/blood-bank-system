package com.bloodbank.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Entity
public class Donor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @Email(message = "Please provide a valid email address")
    @NotBlank
    private String email;

    @NotBlank
    private String phone;

    @NotNull(message = "Age is required")
    private Integer age;

    @NotBlank
    private String gender; // Male/Female/Other

    @NotBlank
    @Column(length = 500)
    private String address;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Blood group is required")
    private BloodGroup bloodGroup;

    private LocalDate lastDonationDate;

    @NotBlank
    private String city;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public BloodGroup getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(BloodGroup bloodGroup) { this.bloodGroup = bloodGroup; }
    public LocalDate getLastDonationDate() { return lastDonationDate; }
    public void setLastDonationDate(LocalDate lastDonationDate) { this.lastDonationDate = lastDonationDate; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
}
