package br.com.gw.exception;

public class DAOException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public DAOException(String mensagem) {
        super(mensagem);
    }

    public DAOException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }

    public DAOException(Throwable causa) {
        super(causa);
    }
}