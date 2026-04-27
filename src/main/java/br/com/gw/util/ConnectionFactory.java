package br.com.gw.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public class ConnectionFactory {
    private static final Logger logger = Logger.getLogger(ConnectionFactory.class.getName());
    private static final String URL     = "jdbc:postgresql://localhost:5432/LambadegaCometa";
    private static final String USUARIO = "postgres";
    private static final String SENHA   = "1234";

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