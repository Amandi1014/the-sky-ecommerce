package com.thesky;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class CheckoutController {

    @Autowired
    private CartService cartService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @PostMapping("/checkout/create-session")
    public String createCheckoutSession(@RequestParam("cartItemIds") List<Integer> cartItemIds,
                                        @RequestParam String shippingName,
                                        @RequestParam String shippingAddress,
                                        @RequestParam String shippingPhone,
                                        Authentication authentication) throws StripeException {

        String email = authentication.getName();
        Customer customer = customerService.getCustomerByEmail(email);

        List<CartItem> selectedItems = cartService.getCartItemsByIds(cartItemIds);

        BigDecimal totalAmount = BigDecimal.ZERO;
        SessionCreateParams.Builder paramsBuilder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:8080/checkout/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl("http://localhost:8080/checkout/cancel");

        for (CartItem item : selectedItems) {
            BigDecimal lineTotal = item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalAmount = totalAmount.add(lineTotal);

            long unitAmountInCents = item.getProduct().getPrice()
                    .multiply(BigDecimal.valueOf(100))
                    .longValue();

            SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                    .setQuantity(Long.valueOf(item.getQuantity()))
                    .setPriceData(
                            SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency("lkr")
                                    .setUnitAmount(unitAmountInCents)
                                    .setProductData(
                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                    .setName(item.getProduct().getProductName())
                                                    .build()
                                    )
                                    .build()
                    )
                    .build();

            paramsBuilder.addLineItem(lineItem);
        }

        Session session = Session.create(paramsBuilder.build());

        Order order = new Order();
        order.setCustomerId(customer.getCustomerId());
        order.setTotalAmount(totalAmount);
        order.setStatus("PENDING");
        order.setStripeSessionId(session.getId());
        order.setOrderDate(java.time.LocalDateTime.now());
        order.setShippingName(shippingName);
        order.setShippingAddress(shippingAddress);
        order.setShippingPhone(shippingPhone);
        orderRepository.save(order);

        for (CartItem item : selectedItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(item.getProduct());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(item.getProduct().getPrice());
            orderItemRepository.save(orderItem);
        }

        return "redirect:" + session.getUrl();
    }

    @GetMapping("/checkout/success")
    public String checkoutSuccess(@RequestParam("session_id") String sessionId,
                                  org.springframework.ui.Model model) throws StripeException {

        Session session = Session.retrieve(sessionId);

        Order order = orderRepository.findAll().stream()
                .filter(o -> sessionId.equals(o.getStripeSessionId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Order not found for this session"));

        if ("paid".equals(session.getPaymentStatus()) && !"PAID".equals(order.getStatus())) {
            order.setStatus("PAID");
            orderRepository.save(order);

            for (OrderItem orderItem : order.getItems()) {
                cartService.removeFromCartByProduct(order.getCustomerId(), orderItem.getProduct().getProductId());
            }
        }

        model.addAttribute("order", order);
        return "checkout-success";
    }

    @GetMapping("/checkout/cancel")
    public String checkoutCancel() {
        return "checkout-cancel";
    }

    @PostMapping("/checkout/shipping-details")
    public String showShippingDetailsForm(@RequestParam("cartItemIds") java.util.List<Integer> cartItemIds,
                                          Authentication authentication,
                                          org.springframework.ui.Model model) {
        String email = authentication.getName();
        Customer customer = customerService.getCustomerByEmail(email);

        model.addAttribute("customer", customer);
        model.addAttribute("cartItemIds", cartItemIds);
        return "shipping-details";
    }
}