package TicketFlow_Sistema_de_Venda_de_Ingressos.api.Payment;

import java.math.BigDecimal;

public record ProviderPayment(
        String id,
        String external_reference,
        String status,
        BigDecimal transaction_amount,
        String currency_id
) {}
