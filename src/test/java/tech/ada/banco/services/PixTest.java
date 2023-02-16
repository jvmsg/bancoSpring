package tech.ada.banco.services;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import tech.ada.banco.exceptions.ContaOrigemIgualDestinoException;
import tech.ada.banco.exceptions.ResourceNotFoundException;
import tech.ada.banco.exceptions.SaldoInsuficienteException;
import tech.ada.banco.exceptions.ValorInvalidoException;
import tech.ada.banco.model.Conta;
import tech.ada.banco.model.ModalidadeConta;
import tech.ada.banco.repository.ContaRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PixTest {

    private final ContaRepository repository = Mockito.mock(ContaRepository.class);
    private final Pix pix = new Pix(repository);

    @Test
    void testPixContasNaoEncontradas() {
        Conta conta = new Conta(ModalidadeConta.CC, null);
        when(repository.findContaByNumeroConta(10)).thenReturn(Optional.of(conta));
        assertEquals(BigDecimal.ZERO.setScale(2), conta.getSaldo(), "O saldo inicial da conta deve ser 0.00.");

        try {
            pix.executar(1, 2, BigDecimal.ONE);
            fail("A conta deveria não ter sido encontrada.");
        } catch (ResourceNotFoundException e) {

        }

        verify(repository, times(0)).save(any());
        assertEquals(BigDecimal.ZERO.setScale(2), conta.getSaldo(), "O saldo da conta não pode ter sido alterado.");
    }

    @Test
    void testPixContaOrigemNaoEncontrada() {
        Conta conta = new Conta(ModalidadeConta.CC, null);
        when(repository.findContaByNumeroConta(10)).thenReturn(Optional.of(conta));
        assertEquals(BigDecimal.ZERO.setScale(2), conta.getSaldo(), "O saldo inicial da conta deve ser 0.00.");

        try {
            pix.executar(1, 10, BigDecimal.ONE);
            fail("A conta deveria não ter sido encontrada.");
        } catch (ResourceNotFoundException e) {

        }

        verify(repository, times(0)).save(any());
        assertEquals(BigDecimal.ZERO.setScale(2), conta.getSaldo(), "O saldo da conta não pode ter sido alterado.");
    }

    @Test
    void testPixContaDestinoNaoEncontrada() {
        Conta conta = new Conta(ModalidadeConta.CC, null);
        when(repository.findContaByNumeroConta(10)).thenReturn(Optional.of(conta));
        assertEquals(BigDecimal.ZERO.setScale(2), conta.getSaldo(), "O saldo inicial da conta deve ser 0.00.");

        try {
            pix.executar(10, 1, BigDecimal.ONE);
            fail("A conta deveria não ter sido encontrada.");
        } catch (ResourceNotFoundException e) {

        }

        verify(repository, times(0)).save(any());
        assertEquals(BigDecimal.ZERO.setScale(2), conta.getSaldo(), "O saldo da conta não pode ter sido alterado.");
    }

    @Test
    void testPixContaDestinoIgualOrigem() {
        Conta conta = new Conta(ModalidadeConta.CC, null);
        when(repository.findContaByNumeroConta(10)).thenReturn(Optional.of(conta));
        assertEquals(BigDecimal.ZERO.setScale(2), conta.getSaldo(), "O saldo inicial da conta deve ser 0.00.");

        try {
            pix.executar(10, 10, BigDecimal.ONE);
            fail("Não deveria ser possível realizar um pix da conta 10 para a conta 10.");
        } catch (ContaOrigemIgualDestinoException e) {
        }

        verify(repository, times(0)).save(any());
        assertEquals(BigDecimal.ZERO.setScale(2), conta.getSaldo(), "O saldo da conta não pode ter sido alterado.");
    }

    @Test
    void testPixDeValorNegativo() {
        Conta conta10 = new Conta(ModalidadeConta.CC, null);
        assertEquals(BigDecimal.ZERO.setScale(2), conta10.getSaldo(), "O saldo inicial da conta 10 deve ser igual a 0.00.");
        conta10.deposito(BigDecimal.TEN);
        assertEquals(BigDecimal.TEN.setScale(2), conta10.getSaldo(), "O saldo da conta 10 deve ser alterado para 10.00.");

        Conta conta5 = new Conta(ModalidadeConta.CC, null);
        assertEquals(BigDecimal.ZERO.setScale(2), conta5.getSaldo(), "O saldo inicial da conta deve ser igual a 0.00.");


        when(repository.findContaByNumeroConta(10)).thenReturn(Optional.of(conta10));
        when(repository.findContaByNumeroConta(5)).thenReturn(Optional.of(conta5));

        try {
            pix.executar(10, 5, BigDecimal.valueOf(-10));
            fail("A conta deveria lançar o erro ValorInvalidoException ao tentar realizar pix de um valor negativo.");
        } catch (ValorInvalidoException e) {}

        assertEquals(BigDecimal.TEN.setScale(2), conta10.getSaldo(), "O saldo da conta deve continuar sendo igual a 10.00.");
        assertEquals(BigDecimal.ZERO.setScale(2), conta5.getSaldo(), "O saldo da conta deve continuar sendo igual a 0.00.");
    }


    @Test
    void testPixComSaldoZerado() {
        Conta conta10 = new Conta(ModalidadeConta.CC, null);
        assertEquals(BigDecimal.ZERO.setScale(2), conta10.getSaldo(), "O saldo inicial da conta 10 deve ser igual a 0.00.");
        conta10.deposito(BigDecimal.TEN);
        assertEquals(BigDecimal.TEN.setScale(2), conta10.getSaldo(), "O saldo da conta 10 deve ser alterado para 10.00.");

        Conta conta5 = new Conta(ModalidadeConta.CC, null);
        assertEquals(BigDecimal.ZERO.setScale(2), conta5.getSaldo(), "O saldo inicial da conta deve ser igual a 0.00.");

        when(repository.findContaByNumeroConta(10)).thenReturn(Optional.of(conta10));
        when(repository.findContaByNumeroConta(5)).thenReturn(Optional.of(conta5));

        try {
            pix.executar(5, 10, BigDecimal.valueOf(5));
            fail("A conta deveria lançar o erro SaldoInsuficienteException ao tentar realizar pix com saldo zerado.");
        } catch (SaldoInsuficienteException e) {}

        assertEquals(BigDecimal.TEN.setScale(2), conta10.getSaldo(), "O saldo da conta deve continuar sendo igual a 10.00.");
        assertEquals(BigDecimal.ZERO.setScale(2), conta5.getSaldo(), "O saldo da conta deve continuar sendo igual a 0.00.");
    }


    @Test
    void testPixParcial() {
        Conta conta10 = new Conta(ModalidadeConta.CC, null);
        assertEquals(BigDecimal.ZERO.setScale(2), conta10.getSaldo(), "O saldo inicial da conta 10 deve ser igual a 0.00.");
        conta10.deposito(BigDecimal.TEN);
        assertEquals(BigDecimal.TEN.setScale(2), conta10.getSaldo(), "O saldo da conta 10 deve ser alterado para 10.00.");

        Conta conta5 = new Conta(ModalidadeConta.CC, null);
        assertEquals(BigDecimal.ZERO.setScale(2), conta5.getSaldo(), "O saldo inicial da conta deve ser igual a 0.00.");

        when(repository.findContaByNumeroConta(10)).thenReturn(Optional.of(conta10));
        when(repository.findContaByNumeroConta(5)).thenReturn(Optional.of(conta5));

        try {
            pix.executar(10, 5, BigDecimal.valueOf(6));
        } catch (SaldoInsuficienteException e) {}

        assertEquals(BigDecimal.valueOf(4).setScale(2), conta10.getSaldo(), "O saldo da conta deve continuar sendo igual a 4.00.");
        assertEquals(BigDecimal.valueOf(6).setScale(2), conta5.getSaldo(), "O saldo da conta deve continuar sendo igual a 6.00.");
    }

    @Test
    void testPixTotal() {
        Conta conta10 = new Conta(ModalidadeConta.CC, null);
        assertEquals(BigDecimal.ZERO.setScale(2), conta10.getSaldo(), "O saldo inicial da conta 10 deve ser igual a 0.00.");
        conta10.deposito(BigDecimal.TEN);
        assertEquals(BigDecimal.TEN.setScale(2), conta10.getSaldo(), "O saldo da conta 10 deve ser alterado para 10.00.");

        Conta conta5 = new Conta(ModalidadeConta.CC, null);
        assertEquals(BigDecimal.ZERO.setScale(2), conta5.getSaldo(), "O saldo inicial da conta deve ser igual a 0.00.");

        when(repository.findContaByNumeroConta(10)).thenReturn(Optional.of(conta10));
        when(repository.findContaByNumeroConta(5)).thenReturn(Optional.of(conta5));

        try {
            pix.executar(10, 5, BigDecimal.TEN);
        } catch (SaldoInsuficienteException e) {}

        assertEquals(BigDecimal.ZERO.setScale(2), conta10.getSaldo(), "O saldo da conta deve continuar sendo igual a 0.00.");
        assertEquals(BigDecimal.TEN.setScale(2), conta5.getSaldo(), "O saldo da conta deve continuar sendo igual a 10.00.");
    }

    @Test
    void testPixNumeroQuebrado() {
        Conta conta10 = new Conta(ModalidadeConta.CC, null);
        assertEquals(BigDecimal.ZERO.setScale(2), conta10.getSaldo(), "O saldo inicial da conta 10 deve ser igual a 0.00.");
        conta10.deposito(BigDecimal.TEN);
        assertEquals(BigDecimal.TEN.setScale(2), conta10.getSaldo(), "O saldo da conta 10 deve ser alterado para 10.00.");

        Conta conta5 = new Conta(ModalidadeConta.CC, null);
        assertEquals(BigDecimal.ZERO.setScale(2), conta5.getSaldo(), "O saldo inicial da conta deve ser igual a 0.00.");

        when(repository.findContaByNumeroConta(10)).thenReturn(Optional.of(conta10));
        when(repository.findContaByNumeroConta(5)).thenReturn(Optional.of(conta5));

        try {
            pix.executar(10, 5, BigDecimal.valueOf(4.37));
        } catch (SaldoInsuficienteException e) {}

        assertEquals(BigDecimal.valueOf(5.63).setScale(2), conta10.getSaldo(), "O saldo da conta deve continuar sendo igual a 5.63.");
        assertEquals(BigDecimal.valueOf(4.37).setScale(2), conta5.getSaldo(), "O saldo da conta deve continuar sendo igual a 4.37.");
    }

    @Test
    void testPixArredondamentoParaCima() {
        Conta conta10 = new Conta(ModalidadeConta.CC, null);
        assertEquals(BigDecimal.ZERO.setScale(2), conta10.getSaldo(), "O saldo inicial da conta 10 deve ser igual a 0.00.");
        conta10.deposito(BigDecimal.TEN);
        assertEquals(BigDecimal.TEN.setScale(2), conta10.getSaldo(), "O saldo da conta 10 deve ser alterado para 10.00.");

        Conta conta5 = new Conta(ModalidadeConta.CC, null);
        assertEquals(BigDecimal.ZERO.setScale(2), conta5.getSaldo(), "O saldo inicial da conta deve ser igual a 0.00.");

        when(repository.findContaByNumeroConta(10)).thenReturn(Optional.of(conta10));
        when(repository.findContaByNumeroConta(5)).thenReturn(Optional.of(conta5));

        try {
            pix.executar(10, 5, BigDecimal.valueOf(4.379));
        } catch (SaldoInsuficienteException e) {}

        assertEquals(BigDecimal.valueOf(5.62).setScale(2), conta10.getSaldo(), "O saldo da conta deve continuar sendo igual a 5.62.");
        assertEquals(BigDecimal.valueOf(4.38).setScale(2), conta5.getSaldo(), "O saldo da conta deve continuar sendo igual a 4.38.");
    }

    @Test
    void testPixArredondamentoParaBaixo() {
        Conta conta10 = new Conta(ModalidadeConta.CC, null);
        assertEquals(BigDecimal.ZERO.setScale(2), conta10.getSaldo(), "O saldo inicial da conta 10 deve ser igual a 0.00.");
        conta10.deposito(BigDecimal.TEN);
        assertEquals(BigDecimal.TEN.setScale(2), conta10.getSaldo(), "O saldo da conta 10 deve ser alterado para 10.00.");

        Conta conta5 = new Conta(ModalidadeConta.CC, null);
        assertEquals(BigDecimal.ZERO.setScale(2), conta5.getSaldo(), "O saldo inicial da conta deve ser igual a 0.00.");

        when(repository.findContaByNumeroConta(10)).thenReturn(Optional.of(conta10));
        when(repository.findContaByNumeroConta(5)).thenReturn(Optional.of(conta5));

        try {
            pix.executar(10, 5, BigDecimal.valueOf(4.371));
        } catch (SaldoInsuficienteException e) {}

        assertEquals(BigDecimal.valueOf(5.63).setScale(2), conta10.getSaldo(), "O saldo da conta deve continuar sendo igual a 5.63.");
        assertEquals(BigDecimal.valueOf(4.37).setScale(2), conta5.getSaldo(), "O saldo da conta deve continuar sendo igual a 4.37.");
    }

    @Test
    void testPixArredondamentoImpar() {
        Conta conta10 = new Conta(ModalidadeConta.CC, null);
        assertEquals(BigDecimal.ZERO.setScale(2), conta10.getSaldo(), "O saldo inicial da conta 10 deve ser igual a 0.00.");
        conta10.deposito(BigDecimal.TEN);
        assertEquals(BigDecimal.TEN.setScale(2), conta10.getSaldo(), "O saldo da conta 10 deve ser alterado para 10.00.");

        Conta conta5 = new Conta(ModalidadeConta.CC, null);
        assertEquals(BigDecimal.ZERO.setScale(2), conta5.getSaldo(), "O saldo inicial da conta deve ser igual a 0.00.");

        when(repository.findContaByNumeroConta(10)).thenReturn(Optional.of(conta10));
        when(repository.findContaByNumeroConta(5)).thenReturn(Optional.of(conta5));

        try {
            pix.executar(10, 5, BigDecimal.valueOf(4.375));
        } catch (SaldoInsuficienteException e) {}

        assertEquals(BigDecimal.valueOf(5.62).setScale(2), conta10.getSaldo(), "O saldo da conta deve continuar sendo igual a 5.63.");
        assertEquals(BigDecimal.valueOf(4.38).setScale(2), conta5.getSaldo(), "O saldo da conta deve continuar sendo igual a 4.37.");
    }

    @Test
    void testPixArredondamentoPar() {
        Conta conta10 = new Conta(ModalidadeConta.CC, null);
        assertEquals(BigDecimal.ZERO.setScale(2), conta10.getSaldo(), "O saldo inicial da conta 10 deve ser igual a 0.00.");
        conta10.deposito(BigDecimal.TEN);
        assertEquals(BigDecimal.TEN.setScale(2), conta10.getSaldo(), "O saldo da conta 10 deve ser alterado para 10.00.");

        Conta conta5 = new Conta(ModalidadeConta.CC, null);
        assertEquals(BigDecimal.ZERO.setScale(2), conta5.getSaldo(), "O saldo inicial da conta deve ser igual a 0.00.");

        when(repository.findContaByNumeroConta(10)).thenReturn(Optional.of(conta10));
        when(repository.findContaByNumeroConta(5)).thenReturn(Optional.of(conta5));

        try {
            pix.executar(10, 5, BigDecimal.valueOf(4.365));
        } catch (SaldoInsuficienteException e) {}

        assertEquals(BigDecimal.valueOf(5.64).setScale(2), conta10.getSaldo(), "O saldo da conta deve continuar sendo igual a 5.63.");
        assertEquals(BigDecimal.valueOf(4.36).setScale(2), conta5.getSaldo(), "O saldo da conta deve continuar sendo igual a 4.37.");
    }
}