package com.example.prioritization.service;

import com.example.prioritization.api.dto.FlaggedTicket;
import com.example.prioritization.api.dto.PrioritizationResponse;
import com.example.prioritization.api.dto.PrioritizedTicket;
import com.example.prioritization.api.dto.TicketRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Testes unitários do serviço de priorização de chamados.
 *
 * <p>Cada teste valida uma regra de negócio descrita em docs/requirements.md
 * (cenários CT01 a CT20), verificando entradas, saídas, casos normais, de
 * limite e de erro.</p>
 */
@DisplayName("TicketPrioritizationService")
class TicketPrioritizationServiceTest {

    private final TicketPrioritizationService service = new TicketPrioritizationService();

    private static TicketRequest ticket(String id, String priority) {
        return new TicketRequest(id, priority);
    }

    private static List<String> prioritizedIds(PrioritizationResponse response) {
        return response.prioritized().stream().map(PrioritizedTicket::id).toList();
    }

    @Test
    @DisplayName("CT01 - ordena lista com todas as prioridades (Alta, Média, Baixa)")
    void ct01_todasAsPrioridades() {
        PrioritizationResponse response = service.prioritize(List.of(
                ticket("C1", "Alta"),
                ticket("C2", "Média"),
                ticket("C3", "Baixa")));

        assertThat(prioritizedIds(response)).containsExactly("C1", "C2", "C3");
        assertThat(response.flagged()).isEmpty();
    }

    @Test
    @DisplayName("CT02 - prioridade prevalece sobre a ordem de chegada")
    void ct02_foraDeOrdem() {
        PrioritizationResponse response = service.prioritize(List.of(
                ticket("C1", "Baixa"),
                ticket("C2", "Alta"),
                ticket("C3", "Média")));

        assertThat(prioritizedIds(response)).containsExactly("C2", "C3", "C1");
        assertThat(response.flagged()).isEmpty();
    }

    @Test
    @DisplayName("CT03 - vários Alta preservam a ordem de chegada")
    void ct03_variosAlta() {
        PrioritizationResponse response = service.prioritize(List.of(
                ticket("C1", "Alta"),
                ticket("C2", "Alta"),
                ticket("C3", "Alta")));

        assertThat(prioritizedIds(response)).containsExactly("C1", "C2", "C3");
    }

    @Test
    @DisplayName("CT04 - vários Média preservam a ordem de chegada")
    void ct04_variosMedia() {
        PrioritizationResponse response = service.prioritize(List.of(
                ticket("C1", "Média"),
                ticket("C2", "Média"),
                ticket("C3", "Média")));

        assertThat(prioritizedIds(response)).containsExactly("C1", "C2", "C3");
    }

    @Test
    @DisplayName("CT05 - vários Baixa preservam a ordem de chegada")
    void ct05_variosBaixa() {
        PrioritizationResponse response = service.prioritize(List.of(
                ticket("C1", "Baixa"),
                ticket("C2", "Baixa"),
                ticket("C3", "Baixa")));

        assertThat(prioritizedIds(response)).containsExactly("C1", "C2", "C3");
    }

    @Test
    @DisplayName("CT06 - mistura de prioridades com empates preserva ordem dentro do grupo")
    void ct06_misturaComEmpates() {
        PrioritizationResponse response = service.prioritize(List.of(
                ticket("C1", "Média"),
                ticket("C2", "Alta"),
                ticket("C3", "Média"),
                ticket("C4", "Baixa"),
                ticket("C5", "Alta")));

        assertThat(prioritizedIds(response)).containsExactly("C2", "C5", "C1", "C3", "C4");
    }

    @Test
    @DisplayName("CT07 - prioridade desconhecida é sinalizada e não vira Alta")
    void ct07_prioridadeDesconhecida() {
        PrioritizationResponse response = service.prioritize(List.of(
                ticket("C1", "Alta"),
                ticket("C2", "Urgente"),
                ticket("C3", "Baixa")));

        assertThat(prioritizedIds(response)).containsExactly("C1", "C3");
        assertThat(response.flagged())
                .extracting(FlaggedTicket::id, FlaggedTicket::receivedValue)
                .containsExactly(tuple("C2", "Urgente"));
        assertThat(response.flagged().get(0).message()).contains("Urgente");
    }

    @Test
    @DisplayName("CT08 - todas as prioridades desconhecidas: nenhuma é classificada")
    void ct08_todasDesconhecidas() {
        PrioritizationResponse response = service.prioritize(List.of(
                ticket("C1", "Urgente"),
                ticket("C2", "Crítica")));

        assertThat(response.prioritized()).isEmpty();
        assertThat(response.flagged())
                .extracting(FlaggedTicket::id)
                .containsExactly("C1", "C2");
    }

    @Test
    @DisplayName("CT09 - prioridade vazia é considerada desconhecida")
    void ct09_prioridadeVazia() {
        PrioritizationResponse response = service.prioritize(List.of(
                ticket("C1", "Alta"),
                ticket("C2", ""),
                ticket("C3", "Baixa")));

        assertThat(prioritizedIds(response)).containsExactly("C1", "C3");
        FlaggedTicket flagged = response.flagged().get(0);
        assertThat(flagged.id()).isEqualTo("C2");
        assertThat(flagged.receivedValue()).isNull();
        assertThat(flagged.message()).isEqualTo("prioridade desconhecida");
    }

