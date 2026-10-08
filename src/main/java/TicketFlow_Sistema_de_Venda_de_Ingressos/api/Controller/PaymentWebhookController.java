package TicketFlow_Sistema_de_Venda_de_Ingressos.api.Controller;

import TicketFlow_Sistema_de_Venda_de_Ingressos.api.Payment.MercadoPagoWebhookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments/mercadopago")
public class PaymentWebhookController {

    private final MercadoPagoWebhookService webhookService;

    public PaymentWebhookController(MercadoPagoWebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> receiveWebhook(
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "data.id", required = false) String paymentId,
            @RequestHeader(name = "x-signature", required = false) String signature,
            @RequestHeader(name = "x-request-id", required = false) String requestId
    ) {
        webhookService.process(type, paymentId, signature, requestId);
        return ResponseEntity.ok().build();
    }
}
