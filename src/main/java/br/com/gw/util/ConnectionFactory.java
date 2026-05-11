package br.com.gw.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

public class ConnectionFactory {
    private static final Logger logger = Logger.getLogger(ConnectionFactory.class.getName());

    private static final String URL;
    private static final String USUARIO;
    private static final String SENHA;

    static {
        Properties props = carregarConfiguracoes();

        URL     = obterObrigatorio(props, "db.url", "DB_URL");
        USUARIO = obterObrigatorio(props, "db.usuario", "DB_USUARIO");
        SENHA   = obterObrigatorio(props, "db.senha", "DB_SENHA");
    }

    private static Properties carregarConfiguracoes() {
        Properties props = new Properties();

        try (InputStream is = ConnectionFactory.class
                .getClassLoader().getResourceAsStream("db.properties")) {
            if (is != null) {
                props.load(is);
                logger.info("Configuracoes de banco carregadas de db.properties");
            } else {
                logger.info("db.properties nao encontrado; tentando variaveis de ambiente");
            }
        } catch (IOException e) {
            logger.warning("Erro ao ler db.properties: " + e.getMessage());
        }

        return props;
    }

    private static String obterObrigatorio(Properties props, String chave, String variavelAmbiente) {
        String valorAmbiente = System.getenv(variavelAmbiente);
        if (!vazio(valorAmbiente)) {
            return valorAmbiente.trim();
        }

        String valorArquivo = props.getProperty(chave);
        if (!vazio(valorArquivo)) {
            return valorArquivo.trim();
        }

        throw new IllegalStateException(
            "Configuracao obrigatoria de banco ausente: informe "
            + variavelAmbiente + " ou " + chave + " em db.properties.");
    }

    private static boolean vazio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USUARIO, SENHA);
        } catch (SQLException e) {
            logger.severe("Erro ao obter conexao: " + e.getMessage());
            throw new RuntimeException("Erro ao obter conexão com o banco.", e);
        }
    }

    public static void rollback(Connection conn) {
        if (conn != null) {
            try { conn.rollback(); }
            catch (SQLException e) { logger.severe("Erro no rollback: " + e.getMessage()); }
        }
    }

    public static void fechar(Connection conn) {
        if (conn != null) {
            try { conn.close(); }
            catch (SQLException e) { logger.severe("Erro ao fechar conexao: " + e.getMessage()); }
        }
    }
}
