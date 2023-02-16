package tech.ada.banco.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Conta Origem não pode ser igual conta Destino.")
public class ContaOrigemIgualDestinoException extends RuntimeException{
    public ContaOrigemIgualDestinoException() {
        super("Conta Origem não pode ser igual conta Destino.");
    }
}
