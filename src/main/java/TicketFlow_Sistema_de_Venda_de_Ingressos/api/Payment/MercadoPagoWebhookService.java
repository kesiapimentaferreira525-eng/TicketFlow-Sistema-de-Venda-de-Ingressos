package TicketFlow_Sistema_de_Venda_de_Ingressos.api.Payment;

import TicketFlow_Sistema_de_Venda_de_Ingressos.api.Exception.InvalidPaymentSignatureException;
import TicketFlow_Sistema_de_Venda_de_Ingressos.api.Exception.PaymentProviderUnavailableException;
import TicketFlow_Sistema_de_Venda_de_Ingressos.api.Service.PurchaseService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Locale;

@Service
public class MercadoPagoWebhookService {

    private final String webhookSecret;
    private final PaymentGateway paymentGateway;
    private final PurchaseService purchaseService;

    public MercadoPagoWebhookService(
            @Value("${payment.mercadopago.webhook-secret:}") String webhookSecret,
            PaymentGateway paymentGateway,
            PurchaseService purchaseService
    ) {
        this.webhookSecret = webhookSecret;
        this.paymentGateway = paymentGateway;
        this.purchaseService = purchaseService;
    }

    public void process(String type, String paymentId, String signature, String requestId) {
        if (!"payment".equals(type)) {
            return;
        }
        if (paymentId == null || paymentId.isBlank()) {
            throw new IllegalArgumentException("Identificador do pagamento ausente.");
        }
        verifySignature(paymentId, signature, requestId);

        ProviderPayment payment = paymentGateway.getPayment(paymentId);
        if (!paymentId.equals(payment.id())) {
            throw new InvalidPaymentSignatureException();
        }
        purchaseService.updatePayment(
                payment.id(),
                payment.external_reference(),
                payment.status(),
                payment.transaction_amount(),
                payment.currency_id()
        );
    }

    private void verifySignature(String paymentId, String signatureHeader, String requestId) {
        if (webhookSecret.isBlank()) {
            throw new PaymentProviderUnavailableException(
                    "Notificações indisponíveis: configure MP_WEBHOOK_SECRET."
            );
        }
        if (signatureHeader == null || signatureHeader.isBlank()) {
            throw new InvalidPaymentSignatureException();
        }

        String timestamp = null;
        String signature = null;
        for (String part : signatureHeader.split(",")) {
            String[] keyValue = part.trim().split("=", 2);
            if (keyValue.length == 2 && "ts".equals(keyValue[0])) {
                timestamp = keyValue[1];
            } else if (keyValue.length == 2 && "v1".equals(keyValue[0])) {
                signature = keyValue[1];
            }
        }
        if (timestamp == null || signature == null) {
            throw new InvalidPaymentSignatureException();
        }

        String manifest = "id:" + paymentId.toLowerCase(Locale.ROOT) +
                ";request-id:" + (requestId == null ? "" : requestId) +
                ";ts:" + timestamp + ";";
        byte[] expected = calculateHmac(manifest);
        byte[] supplied;
        try {
            supplied = HexFormat.of().parseHex(signature);
        } catch (IllegalArgumentException exception) {
            throw new InvalidPaymentSignatureException();
        }
        if (!MessageDigest.isEqual(expected, supplied)) {
            throw new InvalidPaymentSignatureException();
        }
    }

    private byte[] calculateHmac(String manifest) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return mac.doFinal(manifest.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException | java.security.InvalidKeyException exception) {
            throw new IllegalStateException("Não foi possível validar a assinatura do webhook.", exception);
        }
    }
}
