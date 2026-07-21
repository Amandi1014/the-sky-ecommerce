package com.thesky;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("customer", new Customer());
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(@ModelAttribute Customer customer) {
        customerService.registerCustomer(customer);
        return "redirect:/";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/account")
    public String showAccountPage(Model model, Authentication authentication) {
        String email = authentication.getName();
        Customer customer = customerService.getCustomerByEmail(email);
        model.addAttribute("customer", customer);
        return "account";
    }

    @GetMapping("/account/edit")
    public String showUpdateDetailsPage(Model model, Authentication authentication) {
        String email = authentication.getName();
        Customer customer = customerService.getCustomerByEmail(email);
        model.addAttribute("customer", customer);
        return "update-details";
    }

    @GetMapping("/account/change-password")
    public String showChangePasswordPage() {
        return "change-password";
    }

    @PostMapping("/account/update")
    public String updateAccount(@ModelAttribute Customer customer,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        String email = authentication.getName();
        customerService.updateCustomerDetails(email, customer);
        redirectAttributes.addFlashAttribute("successMessage", "Your details were updated successfully.");
        return "redirect:/account";
    }

    @PostMapping("/account/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("passwordError", "New passwords do not match.");
            return "redirect:/account/change-password";
        }
        String email = authentication.getName();
        boolean success = customerService.changePassword(email, currentPassword, newPassword);
        if (!success) {
            redirectAttributes.addFlashAttribute("passwordError", "Current password is incorrect.");
            return "redirect:/account/change-password";
        }
        redirectAttributes.addFlashAttribute("passwordSuccess", "Password changed successfully.");
        return "redirect:/account";
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email, RedirectAttributes redirectAttributes) {
        try {
            customerService.createPasswordResetToken(email);
            redirectAttributes.addFlashAttribute("message", "If that email exists, a reset link has been sent.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("message", "If that email exists, a reset link has been sent.");
        }
        return "redirect:/forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam String token, Model model) {
        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam String token,
                                       @RequestParam String newPassword,
                                       @RequestParam String confirmPassword,
                                       RedirectAttributes redirectAttributes) {
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match.");
            return "redirect:/reset-password?token=" + token;
        }

        boolean success = customerService.resetPassword(token, newPassword);
        if (!success) {
            redirectAttributes.addFlashAttribute("error", "This reset link is invalid or has expired.");
            return "redirect:/reset-password?token=" + token;
        }

        redirectAttributes.addFlashAttribute("successMessage", "Password reset successfully. Please log in.");
        return "redirect:/login";
    }
}