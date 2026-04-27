package br.com.gw.exception;

public class FreteException extends NegocioException {
    private static final long serialVersionUID = 1L;

    public FreteException(String mensagem) {
        super(mensagem);
    }

    public FreteException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
