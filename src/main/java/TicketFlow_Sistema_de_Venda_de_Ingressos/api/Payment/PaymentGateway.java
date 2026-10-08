package TicketFlow_Sistema_de_Venda_de_Ingressos.api.Payment;

import TicketFlow_Sistema_de_Venda_de_Ingressos.api.Model.Purchase;

public interface PaymentGateway {
    PaymentCheckout createCheckout(Purchase purchase);
    ProviderPayment getPayment(String paymentId);
}
