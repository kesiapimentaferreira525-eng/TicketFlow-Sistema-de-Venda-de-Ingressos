package TicketFlow_Sistema_de_Venda_de_Ingressos.api.Model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O título é obrigatório")
    @Column(nullable = false)
    private String titulo;

    @NotBlank(message = "A descrição é obrigatória")
    @Column(nullable = false, length = 1000)
    private String descricao;

    @NotNull(message = "A data e hora são obrigatórias")
    @Future(message = "A data e hora do evento devem ser futuras")
    @Column(nullable = false)
    private LocalDateTime dataHora;

    @NotBlank(message = "O local é obrigatório")
    @Column(nullable = false)
    private String local;

    @Column
    private String imagemUrl;

    @Column(length = 100)
    private String categoria;

    @Column(name = "preco_ingresso", precision = 10, scale = 2)
    private BigDecimal precoIngresso = BigDecimal.ZERO;

    public Event() {}

    public Event(String titulo, String descricao, LocalDateTime dataHora, String local, String imagemUrl, String categoria) {
        this(titulo, descricao, dataHora, local, imagemUrl, categoria, BigDecimal.ZERO);
    }

    public Event(String titulo, String descricao, LocalDateTime dataHora, String local, String imagemUrl, String categoria, BigDecimal precoIngresso) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.dataHora = dataHora;
        this.local = local;
        this.imagemUrl = imagemUrl;
        this.categoria = categoria;
        this.precoIngresso = precoIngresso;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public String getLocal() { return local; }
    public void setLocal(String local) { this.local = local; }

    public String getImagemUrl() { return imagemUrl; }
    public void setImagemUrl(String imagemUrl) { this.imagemUrl = imagemUrl; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public BigDecimal getPrecoIngresso() { return precoIngresso; }
    public void setPrecoIngresso(BigDecimal precoIngresso) { this.precoIngresso = precoIngresso; }
}