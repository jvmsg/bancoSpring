package tech.ada.banco.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import tech.ada.banco.model.Conta;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DepositoControllerTest extends BaseContaTest {

    private final String baseUri = "/deposito";

    @Test
    void testDepositoContaNaoEncontrada() throws Exception {
        Conta contaBase = criarConta(BigDecimal.TEN);
        Optional<Conta> contaInexistente = repository.findContaByNumeroConta(9999);
        assertTrue(contaInexistente.isEmpty());

        String response =
                mvc.perform(post(baseUri + "/9999")
                        .param("valor", "3.7")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isNotFound())
                        .andReturn().getResponse().getContentAsString();

        contaBase = obtemContaDoBanco(contaBase);
        assertEquals(BigDecimal.valueOf(10).setScale(2), contaBase.getSaldo());
    }

    @Test
    void testDepositoDeValorNegativo() throws Exception {
        Conta contaBase = criarConta(BigDecimal.TEN);

        String response =
                mvc.perform(post(baseUri + "/" + contaBase.getNumeroConta())
                        .param("valor", "-3.7")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isBadRequest())
                        .andReturn().getResponse().getContentAsString();

        contaBase = obtemContaDoBanco(contaBase);
        assertEquals(BigDecimal.valueOf(10).setScale(2), contaBase.getSaldo());
    }

    @Test
    void testDepositoComSaldoZerado() throws Exception {
        Conta contaBase = criarConta(BigDecimal.ZERO);

        String response =
                mvc.perform(post(baseUri + "/" + contaBase.getNumeroConta())
                        .param("valor", "3")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString();

        assertEquals(BigDecimal.valueOf(3).setScale(2), BigDecimal.valueOf(Double.parseDouble(response)).setScale(2));

        contaBase = obtemContaDoBanco(contaBase);

        assertEquals(BigDecimal.valueOf(3).setScale(2), contaBase.getSaldo());
    }

    @Test
    void testDepositosConsecutivos() throws Exception {
        Conta contaBase = criarConta(BigDecimal.ZERO);

        String response;

        response =
                mvc.perform(post(baseUri + "/" + contaBase.getNumeroConta())
                        .param("valor", "4.3")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString();

        assertEquals(BigDecimal.valueOf(4.3).setScale(2), BigDecimal.valueOf(Double.parseDouble(response)).setScale(2));

        contaBase = obtemContaDoBanco(contaBase);

        assertEquals(BigDecimal.valueOf(4.3).setScale(2), contaBase.getSaldo());

        response =
                mvc.perform(post(baseUri + "/" + contaBase.getNumeroConta())
                        .param("valor", "7.1")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString();

        assertEquals(BigDecimal.valueOf(11.4).setScale(2), BigDecimal.valueOf(Double.parseDouble(response)).setScale(2));

        contaBase = obtemContaDoBanco(contaBase);

        assertEquals(BigDecimal.valueOf(11.4).setScale(2), contaBase.getSaldo());
    }

    @Test
    void testDepositoDeNumeroQuebrado() throws Exception {
        Conta contaBase = criarConta(BigDecimal.ZERO);

        String response =
                mvc.perform(post(baseUri + "/" + contaBase.getNumeroConta())
                        .param("valor", "3.7")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString();

        assertEquals(BigDecimal.valueOf(3.7).setScale(2), BigDecimal.valueOf(Double.parseDouble(response)).setScale(2));

        contaBase = obtemContaDoBanco(contaBase);

        assertEquals(BigDecimal.valueOf(3.7).setScale(2), contaBase.getSaldo());
    }

    @Test
    void testDepositoArredondamentoParaCima() throws Exception {
        Conta contaBase = criarConta(BigDecimal.ZERO);

        String response =
                mvc.perform(post(baseUri + "/" + contaBase.getNumeroConta())
                        .param("valor", "3.799")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString();

        assertEquals(BigDecimal.valueOf(3.8).setScale(2), BigDecimal.valueOf(Double.parseDouble(response)).setScale(2));

        contaBase = obtemContaDoBanco(contaBase);

        assertEquals(BigDecimal.valueOf(3.8).setScale(2), contaBase.getSaldo());
    }

    @Test
    void testDepositoArredondamentoParaBaixo() throws Exception {
        Conta contaBase = criarConta(BigDecimal.ZERO);

        String response =
                mvc.perform(post(baseUri + "/" + contaBase.getNumeroConta())
                        .param("valor", "3.612")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString();

        assertEquals(BigDecimal.valueOf(3.61).setScale(2), BigDecimal.valueOf(Double.parseDouble(response)).setScale(2));

        contaBase = obtemContaDoBanco(contaBase);

        assertEquals(BigDecimal.valueOf(3.61).setScale(2), contaBase.getSaldo());
    }

    @Test
    void testDepositoArredondamentoImpar() throws Exception {
        Conta contaBase = criarConta(BigDecimal.ZERO);

        String response =
                mvc.perform(post(baseUri + "/" + contaBase.getNumeroConta())
                        .param("valor", "5.651")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString();

        assertEquals(BigDecimal.valueOf(5.65).setScale(2), BigDecimal.valueOf(Double.parseDouble(response)).setScale(2));

        contaBase = obtemContaDoBanco(contaBase);

        assertEquals(BigDecimal.valueOf(5.65).setScale(2), contaBase.getSaldo());
    }

    @Test
    void testDepositoArredondamentoPar() throws Exception {
        Conta contaBase = criarConta(BigDecimal.ZERO);

        String response =
                mvc.perform(post(baseUri + "/" + contaBase.getNumeroConta())
                        .param("valor", "2.458")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString();

        assertEquals(BigDecimal.valueOf(2.46).setScale(2), BigDecimal.valueOf(Double.parseDouble(response)).setScale(2));

        contaBase = obtemContaDoBanco(contaBase);

        assertEquals(BigDecimal.valueOf(2.46).setScale(2), contaBase.getSaldo());
    }

}