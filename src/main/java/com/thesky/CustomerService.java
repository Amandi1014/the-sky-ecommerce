package com.thesky;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    public Customer registerCustomer(Customer customer) {
        String hashedPassword = passwordEncoder.encode(customer.getPassword());
        customer.setPassword(hashedPassword);
        return customerRepository.save(customer);
    }

    public java.util.List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    public Customer updateCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public void deleteCustomer(Integer customerId) {
        customerRepository.deleteById(customerId);
    }

    public Customer updateCustomerDetails(String email, Customer updatedDetails) {
        Customer customer = getCustomerByEmail(email);
        customer.setFullName(updatedDetails.getFullName());
        customer.setPhone(updatedDetails.getPhone());
        customer.setAddress(updatedDetails.getAddress());
        return customerRepository.save(customer);
    }

    public boolean changePassword(String email, String currentPassword, String newPassword) {
        Customer customer = getCustomerByEmail(email);
        if (!passwordEncoder.matches(currentPassword, customer.getPassword())) {
            return false;
        }
        customer.setPassword(passwordEncoder.encode(newPassword));
        customerRepository.save(customer);
        return true;
    }

    public void createPasswordResetToken(String email) {
        Customer customer = getCustomerByEmail(email);
        String token = UUID.randomUUID().toString();
        customer.setResetToken(token);
        customer.setResetTokenExpiry(LocalDateTime.now().plusMinutes(30));
        customerRepository.save(customer);

        String resetLink = "http://localhost:8080/reset-password?token=" + token;
        emailService.sendPasswordResetEmail(customer.getEmail(), resetLink);
    }

    public boolean resetPassword(String token, String newPassword) {
        Customer customer = customerRepository.findByResetToken(token).orElse(null);

        if (customer == null) {
            return false;
        }
        if (customer.getResetTokenExpiry() == null || customer.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            return false;
        }

        customer.setPassword(passwordEncoder.encode(newPassword));
        customer.setResetToken(null);
        customer.setResetTokenExpiry(null);
        customerRepository.save(customer);
        return true;
    }
}