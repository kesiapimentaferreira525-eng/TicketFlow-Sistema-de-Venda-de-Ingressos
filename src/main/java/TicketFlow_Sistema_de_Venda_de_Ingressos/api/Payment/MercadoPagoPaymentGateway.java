package TicketFlow_Sistema_de_Venda_de_Ingressos.api.Payment;

import TicketFlow_Sistema_de_Venda_de_Ingressos.api.Exception.PaymentProviderException;
import TicketFlow_Sistema_de_Venda_de_Ingressos.api.Exception.PaymentProviderUnavailableException;
import TicketFlow_Sistema_de_Venda_de_Ingressos.api.Model.Purchase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MercadoPagoPaymentGateway implements PaymentGateway {

    private final RestClient restClient;
    private final String accessToken;
    private final String frontendUrl;
    private final String notificationUrl;
    private final boolean sandbox;

    public MercadoPagoPaymentGateway(
            RestClient.Builder restClientBuilder,
            @Value("${payment.mercadopago.api-url:https://api.mercadopago.com}") String apiUrl,
            @Value("${payment.mercadopago.access-token:}") String accessToken,
            @Value("${payment.mercadopago.frontend-url:http://localhost:4200}") String frontendUrl,
            @Value("${payment.mercadopago.notification-url:}") String notificationUrl,
            @Value("${payment.mercadopago.sandbox:true}") boolean sandbox
    ) {
        this.restClient = restClientBuilder
                .baseUrl(apiUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .build();
        this.accessToken = accessToken;
        this.frontendUrl = frontendUrl.replaceAll("/+$", "");
        this.notificationUrl = notificationUrl;
        this.sandbox = sandbox;
    }

    @Override
    public PaymentCheckout createCheckout(Purchase purchase) {
        requireAccessToken();

        Map<String, Object> item = Map.of(
                "id", purchase.getEvent().getId().toString(),
                "title", purchase.getEventTitle(),
                "quantity", purchase.getQuantity(),
                "unit_price", purchase.getUnitPrice(),
                "currency_id", "BRL"
        );
        Map<String, Object> payer = Map.of(
                "name", purchase.getCustomerName(),
                "email", purchase.getEmail(),
                "identification", Map.of("type", "CPF", "number", purchase.getCpf())
        );
        String resultUrl = frontendUrl + "/purchase/result/" + purchase.getId();
        Map<String, Object> request = new HashMap<>();
        request.put("items", List.of(item));
        request.put("payer", payer);
        request.put("external_reference", purchase.getId().toString());
        request.put("metadata", Map.of(
                "purchase_id", purchase.getId(),
                "payment_method_preference", purchase.getPaymentMethod().getApiValue()
        ));
        request.put("back_urls", Map.of(
                "success", resultUrl + "?payment=success",
                "pending", resultUrl + "?payment=pending",
                "failure", resultUrl + "?payment=failure"
        ));
        request.put("auto_return", "approved");
        if (!notificationUrl.isBlank()) {
            request.put("notification_url", notificationUrl);
        }

        try {
            MercadoPagoPreference response = restClient.post()
                    .uri("/checkout/preferences")
                    .header("X-Idempotency-Key", "ticketflow-purchase-" + purchase.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(MercadoPagoPreference.class);

            if (response == null || response.id() == null || response.id().isBlank()) {
                throw new PaymentProviderException("O Mercado Pago não retornou a preferência de pagamento.");
            }
            String checkoutUrl = sandbox ? response.sandbox_init_point() : response.init_point();
            if (checkoutUrl == null || checkoutUrl.isBlank()) {
                throw new PaymentProviderException("O Mercado Pago não retornou a URL do checkout.");
            }
            return new PaymentCheckout(response.id(), checkoutUrl);
        } catch (RestClientResponseException exception) {
            throw new PaymentProviderException("O Mercado Pago recusou a criação do checkout.", exception);
        } catch (ResourceAccessException exception) {
            throw new PaymentProviderException("Não foi possível conectar ao Mercado Pago.", exception);
        }
    }

    @Override
    public ProviderPayment getPayment(String paymentId) {
        requireAccessToken();
        try {
            ProviderPayment payment = restClient.get()
                    .uri("/v1/payments/{paymentId}", paymentId)
                    .retrieve()
                    .body(ProviderPayment.class);
            if (payment == null || payment.id() == null || payment.status() == null) {
                throw new PaymentProviderException("O Mercado Pago retornou um pagamento incompleto.");
            }
            return payment;
        } catch (RestClientResponseException exception) {
            throw new PaymentProviderException("Não foi possível consultar o pagamento no Mercado Pago.", exception);
        } catch (ResourceAccessException exception) {
            throw new PaymentProviderException("Não foi possível conectar ao Mercado Pago.", exception);
        }
    }

    private void requireAccessToken() {
        if (accessToken.isBlank()) {
            throw new PaymentProviderUnavailableException(
                    "Checkout indisponível: configure a variável MP_ACCESS_TOKEN com uma credencial de teste do Mercado Pago."
            );
        }
    }

    private record MercadoPagoPreference(String id, String init_point, String sandbox_init_point) {}
}
