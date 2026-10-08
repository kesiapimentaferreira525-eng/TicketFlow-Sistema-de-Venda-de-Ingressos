package TicketFlow_Sistema_de_Venda_de_Ingressos.api.Controller;

import TicketFlow_Sistema_de_Venda_de_Ingressos.api.DTO.EventPageResponseDTO;
import TicketFlow_Sistema_de_Venda_de_Ingressos.api.DTO.EventRequestDTO;
import TicketFlow_Sistema_de_Venda_de_Ingressos.api.DTO.EventResponseDTO;
import TicketFlow_Sistema_de_Venda_de_Ingressos.api.Service.EventService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/events")

public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }


    @PostMapping
    public ResponseEntity<EventResponseDTO> criarEvento(@RequestBody @Valid EventRequestDTO dto) {
        EventResponseDTO novoEvento = eventService.criarEvento(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoEvento);
    }


    @GetMapping
    public ResponseEntity<EventPageResponseDTO> listarEventos(
            @RequestParam(required = false, defaultValue = "todos") String status,
            @RequestParam(required = false) String q,
            @PageableDefault(size = 10, sort = "dataHora") Pageable pageable) {
        Page<EventResponseDTO> eventos = eventService.listarEventos(status, q, pageable);
        return ResponseEntity.ok(new EventPageResponseDTO(eventos));
    }


    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDTO> buscarPorId(@PathVariable Long id) {
        EventResponseDTO evento = eventService.buscarPorId(id);
        return ResponseEntity.ok(evento);
    }
}