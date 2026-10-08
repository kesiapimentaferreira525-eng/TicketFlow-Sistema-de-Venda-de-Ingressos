package TicketFlow_Sistema_de_Venda_de_Ingressos.api.Exception;

public class InvalidPaymentSignatureException extends RuntimeException {
    public InvalidPaymentSignatureException() {
        super("Assinatura da notificação de pagamento inválida.");
    }
}
