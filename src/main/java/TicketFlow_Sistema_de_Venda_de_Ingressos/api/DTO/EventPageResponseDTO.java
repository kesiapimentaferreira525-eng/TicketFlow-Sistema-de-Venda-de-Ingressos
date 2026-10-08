package TicketFlow_Sistema_de_Venda_de_Ingressos.api.DTO;

import org.springframework.data.domain.Page;

import java.util.List;

public record EventPageResponseDTO(
        List<EventResponseDTO> content,
        int number,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {
    public EventPageResponseDTO(Page<EventResponseDTO> page) {
        this(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}
