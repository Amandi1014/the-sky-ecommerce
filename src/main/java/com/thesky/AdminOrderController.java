package com.thesky;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CustomerService customerService;

    @GetMapping
    public String listOrders(Model model) {
        var orders = orderService.getAllOrdersNewestFirst();

        Map<Integer, Customer> customerLookup = new HashMap<>();
        for (Order order : orders) {
            if (!customerLookup.containsKey(order.getCustomerId())) {
                customerLookup.put(order.getCustomerId(), customerService.getCustomerById(order.getCustomerId()));
            }
        }

        model.addAttribute("orders", orders);
        model.addAttribute("customerLookup", customerLookup);
        return "admin-orders";
    }

    @PostMapping("/update-status")
    public String updateStatus(@RequestParam Integer orderId, @RequestParam String status) {
        orderService.updateStatus(orderId, status);
        return "redirect:/admin/orders";
    }
}