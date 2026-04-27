package br.com.gw.filter;

import br.com.gw.usuario.Usuario;
import java.util.logging.Logger;

public class AutenticacaoFiltro {
    private static final Logger logger = Logger.getLogger(AutenticacaoFiltro.class.getName());

    private AutenticacaoFiltro() {}

    public static boolean validarAutenticacao(Usuario usuario) {
        if (usuario == null) {
            return false;
        }
        if (usuario.getId() == null || usuario.getId() <= 0) {
            logger.warning("ID de usuario invalido: " + usuario.getUsuario());
            return false;
        }
        if (usuario.getAtivo() == null || !usuario.getAtivo()) {
            logger.warning("Usuario inativo: " + usuario.getUsuario());
            return false;
        }
        return true;
    }

    public static void registrarAcesso(Usuario usuario, String caminho) {
        logger.info("ACESSO OK | usuario=" + usuario.getUsuario() + " | caminho=" + caminho);
    }

    public static void registrarNegacao(Usuario usuario, String caminho, String motivo) {
        String nome = (usuario != null) ? usuario.getUsuario() : "anonimo";
        logger.warning("ACESSO NEGADO | usuario=" + nome + " | caminho=" + caminho + " | motivo=" + motivo);
    }
}
