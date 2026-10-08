package TicketFlow_Sistema_de_Venda_de_Ingressos.api.DTO;



import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventRequestDTO(
        @NotBlank(message = "O título não pode estar em branco")
        String titulo,

        @NotBlank(message = "A descrição não pode estar em branco")
        String descricao,

        @NotNull(message = "A data e hora são obrigatórias")
        @Future(message = "A data e hora do evento devem ser futuras")
        LocalDateTime dataHora,

        @NotBlank(message = "O local não pode estar em branco")
        String local,

        String imagemUrl,

        String categoria,

        @DecimalMin(value = "0.00", message = "O preço do ingresso não pode ser negativo")
        @Digits(integer = 8, fraction = 2, message = "O preço deve ter no máximo 8 dígitos inteiros e 2 decimais")
        BigDecimal precoIngresso
) {}