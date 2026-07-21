package com.thesky;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private CustomerRepository customerRepository;

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Integer productId,
                            @RequestParam(defaultValue = "1") Integer quantity,
                            Authentication authentication) {

        String email = authentication.getName();
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        cartService.addToCart(customer.getCustomerId(), productId, quantity);

        return "redirect:/cart";
    }

    @GetMapping("/cart")
    public String viewCart(Authentication authentication, Model model) {
        String email = authentication.getName();
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Cart cart = cartService.getOrCreateCart(customer.getCustomerId());
        model.addAttribute("cart", cart);

        return "cart";
    }

    @GetMapping("/cart/remove/{id}")
    public String removeFromCart(@PathVariable Integer id) {
        cartService.removeFromCart(id);
        return "redirect:/cart";
    }

    @PostMapping("/cart/update")
    public String updateCartItem(@RequestParam Integer cartItemId, @RequestParam Integer quantity) {
        cartService.updateQuantity(cartItemId, quantity);
        return "redirect:/cart";
    }
}