package TicketFlow_Sistema_de_Venda_de_Ingressos.api.Model;

public enum PurchaseStatus {
    PENDING("pending"),
    APPROVED("approved"),
    FAILED("failed"),
    REFUNDED("refunded");

    private final String apiValue;

    PurchaseStatus(String apiValue) {
        this.apiValue = apiValue;
    }

    public String getApiValue() {
        return apiValue;
    }

    public static PurchaseStatus fromProviderStatus(String status) {
        return switch (status) {
            case "approved" -> APPROVED;
            case "rejected", "cancelled" -> FAILED;
            case "refunded", "charged_back" -> REFUNDED;
            case "pending", "in_process", "authorized" -> PENDING;
            default -> throw new IllegalArgumentException("Status de pagamento não reconhecido.");
        };
    }
}
