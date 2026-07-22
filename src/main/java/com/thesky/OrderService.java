package com.thesky;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Comparator;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public List<Order> getAllOrdersNewestFirst() {
        List<Order> orders = orderRepository.findAll();
        orders.sort(Comparator.comparing(Order::getOrderDate).reversed());
        return orders;
    }

    public void updateStatus(Integer orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(newStatus);
        orderRepository.save(order);
    }
}