    @Test
    @DisplayName("CT10 - prioridade nula é considerada desconhecida")
    void ct10_prioridadeNula() {
        List<TicketRequest> tickets = new ArrayList<>();
        tickets.add(ticket("C1", "Alta"));
        tickets.add(ticket("C2", null));
        tickets.add(ticket("C3", "Média"));

        PrioritizationResponse response = service.prioritize(tickets);

        assertThat(prioritizedIds(response)).containsExactly("C1", "C3");
        FlaggedTicket flagged = response.flagged().get(0);
        assertThat(flagged.id()).isEqualTo("C2");
        assertThat(flagged.receivedValue()).isNull();
        assertThat(flagged.message()).isEqualTo("prioridade desconhecida");
    }

    @Test
    @DisplayName("CT11 - lista vazia retorna resultado vazio sem erro")
    void ct11_listaVazia() {
        PrioritizationResponse response = service.prioritize(List.of());

        assertThat(response.prioritized()).isEmpty();
        assertThat(response.flagged()).isEmpty();
    }

    @Test
    @DisplayName("CT11b - lista nula é tratada como vazia")
    void ct11b_listaNula() {
        PrioritizationResponse response = service.prioritize(null);

        assertThat(response.prioritized()).isEmpty();
        assertThat(response.flagged()).isEmpty();
    }

    @Test
    @DisplayName("CT12 - um único chamado é retornado sem alteração")
    void ct12_umUnicoChamado() {
        PrioritizationResponse response = service.prioritize(List.of(
                ticket("C1", "Média")));

        assertThat(prioritizedIds(response)).containsExactly("C1");
        assertThat(response.flagged()).isEmpty();
    }

    @Test
    @DisplayName("CT13 - apenas Alta e Baixa: Alta precede Baixa e preserva ordem")
    void ct13_apenasAltaEBaixa() {
        PrioritizationResponse response = service.prioritize(List.of(
                ticket("C1", "Baixa"),
                ticket("C2", "Alta"),
                ticket("C3", "Baixa")));

        assertThat(prioritizedIds(response)).containsExactly("C2", "C1", "C3");
    }

    @Test
    @DisplayName("CT14 - apenas Média e Baixa: Média precede Baixa e preserva ordem")
    void ct14_apenasMediaEBaixa() {
        PrioritizationResponse response = service.prioritize(List.of(
                ticket("C1", "Baixa"),
                ticket("C2", "Média"),
                ticket("C3", "Baixa")));

        assertThat(prioritizedIds(response)).containsExactly("C2", "C1", "C3");
    }

    @Test
    @DisplayName("CT15 - diferença de capitalização é normalizada (alta, BAIXA)")
    void ct15_capitalizacao() {
        PrioritizationResponse response = service.prioritize(List.of(
                ticket("C1", "alta"),
                ticket("C2", "Média"),
                ticket("C3", "BAIXA")));

        assertThat(prioritizedIds(response)).containsExactly("C1", "C2", "C3");
        assertThat(response.flagged()).isEmpty();
        assertThat(response.prioritized())
                .extracting(PrioritizedTicket::priority)
                .containsExactly("Alta", "Média", "Baixa");
    }

    @Test
    @DisplayName("CT16 - prioridade com espaços é normalizada (trim)")
    void ct16_espacos() {
        PrioritizationResponse response = service.prioritize(List.of(
                ticket("C1", " Alta "),
                ticket("C2", "Média")));

        assertThat(prioritizedIds(response)).containsExactly("C1", "C2");
        assertThat(response.flagged()).isEmpty();
    }

    @Test
    @DisplayName("CT17 - preservação da ordem entre prioridades iguais")
    void ct17_preservacaoOrdemEntreIguais() {
        PrioritizationResponse response = service.prioritize(List.of(
                ticket("C1", "Baixa"),
                ticket("C2", "Alta"),
                ticket("C3", "Média"),
                ticket("C4", "Alta"),
                ticket("C5", "Baixa"),
                ticket("C6", "Média")));

        assertThat(prioritizedIds(response)).containsExactly("C2", "C4", "C3", "C6", "C1", "C5");
    }

    @Test
    @DisplayName("CT18 - desconhecidas misturadas com válidas: ordena válidas e sinaliza desconhecidas")
    void ct18_desconhecidasMisturadas() {
        PrioritizationResponse response = service.prioritize(List.of(
                ticket("C1", "Baixa"),
                ticket("C2", "Urgente"),
                ticket("C3", "Alta"),
                ticket("C4", "Média"),
                ticket("C5", "Crítica"),
                ticket("C6", "Alta")));

        assertThat(prioritizedIds(response)).containsExactly("C3", "C6", "C4", "C1");
        assertThat(response.flagged())
                .extracting(FlaggedTicket::id, FlaggedTicket::receivedValue)
                .containsExactly(
                        tuple("C2", "Urgente"),
                        tuple("C5", "Crítica"));
    }

    @Test
    @DisplayName("CT19 - entrada já ordenada permanece inalterada")
    void ct19_entradaJaOrdenada() {
        PrioritizationResponse response = service.prioritize(List.of(
                ticket("C1", "Alta"),
                ticket("C2", "Alta"),
                ticket("C3", "Média"),
                ticket("C4", "Média"),
                ticket("C5", "Baixa")));

        assertThat(prioritizedIds(response)).containsExactly("C1", "C2", "C3", "C4", "C5");
    }

    @Test
    @DisplayName("CT20 - entrada totalmente invertida é reorganizada preservando ordem interna")
    void ct20_entradaInvertida() {
        PrioritizationResponse response = service.prioritize(List.of(
                ticket("C1", "Baixa"),
                ticket("C2", "Baixa"),
                ticket("C3", "Média"),
                ticket("C4", "Média"),
                ticket("C5", "Alta"),
                ticket("C6", "Alta")));

        assertThat(prioritizedIds(response)).containsExactly("C5", "C6", "C3", "C4", "C1", "C2");
    }
}
