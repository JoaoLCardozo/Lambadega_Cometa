package br.com.gw.util;

import br.com.gw.exception.NegocioException;

public final class SegurancaUtils {
    private SegurancaUtils() {}

    public static String normalizarTexto(String valor) {
        if (valor == null) return null;
        String texto = valor.trim();
        return texto.isEmpty() ? null : texto;
    }

    public static String normalizarTextoSemHtml(String valor, String nomeCampo) throws NegocioException {
        String texto = normalizarTexto(valor);
        validarSemHtml(texto, nomeCampo);
        return texto;
    }

    public static void validarSemHtml(String valor, String nomeCampo) throws NegocioException {
        if (contemHtml(valor)) {
            throw new NegocioException("O campo " + nomeCampo + " não permite HTML ou scripts.");
        }
    }

    public static boolean contemHtml(String valor) {
        return valor != null && (valor.contains("<") || valor.contains(">"));
    }
}
