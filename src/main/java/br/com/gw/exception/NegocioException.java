package br.com.gw.exception;

public class NegocioException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public NegocioException(String mensagem) {
        super(mensagem);
    }

    public NegocioException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }

    public NegocioException(Throwable causa) {
        super(causa);
    }
}