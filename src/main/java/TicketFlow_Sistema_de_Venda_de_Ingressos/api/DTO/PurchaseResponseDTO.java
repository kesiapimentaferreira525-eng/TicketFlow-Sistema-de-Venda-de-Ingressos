package TicketFlow_Sistema_de_Venda_de_Ingressos.api.DTO;

import TicketFlow_Sistema_de_Venda_de_Ingressos.api.Model.Purchase;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PurchaseResponseDTO(
        Long id,
        Long eventId,
        String eventTitle,
        String customerName,
        String email,
        String phone,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal totalPrice,
        String paymentMethod,
        String status,
        String checkoutUrl,
        LocalDateTime createdAt
) {
    public PurchaseResponseDTO(Purchase purchase) {
        this(
                purchase.getId(),
                purchase.getEvent().getId(),
                purchase.getEventTitle(),
                purchase.getCustomerName(),
                purchase.getEmail(),
                purchase.getPhone(),
                purchase.getQuantity(),
                purchase.getUnitPrice(),
                purchase.getTotalPrice(),
                purchase.getPaymentMethod().getApiValue(),
                purchase.getStatus().getApiValue(),
                purchase.getCheckoutUrl(),
                purchase.getCreatedAt()
        );
    }
}
