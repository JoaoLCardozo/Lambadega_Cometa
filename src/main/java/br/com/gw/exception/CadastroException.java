package br.com.gw.exception;

public class CadastroException extends NegocioException {
    private static final long serialVersionUID = 1L;

    public CadastroException(String mensagem) {
        super(mensagem);
    }

    public CadastroException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
