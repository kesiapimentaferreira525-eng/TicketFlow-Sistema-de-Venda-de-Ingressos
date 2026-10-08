package TicketFlow_Sistema_de_Venda_de_Ingressos.api.DTO;

import TicketFlow_Sistema_de_Venda_de_Ingressos.api.Model.Event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventResponseDTO(
        Long id,
        String titulo,
        String descricao,
        LocalDateTime dataHora,
        String local,
        String imagemUrl,
        String categoria,
        BigDecimal precoIngresso
) {
    public EventResponseDTO(Event event) {
        this(
                event.getId(),
                event.getTitulo(),
                event.getDescricao(),
                event.getDataHora(),
                event.getLocal(),
                event.getImagemUrl(),
                event.getCategoria(),
                event.getPrecoIngresso()
        );
    }
}