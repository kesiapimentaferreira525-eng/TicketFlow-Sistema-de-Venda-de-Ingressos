package TicketFlow_Sistema_de_Venda_de_Ingressos.api.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PurchaseRequestDTO(
        @NotNull(message = "O evento é obrigatório")
        Long eventId,

        @NotBlank(message = "O nome completo é obrigatório")
        @Size(max = 120, message = "O nome deve ter no máximo 120 caracteres")
        String customerName,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "Informe um e-mail válido")
        @Size(max = 180, message = "O e-mail deve ter no máximo 180 caracteres")
        String email,

        @NotBlank(message = "O CPF é obrigatório")
        @Pattern(
                regexp = "(?:\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2})",
                message = "Informe um CPF válido"
        )
        String cpf,

        @NotBlank(message = "O telefone é obrigatório")
        @Pattern(
                regexp = "\\+?[0-9() .-]{8,30}",
                message = "Informe um telefone válido"
        )
        String phone,

        @Min(value = 1, message = "A quantidade mínima é 1 ingresso")
        @Max(value = 10, message = "A quantidade máxima por compra é 10 ingressos")
        int quantity,

        @NotBlank(message = "A forma de pagamento é obrigatória")
        @Pattern(
                regexp = "(?i)pix|credit-card|boleto",
                message = "Escolha Pix, cartão de crédito ou boleto"
        )
        String paymentMethod
) {}
