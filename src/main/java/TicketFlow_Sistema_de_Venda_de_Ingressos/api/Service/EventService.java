package TicketFlow_Sistema_de_Venda_de_Ingressos.api.Service;



import TicketFlow_Sistema_de_Venda_de_Ingressos.api.DTO.EventRequestDTO;
import TicketFlow_Sistema_de_Venda_de_Ingressos.api.DTO.EventResponseDTO;
import TicketFlow_Sistema_de_Venda_de_Ingressos.api.Exception.ResourceNotFoundException;
import TicketFlow_Sistema_de_Venda_de_Ingressos.api.Model.Event;
import TicketFlow_Sistema_de_Venda_de_Ingressos.api.Repository.EventRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Transactional
    public EventResponseDTO criarEvento(EventRequestDTO dto) {

        if (dto.dataHora().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("A data e hora do evento devem ser futuras.");
        }

        Event event = new Event(
                dto.titulo(),
                dto.descricao(),
                dto.dataHora(),
                dto.local(),
                dto.imagemUrl(),
                dto.categoria(),
                dto.precoIngresso() == null ? BigDecimal.ZERO : dto.precoIngresso()
        );

        Event savedEvent = eventRepository.save(event);
        return new EventResponseDTO(savedEvent);
    }

    @Transactional(readOnly = true)
    public Page<EventResponseDTO> listarEventos(String status, String query, Pageable pageable) {
        if (!status.equals("todos") && !status.equals("futuros") && !status.equals("passados")) {
            throw new IllegalArgumentException("Status deve ser 'todos', 'futuros' ou 'passados'.");
        }
        LocalDateTime now = LocalDateTime.now();
        String normalizedQuery = query == null || query.isBlank() ? null : query.trim();
        Page<Event> events = eventRepository.findAllFiltered(status, now, normalizedQuery, pageable);
        return events.map(EventResponseDTO::new);
    }

    @Transactional(readOnly = true)
    public EventResponseDTO buscarPorId(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento com ID " + id + " não encontrado."));
        return new EventResponseDTO(event);
    }
}