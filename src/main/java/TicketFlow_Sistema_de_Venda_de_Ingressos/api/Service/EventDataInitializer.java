package TicketFlow_Sistema_de_Venda_de_Ingressos.api.Service;

import TicketFlow_Sistema_de_Venda_de_Ingressos.api.Model.Event;
import TicketFlow_Sistema_de_Venda_de_Ingressos.api.Repository.EventRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
public class EventDataInitializer implements ApplicationRunner {

    private static final Map<String, String> DEMO_CATEGORIES = Map.of(
            "Festival de Música ao Vivo", "Música",
            "Festival Gastronômico", "Gastronomia",
            "Noite de Stand-up", "Comédia",
            "Conferência de Tecnologia", "Tecnologia",
            "Feira de Arte e Design", "Arte e design"
    );
    private static final Map<String, BigDecimal> DEMO_PRICES = Map.of(
            "Festival de Música ao Vivo", new BigDecimal("90.00"),
            "Festival Gastronômico", new BigDecimal("35.00"),
            "Noite de Stand-up", new BigDecimal("80.00"),
            "Conferência de Tecnologia", new BigDecimal("120.00"),
            "Feira de Arte e Design", new BigDecimal("40.00")
    );

    private final EventRepository eventRepository;

    public EventDataInitializer(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (eventRepository.count() > 0) {
            List<Event> demoEventsToCategorize = eventRepository.findAll().stream()
                    .filter(event -> DEMO_CATEGORIES.containsKey(event.getTitulo()))
                    .filter(event -> event.getCategoria() == null || event.getPrecoIngresso() == null)
                    .toList();
            demoEventsToCategorize.forEach(event -> {
                if (event.getCategoria() == null) {
                    event.setCategoria(DEMO_CATEGORIES.get(event.getTitulo()));
                }
                if (event.getPrecoIngresso() == null) {
                    event.setPrecoIngresso(DEMO_PRICES.get(event.getTitulo()));
                }
            });
            if (!demoEventsToCategorize.isEmpty()) {
                eventRepository.saveAll(demoEventsToCategorize);
            }
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        eventRepository.saveAll(List.of(
                new Event("Festival de Música ao Vivo",
                        "Uma noite com bandas e artistas brasileiros em vários palcos.",
                        now.plusDays(12).withHour(19).withMinute(0).withSecond(0).withNano(0),
                        "Parque Villa-Lobos, São Paulo", null, "Música", DEMO_PRICES.get("Festival de Música ao Vivo")),
                new Event("Festival Gastronômico",
                        "Sabores, pratos e experiências de chefs da região.",
                        now.plusDays(20).withHour(11).withMinute(0).withSecond(0).withNano(0),
                        "Parque da Cidade, São Paulo", null, "Gastronomia", DEMO_PRICES.get("Festival Gastronômico")),
                new Event("Noite de Stand-up",
                        "Uma noite de comédia com talentos da cena nacional.",
                        now.plusDays(28).withHour(20).withMinute(30).withSecond(0).withNano(0),
                        "Teatro Bradesco, São Paulo", null, "Comédia", DEMO_PRICES.get("Noite de Stand-up")),
                new Event("Conferência de Tecnologia",
                        "Palestras e networking sobre inovação e tecnologia.",
                        now.plusDays(35).withHour(9).withMinute(0).withSecond(0).withNano(0),
                        "Centro de Convenções, São Paulo", null, "Tecnologia", DEMO_PRICES.get("Conferência de Tecnologia")),
                new Event("Feira de Arte e Design",
                        "Exposição e venda de trabalhos de artistas e designers independentes.",
                        now.plusDays(42).withHour(10).withMinute(0).withSecond(0).withNano(0),
                        "Museu da Imagem e do Som, São Paulo", null, "Arte e design", DEMO_PRICES.get("Feira de Arte e Design"))
        ));
    }
}
