package TicketFlow_Sistema_de_Venda_de_Ingressos.api.Service;

import TicketFlow_Sistema_de_Venda_de_Ingressos.api.DTO.PurchaseRequestDTO;
import TicketFlow_Sistema_de_Venda_de_Ingressos.api.DTO.PurchaseResponseDTO;
import TicketFlow_Sistema_de_Venda_de_Ingressos.api.DTO.PurchaseStatusResponseDTO;
import TicketFlow_Sistema_de_Venda_de_Ingressos.api.Exception.ResourceNotFoundException;
import TicketFlow_Sistema_de_Venda_de_Ingressos.api.Model.Event;
import TicketFlow_Sistema_de_Venda_de_Ingressos.api.Model.PaymentMethod;
import TicketFlow_Sistema_de_Venda_de_Ingressos.api.Model.Purchase;
import TicketFlow_Sistema_de_Venda_de_Ingressos.api.Model.PurchaseStatus;
import TicketFlow_Sistema_de_Venda_de_Ingressos.api.Payment.PaymentCheckout;
import TicketFlow_Sistema_de_Venda_de_Ingressos.api.Payment.PaymentGateway;
import TicketFlow_Sistema_de_Venda_de_Ingressos.api.Repository.EventRepository;
import TicketFlow_Sistema_de_Venda_de_Ingressos.api.Repository.PurchaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Locale;

@Service
public class PurchaseService {

    private final EventRepository eventRepository;
    private final PurchaseRepository purchaseRepository;
    private final PaymentGateway paymentGateway;

    public PurchaseService(
            EventRepository eventRepository,
            PurchaseRepository purchaseRepository,
            PaymentGateway paymentGateway
    ) {
        this.eventRepository = eventRepository;
        this.purchaseRepository = purchaseRepository;
        this.paymentGateway = paymentGateway;
    }

    @Transactional
    public PurchaseResponseDTO create(PurchaseRequestDTO request) {
        Event event = eventRepository.findById(request.eventId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Evento com ID " + request.eventId() + " não encontrado."
                ));

        String normalizedCpf = request.cpf().replaceAll("\\D", "");
        if (!isValidCpf(normalizedCpf)) {
            throw new IllegalArgumentException("Informe um CPF válido.");
        }

        PaymentMethod paymentMethod = PaymentMethod.fromApiValue(
                request.paymentMethod().toLowerCase(Locale.ROOT)
        );
        BigDecimal unitPrice = event.getPrecoIngresso() == null
                ? BigDecimal.ZERO
                : event.getPrecoIngresso();

        Purchase purchase = new Purchase(
                event,
                request.customerName().trim(),
                request.email().trim().toLowerCase(Locale.ROOT),
                normalizedCpf,
                request.phone().trim(),
                request.quantity(),
                unitPrice,
                paymentMethod
        );

        Purchase savedPurchase = purchaseRepository.save(purchase);
        if (savedPurchase.getTotalPrice().signum() == 0) {
            savedPurchase.confirmFreePurchase();
        } else {
            PaymentCheckout checkout = paymentGateway.createCheckout(savedPurchase);
            savedPurchase.setCheckout(checkout.preferenceId(), checkout.checkoutUrl());
        }
        return new PurchaseResponseDTO(savedPurchase);
    }

    @Transactional(readOnly = true)
    public PurchaseStatusResponseDTO getStatus(Long purchaseId) {
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Compra com ID " + purchaseId + " não encontrada."
                ));
        return new PurchaseStatusResponseDTO(purchase);
    }

    @Transactional
    public void updatePayment(String paymentId, String externalReference, String providerStatus, BigDecimal amount, String currency) {
        long purchaseId;
        try {
            purchaseId = Long.parseLong(externalReference);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Referência da compra inválida.");
        }

        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Compra com ID " + purchaseId + " não encontrada."
                ));

        if (amount == null || amount.compareTo(purchase.getTotalPrice()) != 0 || !"BRL".equals(currency)) {
            throw new IllegalArgumentException("O valor ou a moeda do pagamento não correspondem à compra.");
        }

        purchase.updatePayment(paymentId, PurchaseStatus.fromProviderStatus(providerStatus));
    }

    private boolean isValidCpf(String cpf) {
        if (cpf.length() != 11 || cpf.chars().distinct().count() == 1) {
            return false;
        }

        for (int digitPosition = 9; digitPosition <= 10; digitPosition++) {
            int sum = 0;
            for (int index = 0; index < digitPosition; index++) {
                sum += (cpf.charAt(index) - '0') * (digitPosition + 1 - index);
            }
            int expectedDigit = (sum * 10) % 11;
            if (expectedDigit == 10) {
                expectedDigit = 0;
            }
            if (expectedDigit != cpf.charAt(digitPosition) - '0') {
                return false;
            }
        }

        return true;
    }
}
