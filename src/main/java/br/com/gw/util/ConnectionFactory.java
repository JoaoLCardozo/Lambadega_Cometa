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
        Properties props = new Properties();
        InputStream is = ConnectionFactory.class
            .getClassLoader().getResourceAsStream("db.properties");

        if (is != null) {
            try {
                props.load(is);
                logger.info("Configuracoes de banco carregadas de db.properties");
            } catch (IOException e) {
                logger.warning("Erro ao ler db.properties: " + e.getMessage());
            }
        } else {
            logger.warning("db.properties nao encontrado — usando valores padrao de desenvolvimento");
        }

        URL     = props.getProperty("db.url",     "jdbc:postgresql://localhost:5432/LambadegaCometa");
        USUARIO = props.getProperty("db.usuario", "postgres");
        SENHA   = props.getProperty("db.senha",   "1234");
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