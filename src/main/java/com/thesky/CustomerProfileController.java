package com.thesky;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CustomerProfileController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/profile")
    public String showProfile(Authentication authentication, Model model) {
        String email = authentication.getName();
        Customer customer = customerService.getCustomerByEmail(email);
        model.addAttribute("customer", customer);
        return "profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(Authentication authentication, @ModelAttribute Customer customer) {
        String email = authentication.getName();
        Customer existing = customerService.getCustomerByEmail(email);
        customer.setCustomerId(existing.getCustomerId());
        customer.setEmail(existing.getEmail());
        customer.setPassword(existing.getPassword());
        customerService.updateCustomer(customer);
        return "redirect:/profile";
    }

    @PostMapping("/profile/delete")
    public String deleteProfile(Authentication authentication) {
        String email = authentication.getName();
        Customer customer = customerService.getCustomerByEmail(email);
        customerService.deleteCustomer(customer.getCustomerId());
        SecurityContextHolder.clearContext();
        return "redirect:/";
    }
}