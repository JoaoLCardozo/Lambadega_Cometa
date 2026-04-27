package br.com.gw.exception;

public class AuthenticationException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public AuthenticationException(String mensagem) {
        super(mensagem);
    }

    public AuthenticationException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }

    public AuthenticationException(Throwable causa) {
        super(causa);
    }
}