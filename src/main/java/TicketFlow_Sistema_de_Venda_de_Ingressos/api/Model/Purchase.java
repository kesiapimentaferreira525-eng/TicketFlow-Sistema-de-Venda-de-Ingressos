package TicketFlow_Sistema_de_Venda_de_Ingressos.api.Model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "purchases")
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(nullable = false, length = 200)
    private String eventTitle;

    @Column(nullable = false, length = 120)
    private String customerName;

    @Column(nullable = false, length = 180)
    private String email;

    @Column(nullable = false, length = 11)
    private String cpf;

    @Column(nullable = false, length = 30)
    private String phone;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false, precision = 11, scale = 2)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PurchaseStatus status;

    @Column(length = 100)
    private String providerPreferenceId;

    @Column(length = 1000)
    private String checkoutUrl;

    @Column(length = 100)
    private String providerPaymentId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected Purchase() {}

    public Purchase(
            Event event,
            String customerName,
            String email,
            String cpf,
            String phone,
            int quantity,
            BigDecimal unitPrice,
            PaymentMethod paymentMethod
    ) {
        this.event = event;
        this.eventTitle = event.getTitulo();
        this.customerName = customerName;
        this.email = email;
        this.cpf = cpf;
        this.phone = phone;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        this.paymentMethod = paymentMethod;
        this.status = PurchaseStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Event getEvent() { return event; }
    public String getEventTitle() { return eventTitle; }
    public String getCustomerName() { return customerName; }
    public String getEmail() { return email; }
    public String getCpf() { return cpf; }
    public String getPhone() { return phone; }
    public int getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public PurchaseStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getProviderPreferenceId() { return providerPreferenceId; }
    public String getCheckoutUrl() { return checkoutUrl; }
    public String getProviderPaymentId() { return providerPaymentId; }

    public void setCheckout(String preferenceId, String url) {
        this.providerPreferenceId = preferenceId;
        this.checkoutUrl = url;
    }

    public void confirmFreePurchase() {
        if (totalPrice.signum() == 0) {
            this.status = PurchaseStatus.APPROVED;
        }
    }

    public void updatePayment(String paymentId, PurchaseStatus newStatus) {
        if (status == PurchaseStatus.REFUNDED || status == PurchaseStatus.FAILED) {
            return;
        }
        if (status == PurchaseStatus.APPROVED && newStatus == PurchaseStatus.PENDING) {
            return;
        }
        this.providerPaymentId = paymentId;
        this.status = newStatus;
    }
}
