package TicketFlow_Sistema_de_Venda_de_Ingressos.api.DTO;

import TicketFlow_Sistema_de_Venda_de_Ingressos.api.Model.Purchase;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PurchaseStatusResponseDTO(
        Long id,
        String eventTitle,
        int quantity,
        BigDecimal totalPrice,
        String paymentMethod,
        String status,
        LocalDateTime createdAt
) {
    public PurchaseStatusResponseDTO(Purchase purchase) {
        this(
                purchase.getId(),
                purchase.getEventTitle(),
                purchase.getQuantity(),
                purchase.getTotalPrice(),
                purchase.getPaymentMethod().getApiValue(),
                purchase.getStatus().getApiValue(),
                purchase.getCreatedAt()
        );
    }
}
