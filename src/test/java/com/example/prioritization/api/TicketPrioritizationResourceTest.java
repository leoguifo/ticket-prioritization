package com.example.prioritization.api;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

/**
 * Testes de integração do endpoint REST de priorização de chamados.
 */
@QuarkusTest
@DisplayName("POST /tickets/prioritize")
class TicketPrioritizationResourceTest {

    @Test
    @DisplayName("prioriza chamados válidos e sinaliza desconhecidos (CT18)")
    void prioritizaESinaliza() {
        String body = """
                [
                  {"id": "C1", "priority": "Baixa"},
                  {"id": "C2", "priority": "Urgente"},
                  {"id": "C3", "priority": "Alta"},
                  {"id": "C4", "priority": "Média"},
                  {"id": "C5", "priority": "Crítica"},
                  {"id": "C6", "priority": "Alta"}
                ]
                """;

        given()
                .contentType("application/json")
                .body(body)
                .when()
                .post("/tickets/prioritize")
                .then()
                .statusCode(200)
                .body("prioritized.id", contains("C3", "C6", "C4", "C1"))
                .body("flagged.id", contains("C2", "C5"))
                .body("flagged[0].receivedValue", is("Urgente"))
                .body("flagged[1].receivedValue", is("Crítica"));
    }

    @Test
    @DisplayName("lista vazia retorna resultado vazio (CT11)")
    void listaVazia() {
        given()
                .contentType("application/json")
                .body("[]")
                .when()
                .post("/tickets/prioritize")
                .then()
                .statusCode(200)
                .body("prioritized", hasSize(0))
                .body("flagged", empty());
    }
}
