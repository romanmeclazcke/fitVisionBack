package org.example.fitvisionback.order.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.fitvisionback.order.entity.Order;
import org.example.fitvisionback.order.mapper.OrderMapper;
import org.example.fitvisionback.order.repository.OrderRepository;
import org.example.fitvisionback.order.utils.OrderStatusEnum;
import org.example.fitvisionback.plan.entity.Plan;
import org.example.fitvisionback.plan.repository.PlanRepository;
import org.example.fitvisionback.user.entity.User;
import org.example.fitvisionback.utils.GetUserConected;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    private OrderRepository orderRepository;
    private OrderMapper orderMapper;
    private GetUserConected getUserConected;
    private PlanRepository planRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, OrderMapper orderMapper, GetUserConected getUserConected, PlanRepository planRepository) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.getUserConected = getUserConected;
        this.planRepository = planRepository;
    }

    @Override
    public void createOrder(UUID planId, String preferenceId) {
        User user = getUserConected.getUserConected();
        Plan plan = this.planRepository.findById(planId).orElseThrow(()-> new EntityNotFoundException("Plan not found"));

        Order order= Order.builder()
                .plan(plan)
                .user(user)
                .status(OrderStatusEnum.PENDING)
                .mercadoPagoPreferenceId(preferenceId)
                .build();

        this.orderRepository.save(order);
    }

    @Override
    public Order findByMercadoPagoPreferenceId(String preferenceId) {
        return this.orderRepository.findByMercadoPagoPreferenceId((preferenceId)).orElseThrow(()-> new EntityNotFoundException("Order not found"));
    }

    @Override
    public void save(Order order) {
        this.orderRepository.save(order);
    }
}
