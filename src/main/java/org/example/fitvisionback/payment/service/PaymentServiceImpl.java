package org.example.fitvisionback.payment.service;


import com.mercadopago.resources.payment.Payment;
import lombok.extern.slf4j.Slf4j;
import org.example.fitvisionback.credits.service.CreditsService;
import org.example.fitvisionback.exceptions.PaymentWasProccesedException;
import org.example.fitvisionback.order.entity.Order;
import org.example.fitvisionback.order.service.OrderService;
import org.example.fitvisionback.order.utils.OrderStatusEnum;
import org.example.fitvisionback.payment.entity.PaymentHistory;
import org.example.fitvisionback.payment.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderService orderService;
    private final CreditsService creditsService;

    @Autowired
    private PaymentServiceImpl(PaymentRepository paymentRepository, OrderService orderService, CreditsService creditsService) {
        this.paymentRepository = paymentRepository;
        this.orderService = orderService;
        this.creditsService = creditsService;
    }

    @Override
    public void handlePayment(Payment payment,String externalReference) {
        String status = payment.getStatus();

        Order order = orderService.getById(externalReference);

        if (order.isProcessed()) {
            throw new PaymentWasProccesedException("El pago ya fue procesado anteriormente");
        }

        String paymentType = payment.getPaymentTypeId();      // Tipo general: credit_card, pix, etc.
        String paymentMethod = payment.getPaymentMethodId();

        // Creo registro para auditorias de pagos
        PaymentHistory paymentEntity = PaymentHistory.builder()
                .mpPaymentId(payment.getId().toString())
                .status(status)
                .amount(payment.getTransactionAmount())
                .paymentDate(payment.getDateApproved().toLocalDateTime())
                .paymentMethod(paymentMethod)
                .paymentType(paymentType)
                .order(order)
                .build();

        paymentRepository.save(paymentEntity);
        log.info("✅ Payment registrado en la base de datos con ID {}", paymentEntity.getId());

        updateOrderStatus(order, status);
    }

    private void updateOrderStatus(Order order, String status) {
        log.info("Updating order status to: {}", status);
        order.setProcessed(true);
        switch (status) {
            case "approved":
                order.setStatus(OrderStatusEnum.APPROVED);
                creditsService.addCreditsToUser(order.getUser(), order.getPlan());
                break;
            case "rejected":
                order.setStatus(OrderStatusEnum.REJECTED);
                break;
            case "pending":
                order.setStatus(OrderStatusEnum.PENDING);
                break;
        }
        orderService.save(order);
    }
}
