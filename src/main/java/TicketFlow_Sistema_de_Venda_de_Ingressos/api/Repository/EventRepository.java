package TicketFlow_Sistema_de_Venda_de_Ingressos.api.Repository;


import TicketFlow_Sistema_de_Venda_de_Ingressos.api.Model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("""
            SELECT e FROM Event e
            WHERE (:status = 'todos'
                OR (:status = 'futuros' AND e.dataHora > :now)
                OR (:status = 'passados' AND e.dataHora <= :now))
              AND (:query IS NULL
                OR LOWER(e.titulo) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(e.local) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(e.descricao) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(e.categoria) LIKE LOWER(CONCAT('%', :query, '%')))
            """)
    Page<Event> findAllFiltered(
            @Param("status") String status,
            @Param("now") LocalDateTime now,
            @Param("query") String query,
            Pageable pageable);
}