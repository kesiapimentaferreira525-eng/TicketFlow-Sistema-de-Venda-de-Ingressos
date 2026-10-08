package TicketFlow_Sistema_de_Venda_de_Ingressos.api.Repository;

import TicketFlow_Sistema_de_Venda_de_Ingressos.api.Model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {}
