package br.com.gw.exception;

public class ValidationException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public ValidationException(String mensagem) {
        super(mensagem);
    }

    public ValidationException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }

    public ValidationException(Throwable causa) {
        super(causa);
    }
}