package com.thesky;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private CartRepository cartRepository;

    @ModelAttribute("cartItemCount")
    public int cartItemCount(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return 0;
        }

        return customerRepository.findByEmail(authentication.getName())
                .map(customer -> cartService.getCartItemCount(customer.getCustomerId()))
                .orElse(0);
    }

    @ModelAttribute("cartPreviewItems")
    public java.util.List<CartItem> cartPreviewItems(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return java.util.Collections.emptyList();
        }

        return customerRepository.findByEmail(authentication.getName())
                .flatMap(customer -> cartRepository.findByCustomerId(customer.getCustomerId()))
                .map(Cart::getItems)
                .orElse(java.util.Collections.emptyList());
    }
}