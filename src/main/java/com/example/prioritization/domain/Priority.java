package com.example.prioritization.domain;

import java.util.Optional;

/**
 * Prioridades válidas de um chamado, declaradas na ordem de processamento:
 * ALTA (maior prioridade) -> MEDIA -> BAIXA (menor prioridade).
 *
 * <p>A ordem de declaração (ordinal) representa a ordem de ordenação exigida
 * pela regra de negócio.</p>
 */
public enum Priority {

    ALTA("Alta"),
    MEDIA("Média"),
    BAIXA("Baixa");

    private final String label;

    Priority(String label) {
        this.label = label;
    }

    /** Rótulo de exibição da prioridade, conforme as regras de negócio. */
    public String getLabel() {
        return label;
    }

    /**
     * Resolve o valor textual recebido para uma prioridade válida.
     *
     * <p>Normalização aplicada: remoção de espaços nas extremidades (trim) e
     * comparação sem distinção entre maiúsculas e minúsculas.</p>
     *
     * <p>Valores {@code null}, vazios ou que não correspondam a uma prioridade
     * válida resultam em {@link Optional#empty()}, sinalizando prioridade
     * desconhecida. Nenhum valor desconhecido é convertido para uma prioridade
     * válida.</p>
     *
     * @param rawValue valor textual bruto da prioridade (pode ser nulo)
     * @return a prioridade correspondente, ou vazio se desconhecida
     */
    public static Optional<Priority> fromRawValue(String rawValue) {
        if (rawValue == null) {
            return Optional.empty();
        }

        String normalized = rawValue.trim();
        if (normalized.isEmpty()) {
            return Optional.empty();
        }

        for (Priority priority : values()) {
            if (priority.name().equalsIgnoreCase(normalized)
                    || priority.label.equalsIgnoreCase(normalized)) {
                return Optional.of(priority);
            }
        }

        return Optional.empty();
    }
}
