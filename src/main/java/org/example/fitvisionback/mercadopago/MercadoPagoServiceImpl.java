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
import org.example.fitvisionback.order.entity.Order;
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

        // 1️⃣ Crear la orden en la base de datos primero
        Order order = this.orderService.createOrder(planId, "S"); // todavía no tenemos preferenceId

        // 2️⃣ Crear el ítem de la preferencia
        PreferenceItemRequest item = PreferenceItemRequest.builder()
                .title(plan.getName())
                .description(plan.getDescription())
                .quantity(1)
                .unitPrice(BigDecimal.valueOf(plan.getPrice()))
                .currencyId("ARS")
                .build();

        // 3️⃣ Usar external_reference = orderId
        PreferenceRequest request = PreferenceRequest.builder()
                .items(Collections.singletonList(item))
                .externalReference(order.getId().toString()) // 🔑 clave para buscar la orden en el webhook
                .backUrls(
                        PreferenceBackUrlsRequest.builder()
                                .success("https://v0-image-fusion-app-git-dev-romanmeclazckes-projects.vercel.app/payment/success")
                                .failure("https://tuapp.com/pago-fallido")
                                .pending("https://tuapp.com/pago-pendiente")
                                .build())
                .autoReturn("approved")
                .build();

        PreferenceClient client = new PreferenceClient();
        Preference preference = client.create(request);

        // 4️⃣ Actualizar la orden con el preferenceId
        this.orderService.updatePreferenceId(order.getId(), preference.getId());

        return preference.getInitPoint();
    }

    @Override
    public void processWebhook(Map<String, Object> payload) {
        log.info("Received webhook payload: {}", payload);
        try {
            String action = (String) payload.get("action");
            if (action == null || !action.startsWith("payment.")) {
                log.warn("Webhook ignorado: {}", payload);
                return;
            }

            Map<String, Object> data = (Map<String, Object>) payload.get("data");
            String paymentId = String.valueOf(data.get("id"));
            log.info("Consultando pago en MP: {}", paymentId);

            PaymentClient paymentClient = new PaymentClient();
            Payment payment = paymentClient.get(Long.valueOf(paymentId));

            if (!"approved".equalsIgnoreCase(payment.getStatus())) {
                log.info("Pago no aprobado todavía: {}", payment.getStatus());
                return;
            }

            // ✅ Usar external_reference para buscar la orden
            String externalReference = payment.getExternalReference();
            log.info("External reference recibido: {}", externalReference);

            this.paymentService.handlePayment(payment, externalReference);
        } catch (MPApiException e) {
            log.error("Error de Mercado Pago: status={}, body={}",
                    e.getStatusCode(), e.getApiResponse().getContent(), e);
        } catch (Exception e) {
            log.error("Error procesando webhook de MercadoPago", e);
        }
    }

}
