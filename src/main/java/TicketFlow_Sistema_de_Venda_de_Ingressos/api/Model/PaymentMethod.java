package TicketFlow_Sistema_de_Venda_de_Ingressos.api.Model;

public enum PaymentMethod {
    PIX("pix"),
    CREDIT_CARD("credit-card"),
    BOLETO("boleto");

    private final String apiValue;

    PaymentMethod(String apiValue) {
        this.apiValue = apiValue;
    }

    public String getApiValue() {
        return apiValue;
    }

    public static PaymentMethod fromApiValue(String value) {
        return switch (value.toLowerCase()) {
            case "pix" -> PIX;
            case "credit-card" -> CREDIT_CARD;
            case "boleto" -> BOLETO;
            default -> throw new IllegalArgumentException("Forma de pagamento inválida.");
        };
    }
}
