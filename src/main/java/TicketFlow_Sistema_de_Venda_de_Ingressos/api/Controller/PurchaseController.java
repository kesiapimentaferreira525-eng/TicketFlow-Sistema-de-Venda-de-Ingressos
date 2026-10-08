package TicketFlow_Sistema_de_Venda_de_Ingressos.api.Controller;

import TicketFlow_Sistema_de_Venda_de_Ingressos.api.DTO.PurchaseRequestDTO;
import TicketFlow_Sistema_de_Venda_de_Ingressos.api.DTO.PurchaseResponseDTO;
import TicketFlow_Sistema_de_Venda_de_Ingressos.api.DTO.PurchaseStatusResponseDTO;
import TicketFlow_Sistema_de_Venda_de_Ingressos.api.Service.PurchaseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/purchases")
public class PurchaseController {

    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @PostMapping
    public ResponseEntity<PurchaseResponseDTO> create(@RequestBody @Valid PurchaseRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(purchaseService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseStatusResponseDTO> status(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseService.getStatus(id));
    }
}
