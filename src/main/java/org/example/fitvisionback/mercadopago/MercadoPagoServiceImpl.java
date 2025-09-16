package org.example.fitvisionback.mercadopago;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.example.fitvisionback.credits.service.CreditsService;
import org.example.fitvisionback.order.service.OrderService;
import org.example.fitvisionback.payment.service.PaymentService;
import org.example.fitvisionback.plan.entity.Plan;
import org.example.fitvisionback.plan.service.PlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class MercadoPagoServiceImpl implements PaymentsService {

    private PlanService planService;

    @Value("${mercadopago.access.token}")
    private String mpAccessToken;
    private OrderService orderService;
    private CreditsService creditsService;
    private PaymentService paymentService;


    @PostConstruct
    public void init() {
       MercadoPagoConfig.setAccessToken(this.mpAccessToken);
    }

    @Autowired
    public MercadoPagoServiceImpl(PlanService planService,OrderService orderService,CreditsService creditsService, PaymentService paymentService) {
        this.planService = planService;
        this.orderService = orderService;
        this.creditsService = creditsService;
        this.paymentService = paymentService;
    }

    @Override
    public String createPreference(UUID planId) throws MPException, MPApiException {
        Plan plan = this.planService.getPlanById(planId);

        PreferenceItemRequest item = PreferenceItemRequest.builder()
                .title(plan.getName())
                .description(plan.getDescription())
                .quantity(1)
                .unitPrice(BigDecimal.valueOf(plan.getPrice()))
                .currencyId("ARS")
                .build();

        PreferenceRequest request = PreferenceRequest.builder()
                .items(Collections.singletonList(item))
                .backUrls(
                        PreferenceBackUrlsRequest.builder()
                                .success("https://tuapp.com/pago-exitoso")
                                .failure("https://tuapp.com/pago-fallido")
                                .pending("https://tuapp.com/pago-pendiente")
                                .build())
                .autoReturn("approved")
                .build();

        PreferenceClient client = new PreferenceClient();
        Preference preference = client.create(request);

        this.orderService.createOrder(planId,preference.getId());
        return preference.getInitPoint();

    }

    @Override
    public void processWebhook(Map<String, Object> payload) {
        log.info("Received webhook payload: {}", payload);
        try {
            String type = (String) payload.get("type");
            if (!"payment".equals(type)) return;

            Map<String, Object> data = (Map<String, Object>) payload.get("data");
            String paymentId = String.valueOf(data.get("id"));

            PaymentClient paymentClient = new PaymentClient();
            Payment payment = paymentClient.get(Long.valueOf(paymentId));

            this.paymentService.handlePayment(payment);
        } catch (Exception e) {
            log.error("Error procesando webhook de MercadoPago", e);
        }
    }



}
