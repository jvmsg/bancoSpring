package tech.ada.banco.services;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import tech.ada.banco.exceptions.ResourceNotFoundException;
import tech.ada.banco.exceptions.ValorInvalidoException;
import tech.ada.banco.model.Conta;
import tech.ada.banco.model.ModalidadeConta;
import tech.ada.banco.repository.ContaRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

public class DepositoTest {

    private final ContaRepository repository = Mockito.mock(ContaRepository.class);
    private final Deposito deposito = new Deposito(repository);

    @Test
    void testDepositoContaNaoEncontrada() {
        Conta conta = new Conta(ModalidadeConta.CC, null);
        when(repository.findContaByNumeroConta(10)).thenReturn(Optional.of(conta));
        assertEquals(BigDecimal.ZERO, conta.getSaldo(), "O saldo inicial da conta deve ser 0.00.");

        try {
            deposito.executar(1, BigDecimal.ONE);
            fail("A conta deveria não ter sido encontrada.");
        } catch (ResourceNotFoundException e) {

        }

        verify(repository, times(0)).save(any());
        assertEquals(BigDecimal.ZERO, conta.getSaldo(), "O saldo da conta não pode ter sido alterado.");
    }

    @Test
    void testDepositoDeValorNegativo() {
        Conta conta = new Conta(ModalidadeConta.CC, null);
        assertEquals(BigDecimal.ZERO, conta.getSaldo(), "O saldo inicial da conta deve ser igual a 0.00.");
        when(repository.findContaByNumeroConta(10)).thenReturn(Optional.of(conta));
        try {
            deposito.executar(10, BigDecimal.valueOf(-10));
            fail("A conta deveria lançar o erro ValorInvalidoException ao tentar depositar um valor negativo.");
        } catch (ValorInvalidoException e) {}
        assertEquals(BigDecimal.ZERO, conta.getSaldo(), "O saldo da conta deve continuar sendo igual a 0.00.");
    }

    @Test
    void testDepositoComSaldoZerado() {
        Conta conta = new Conta(ModalidadeConta.CC, null);
        when(repository.findContaByNumeroConta(10)).thenReturn(Optional.of(conta));
        assertEquals(BigDecimal.ZERO, conta.getSaldo(), "O saldo inicial da conta deve ser igual a 0.00.");

        deposito.executar(10, BigDecimal.valueOf(12));
        assertEquals(BigDecimal.valueOf(12).setScale(2), conta.getSaldo(), "O saldo da conta deve ser alterado para 12.00.");
    }

    @Test
    void testDepositosConsecutivos() {
        Conta conta = new Conta(ModalidadeConta.CC, null);
        assertEquals(BigDecimal.ZERO, conta.getSaldo(), "O saldo inicial da conta deve ser igual a 0.00.");
        when(repository.findContaByNumeroConta(10)).thenReturn(Optional.of(conta));

        deposito.executar(10, BigDecimal.TEN);
        assertEquals(BigDecimal.valueOf(10).setScale(2), conta.getSaldo(), "O saldo da conta deve ser alterado para 10.00.");

        deposito.executar(10, BigDecimal.valueOf(17));
        assertEquals(BigDecimal.valueOf(27).setScale(2), conta.getSaldo(), "O saldo da conta deve ser alterado para 27.00.");
    }

    @Test
    void testDepositoDeNumeroQuebrado() {
        Conta conta = new Conta(ModalidadeConta.CC, null);
        when(repository.findContaByNumeroConta(10)).thenReturn(Optional.of(conta));
        assertEquals(BigDecimal.ZERO, conta.getSaldo(), "O saldo inicial da conta deve ser igual a 0.00.");

        deposito.executar(10, BigDecimal.valueOf(12.27));
        assertEquals(BigDecimal.valueOf(12.27), conta.getSaldo(), "O saldo da conta deve ser alterado para 12.27");
    }

    @Test
    void testDepositoArredondamentoParaCima() {
        Conta conta = new Conta(ModalidadeConta.CC, null);
        when(repository.findContaByNumeroConta(10)).thenReturn(Optional.of(conta));
        assertEquals(BigDecimal.ZERO, conta.getSaldo(), "O saldo inicial da conta deve ser igual a 0.00.");

        deposito.executar(10, BigDecimal.valueOf(12.279));
        assertEquals(BigDecimal.valueOf(12.28), conta.getSaldo(), "O saldo da conta deve ser alterado para 12.28");
    }

    @Test
    void testDepositoArredondamentoParaBaixo() {
        Conta conta = new Conta(ModalidadeConta.CC, null);
        when(repository.findContaByNumeroConta(10)).thenReturn(Optional.of(conta));
        assertEquals(BigDecimal.ZERO, conta.getSaldo(), "O saldo inicial da conta deve ser igual a 0.00.");

        deposito.executar(10, BigDecimal.valueOf(12.2709));
        assertEquals(BigDecimal.valueOf(12.27), conta.getSaldo(), "O saldo da conta deve ser alterado para 12.27");
    }

    @Test
    void testDepositoArredondamentoImpar() {
        Conta conta = new Conta(ModalidadeConta.CC, null);
        when(repository.findContaByNumeroConta(10)).thenReturn(Optional.of(conta));
        assertEquals(BigDecimal.ZERO, conta.getSaldo(), "O saldo inicial da conta deve ser igual a 0.00.");

        deposito.executar(10, BigDecimal.valueOf(10.275));
        assertEquals(BigDecimal.valueOf(10.28), conta.getSaldo(), "O saldo da conta deve ser alterado para 10.28");
    }

    @Test
    void testDepositoArredondamentoPar() {
        Conta conta = new Conta(ModalidadeConta.CC, null);
        when(repository.findContaByNumeroConta(10)).thenReturn(Optional.of(conta));
        assertEquals(BigDecimal.ZERO, conta.getSaldo(), "O saldo inicial da conta deve ser igual a 0.00.");

        deposito.executar(10, BigDecimal.valueOf(10.265));
        assertEquals(BigDecimal.valueOf(10.26), conta.getSaldo(), "O saldo da conta deve ser alterado para 10.26");
    }
}
