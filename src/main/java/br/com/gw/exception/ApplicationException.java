package br.com.gw.exception;

public class ApplicationException extends Exception {

    private static final long serialVersionUID = 1L;

    public ApplicationException(String mensagem) {
        super(mensagem);
    }

    public ApplicationException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }

    public ApplicationException(Throwable causa) {
        super(causa);
    }
}