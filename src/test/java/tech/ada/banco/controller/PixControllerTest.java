package tech.ada.banco.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import tech.ada.banco.model.Conta;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PixControllerTest extends BaseContaTest {
    private final String baseUri = "/pix";

    @Test
    void testPixContasNaoEncontradas() throws Exception {
        Conta contaBase = criarConta(BigDecimal.ZERO);
        Conta contaDestino = criarConta(BigDecimal.ZERO);

        String response =
                mvc.perform(
                    post(baseUri + "/" + 1)
                        .param("destino", "2")
                        .param("valor", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(
                    print()
                ).andExpect(
                        status().isNotFound()
                ).andReturn()
                .getResponse()
                .getErrorMessage();

    }

    @Test
    void testPixContaOrigemNaoEncontrada() throws Exception {
        Conta contaBase = criarConta(BigDecimal.ZERO);
        Conta contaDestino = criarConta(BigDecimal.ZERO);

        String response =
                mvc.perform(
                        post(baseUri + "/" + 1)
                                .param("destino", String.valueOf(contaDestino.getNumeroConta()))
                                .param("valor", "10")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(
                        print()
                ).andExpect(
                        status().isNotFound()
                ).andReturn()
                        .getResponse()
                        .getErrorMessage();
    }

    @Test
    void testPixContaDestinoNaoEncontrada() throws Exception {
        Conta contaBase = criarConta(BigDecimal.ZERO);
        Conta contaDestino = criarConta(BigDecimal.ZERO);

        String response =
                mvc.perform(
                        post(baseUri + "/" + contaBase.getNumeroConta())
                                .param("destino", "1")
                                .param("valor", "10")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(
                        print()
                ).andExpect(
                        status().isNotFound()
                ).andReturn()
                        .getResponse()
                        .getErrorMessage();
    }

    @Test
    void testPixContaDestinoIgualOrigem() throws Exception {
        Conta contaBase = criarConta(BigDecimal.ZERO);

        String response =
                mvc.perform(
                        post(baseUri + "/" + contaBase.getNumeroConta())
                                .param("destino", String.valueOf(contaBase.getNumeroConta()))
                                .param("valor", "10")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(
                        print()
                ).andExpect(
                        status().isBadRequest()
                ).andReturn()
                        .getResponse()
                        .getErrorMessage();
    }

    @Test
    void testPixDeValorNegativo() throws Exception {
        Conta contaBase = criarConta(BigDecimal.TEN);
        Conta contaDestino = criarConta(BigDecimal.ZERO);

        String response =
                mvc.perform(
                        post(baseUri + "/" + contaBase.getNumeroConta())
                                .param("destino", String.valueOf(contaDestino.getNumeroConta()))
                                .param("valor", "-3")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(
                        print()
                ).andExpect(
                        status().isBadRequest()
                ).andReturn()
                        .getResponse()
                        .getErrorMessage();
    }

    @Test
    void testPixComSaldoZerado() throws Exception {
        Conta contaBase = criarConta(BigDecimal.ZERO);
        Conta contaDestino = criarConta(BigDecimal.ZERO);

        String response =
                mvc.perform(
                        post(baseUri + "/" + contaBase.getNumeroConta())
                                .param("destino", String.valueOf(contaDestino.getNumeroConta()))
                                .param("valor", "10")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(
                        print()
                ).andExpect(
                        status().isBadRequest()
                ).andReturn()
                        .getResponse()
                        .getErrorMessage();
    }


    @Test
    void testPixParcial() throws Exception {
        Conta contaBase = criarConta(BigDecimal.TEN);
        Conta contaDestino = criarConta(BigDecimal.ZERO);

        String response =
                mvc.perform(
                        post(baseUri + "/" + contaBase.getNumeroConta())
                                .param("destino", String.valueOf(contaDestino.getNumeroConta()))
                                .param("valor", "3")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(
                        print()
                ).andExpect(
                        status().isOk()
                ).andReturn()
                        .getResponse()
                        .getErrorMessage();

        assertEquals(BigDecimal.valueOf(7).setScale(2), obtemContaDoBanco(contaBase).getSaldo());
        assertEquals(BigDecimal.valueOf(3).setScale(2), obtemContaDoBanco(contaDestino).getSaldo());
    }


    @Test
    void testPixTotal() throws Exception {
        Conta contaBase = criarConta(BigDecimal.TEN);
        Conta contaDestino = criarConta(BigDecimal.ZERO);

        String response =
                mvc.perform(
                        post(baseUri + "/" + contaBase.getNumeroConta())
                                .param("destino", String.valueOf(contaDestino.getNumeroConta()))
                                .param("valor", "10")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(
                        print()
                ).andExpect(
                        status().isOk()
                ).andReturn()
                        .getResponse()
                        .getErrorMessage();

        assertEquals(BigDecimal.ZERO.setScale(2), obtemContaDoBanco(contaBase).getSaldo());
        assertEquals(BigDecimal.TEN.setScale(2), obtemContaDoBanco(contaDestino).getSaldo());
    }


    @Test
    void testPixNumeroQuebrado() throws Exception {
        Conta contaBase = criarConta(BigDecimal.TEN);
        Conta contaDestino = criarConta(BigDecimal.ZERO);

        String response =
                mvc.perform(
                        post(baseUri + "/" + contaBase.getNumeroConta())
                                .param("destino", String.valueOf(contaDestino.getNumeroConta()))
                                .param("valor", "2.35")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(
                        print()
                ).andExpect(
                        status().isOk()
                ).andReturn()
                        .getResponse()
                        .getErrorMessage();

        assertEquals(BigDecimal.valueOf(7.65).setScale(2), obtemContaDoBanco(contaBase).getSaldo());
        assertEquals(BigDecimal.valueOf(2.35).setScale(2), obtemContaDoBanco(contaDestino).getSaldo());
    }


    @Test
    void testPixArredondamentoParaCima() throws Exception {
        Conta contaBase = criarConta(BigDecimal.TEN);
        Conta contaDestino = criarConta(BigDecimal.ZERO);

        String response =
                mvc.perform(
                        post(baseUri + "/" + contaBase.getNumeroConta())
                                .param("destino", String.valueOf(contaDestino.getNumeroConta()))
                                .param("valor", "2.359")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(
                        print()
                ).andExpect(
                        status().isOk()
                ).andReturn()
                        .getResponse()
                        .getErrorMessage();

        assertEquals(BigDecimal.valueOf(7.64).setScale(2), obtemContaDoBanco(contaBase).getSaldo());
        assertEquals(BigDecimal.valueOf(2.36).setScale(2), obtemContaDoBanco(contaDestino).getSaldo());
    }

    @Test
    void testPixArredondamentoParaBaixo() throws Exception {
        Conta contaBase = criarConta(BigDecimal.TEN);
        Conta contaDestino = criarConta(BigDecimal.ZERO);

        String response =
                mvc.perform(
                        post(baseUri + "/" + contaBase.getNumeroConta())
                                .param("destino", String.valueOf(contaDestino.getNumeroConta()))
                                .param("valor", "2.351")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(
                        print()
                ).andExpect(
                        status().isOk()
                ).andReturn()
                        .getResponse()
                        .getErrorMessage();

        assertEquals(BigDecimal.valueOf(7.65).setScale(2), obtemContaDoBanco(contaBase).getSaldo());
        assertEquals(BigDecimal.valueOf(2.35).setScale(2), obtemContaDoBanco(contaDestino).getSaldo());
    }

    @Test
    void testPixArredondamentoImpar() throws Exception {
        Conta contaBase = criarConta(BigDecimal.TEN);
        Conta contaDestino = criarConta(BigDecimal.ZERO);

        String response =
                mvc.perform(
                        post(baseUri + "/" + contaBase.getNumeroConta())
                                .param("destino", String.valueOf(contaDestino.getNumeroConta()))
                                .param("valor", "2.355")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(
                        print()
                ).andExpect(
                        status().isOk()
                ).andReturn()
                        .getResponse()
                        .getErrorMessage();

        assertEquals(BigDecimal.valueOf(7.64).setScale(2), obtemContaDoBanco(contaBase).getSaldo());
        assertEquals(BigDecimal.valueOf(2.36).setScale(2), obtemContaDoBanco(contaDestino).getSaldo());
    }

    @Test
    void testPixArredondamentoPar() throws Exception {
        Conta contaBase = criarConta(BigDecimal.TEN);
        Conta contaDestino = criarConta(BigDecimal.ZERO);

        String response =
                mvc.perform(
                        post(baseUri + "/" + contaBase.getNumeroConta())
                                .param("destino", String.valueOf(contaDestino.getNumeroConta()))
                                .param("valor", "2.365")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(
                        print()
                ).andExpect(
                        status().isOk()
                ).andReturn()
                        .getResponse()
                        .getErrorMessage();

        assertEquals(BigDecimal.valueOf(7.64).setScale(2), obtemContaDoBanco(contaBase).getSaldo());
        assertEquals(BigDecimal.valueOf(2.36).setScale(2), obtemContaDoBanco(contaDestino).getSaldo());
    }
}