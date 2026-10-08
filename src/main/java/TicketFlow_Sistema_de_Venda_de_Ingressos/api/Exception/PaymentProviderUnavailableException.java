package TicketFlow_Sistema_de_Venda_de_Ingressos.api.Exception;

public class PaymentProviderUnavailableException extends RuntimeException {
    public PaymentProviderUnavailableException(String message) {
        super(message);
    }
}
