package TicketFlow_Sistema_de_Venda_de_Ingressos.api;

import TicketFlow_Sistema_de_Venda_de_Ingressos.api.Model.Event;
import TicketFlow_Sistema_de_Venda_de_Ingressos.api.Model.Purchase;
import TicketFlow_Sistema_de_Venda_de_Ingressos.api.Payment.PaymentCheckout;
import TicketFlow_Sistema_de_Venda_de_Ingressos.api.Payment.PaymentGateway;
import TicketFlow_Sistema_de_Venda_de_Ingressos.api.Payment.ProviderPayment;
import TicketFlow_Sistema_de_Venda_de_Ingressos.api.Repository.EventRepository;
import TicketFlow_Sistema_de_Venda_de_Ingressos.api.Repository.PurchaseRepository;
import TicketFlow_Sistema_de_Venda_de_Ingressos.api.DTO.PurchaseRequestDTO;
import TicketFlow_Sistema_de_Venda_de_Ingressos.api.DTO.PurchaseResponseDTO;
import TicketFlow_Sistema_de_Venda_de_Ingressos.api.Exception.ResourceNotFoundException;
import TicketFlow_Sistema_de_Venda_de_Ingressos.api.Service.PurchaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:purchases_test;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Import(PurchaseControllerTests.FakePaymentConfiguration.class)
class PurchaseControllerTests {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private PurchaseService purchaseService;

    private Event event;

    @BeforeEach
    void setUp() {
        purchaseRepository.deleteAll();
        eventRepository.deleteAll();
        event = eventRepository.save(new Event(
                "Show TicketFlow",
                "Evento de teste",
                LocalDateTime.now().plusDays(7),
                "São Paulo",
                null,
                "Música",
                new BigDecimal("49.90")
        ));
    }

    @Test
    void createsPendingPurchaseUsingEventPrice() {
        PurchaseResponseDTO response = purchaseService.create(new PurchaseRequestDTO(
                event.getId(),
                "Pessoa Compradora",
                "comprador@example.com",
                "529.982.247-25",
                "(11) 99999-9999",
                2,
                "credit-card"
        ));

        assertThat(response.eventId()).isEqualTo(event.getId());
        assertThat(response.quantity()).isEqualTo(2);
        assertThat(response.unitPrice()).isEqualByComparingTo("49.90");
        assertThat(response.totalPrice()).isEqualByComparingTo("99.80");
        assertThat(response.paymentMethod()).isEqualTo("credit-card");
        assertThat(response.status()).isEqualTo("pending");
        assertThat(response.checkoutUrl()).isEqualTo("https://sandbox.mercadopago.com/checkout/demo");
        assertThat(purchaseRepository.count()).isEqualTo(1);

        purchaseService.updatePayment("payment-1", response.id().toString(), "approved", new BigDecimal("99.80"), "BRL");
        assertThat(purchaseService.getStatus(response.id()).status()).isEqualTo("approved");
    }

    @Test
    void rejectsInvalidCpf() {
        assertThatThrownBy(() -> purchaseService.create(new PurchaseRequestDTO(
                event.getId(),
                "Pessoa Compradora",
                "comprador@example.com",
                "111.111.111-11",
                "(11) 99999-9999",
                1,
                "pix"
        ))).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Informe um CPF válido.");

        assertThat(purchaseRepository.count()).isZero();
    }

    @Test
    void rejectsUnknownEvent() {
        assertThatThrownBy(() -> purchaseService.create(new PurchaseRequestDTO(
                Long.MAX_VALUE,
                "Pessoa Compradora",
                "comprador@example.com",
                "529.982.247-25",
                "(11) 99999-9999",
                1,
                "pix"
        ))).isInstanceOf(ResourceNotFoundException.class);
    }

    @TestConfiguration
    static class FakePaymentConfiguration {
        @Bean
        @Primary
        PaymentGateway fakePaymentGateway() {
            return new PaymentGateway() {
                @Override
                public PaymentCheckout createCheckout(Purchase purchase) {
                    return new PaymentCheckout("preference-test", "https://sandbox.mercadopago.com/checkout/demo");
                }

                @Override
                public ProviderPayment getPayment(String paymentId) {
                    return new ProviderPayment(paymentId, null, "approved", BigDecimal.ZERO, "BRL");
                }
            };
        }
    }
}
