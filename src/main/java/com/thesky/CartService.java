package com.thesky;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    public Cart getOrCreateCart(Integer customerId) {
        return cartRepository.findByCustomerId(customerId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setCustomerId(customerId);
                    return cartRepository.save(newCart);
                });
    }

    public void addToCart(Integer customerId, Integer productId, Integer quantity) {
        Cart cart = getOrCreateCart(customerId);

        for (CartItem item : cart.getItems()) {
            if (item.getProduct().getProductId().equals(productId)) {
                item.setQuantity(item.getQuantity() + quantity);
                cartItemRepository.save(item);
                return;
            }
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CartItem newItem = new CartItem();
        newItem.setCart(cart);
        newItem.setProduct(product);
        newItem.setQuantity(quantity);
        cartItemRepository.save(newItem);
    }

    public void removeFromCart(Integer cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    public void updateQuantity(Integer cartItemId, Integer quantity) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        item.setQuantity(quantity);
        cartItemRepository.save(item);
    }

    public int getCartItemCount(Integer customerId) {
        return cartRepository.findByCustomerId(customerId)
                .map(cart -> cart.getItems().stream().mapToInt(CartItem::getQuantity).sum())
                .orElse(0);
    }
}