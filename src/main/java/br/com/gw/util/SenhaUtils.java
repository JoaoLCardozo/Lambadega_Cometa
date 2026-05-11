package br.com.gw.util;

import br.com.gw.exception.NegocioException;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class SenhaUtils {
    private static final String ALGORITMO = "PBKDF2WithHmacSHA256";
    private static final String PREFIXO = "pbkdf2";
    private static final int ITERACOES = 65536;
    private static final int TAMANHO_CHAVE_BITS = 256;
    private static final int TAMANHO_SAL_BYTES = 16;
    private static final SecureRandom RANDOM = new SecureRandom();

    private SenhaUtils() {}

    public static String gerarHash(String senha) throws NegocioException {
        byte[] sal = new byte[TAMANHO_SAL_BYTES];
        RANDOM.nextBytes(sal);
        byte[] hash = calcularHash(senha, sal, ITERACOES, TAMANHO_CHAVE_BITS);

        return PREFIXO + "$" + ITERACOES + "$"
            + Base64.getEncoder().encodeToString(sal) + "$"
            + Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verificar(String senha, String senhaArmazenada) throws NegocioException {
        if (senha == null || senhaArmazenada == null) return false;

        if (!ehHash(senhaArmazenada)) {
            return senha.equals(senhaArmazenada);
        }

        String[] partes = senhaArmazenada.split("\\$");
        if (partes.length != 4) return false;

        int iteracoes = Integer.parseInt(partes[1]);
        byte[] sal = Base64.getDecoder().decode(partes[2]);
        byte[] hashArmazenado = Base64.getDecoder().decode(partes[3]);
        byte[] hashInformado = calcularHash(senha, sal, iteracoes, hashArmazenado.length * 8);

        return compararTempoConstante(hashArmazenado, hashInformado);
    }

    public static boolean ehHash(String senhaArmazenada) {
        return senhaArmazenada != null && senhaArmazenada.startsWith(PREFIXO + "$");
    }

    private static byte[] calcularHash(String senha, byte[] sal, int iteracoes, int tamanhoChaveBits)
            throws NegocioException {
        try {
            PBEKeySpec spec = new PBEKeySpec(senha.toCharArray(), sal, iteracoes, tamanhoChaveBits);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITMO);
            return factory.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new NegocioException("Erro ao proteger senha do usuário.", e);
        }
    }

    private static boolean compararTempoConstante(byte[] esperado, byte[] informado) {
        if (esperado.length != informado.length) return false;

        int diferenca = 0;
        for (int i = 0; i < esperado.length; i++) {
            diferenca |= esperado[i] ^ informado[i];
        }
        return diferenca == 0;
    }
}
