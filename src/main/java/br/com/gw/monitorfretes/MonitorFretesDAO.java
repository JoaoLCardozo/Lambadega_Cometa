package br.com.gw.monitorfretes;

import br.com.gw.exception.NegocioException;
import br.com.gw.util.ConnectionFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class MonitorFretesDAO {
    private static final Logger logger = Logger.getLogger(MonitorFretesDAO.class.getName());
    private static final String STATUS_FRETES_ATIVOS = "('EMITIDO','SAIDA_CONFIRMADA','EM_TRANSITO')";

    public MonitorFretesResumo buscarResumo() throws NegocioException {
        MonitorFretesResumo resumo = new MonitorFretesResumo();

        try (Connection conn = ConnectionFactory.getConnection()) {
            resumo.setFretesAbertos(contar(conn,
                "SELECT COUNT(*) FROM frete WHERE status IN " + STATUS_FRETES_ATIVOS));
            resumo.setFretesAtrasados(contar(conn,
                "SELECT COUNT(*) FROM frete WHERE status IN " + STATUS_FRETES_ATIVOS +
                    " AND data_previsao_entrega < CURRENT_DATE"));
            resumo.setEntregasHoje(contar(conn,
                "SELECT COUNT(*) FROM frete WHERE status IN " + STATUS_FRETES_ATIVOS +
                    " AND data_previsao_entrega = CURRENT_DATE"));
            resumo.setVeiculosDisponiveis(contar(conn,
                "SELECT COUNT(*) FROM veiculo WHERE status = 'DISPONIVEL'"));
            resumo.setMotoristasCnhVencida(contar(conn,
                "SELECT COUNT(*) FROM motorista WHERE status = 'ATIVO' AND cnh_validade < CURRENT_DATE"));
            resumo.setClientesAtivos(contar(conn,
                "SELECT COUNT(*) FROM cliente WHERE status = 'ATIVO'"));
            resumo.setValorFretesMes(somar(conn,
                "SELECT COALESCE(SUM(valor_total), 0) FROM frete " +
                    "WHERE status <> 'CANCELADO' " +
                    "AND data_emissao >= date_trunc('month', CURRENT_DATE) " +
                    "AND data_emissao < date_trunc('month', CURRENT_DATE) + INTERVAL '1 month'"));
            resumo.setStatusFretes(listarStatusFretes(conn));
            resumo.setFretesCriticos(listarFretesCriticos(conn));
            resumo.setRankingMotoristas(listarRankingMotoristas(conn));
        } catch (SQLException e) {
            logger.severe("Erro ao carregar monitor de fretes: " + e.getMessage());
            throw new NegocioException("Erro ao carregar monitor de fretes.", e);
        }

        return resumo;
    }

    private int contar(Connection conn, String sql) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private BigDecimal somar(Connection conn, String sql) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getBigDecimal(1) : BigDecimal.ZERO;
        }
    }

    private List<MonitorFretesIndicadorStatus> listarStatusFretes(Connection conn) throws SQLException {
        List<MonitorFretesIndicadorStatus> lista = new ArrayList<>();
        String sql = "SELECT status, COUNT(*) total FROM frete GROUP BY status ORDER BY total DESC, status";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                MonitorFretesIndicadorStatus item = new MonitorFretesIndicadorStatus();
                item.setStatus(rs.getString("status"));
                item.setTotal(rs.getInt("total"));
                lista.add(item);
            }
        }

        return lista;
    }

    private List<MonitorFretesFreteCritico> listarFretesCriticos(Connection conn) throws SQLException {
        List<MonitorFretesFreteCritico> lista = new ArrayList<>();
        String sql =
            "SELECT f.id, f.numero, d.nome_razao_social destinatario, f.municipio_destino, " +
            "f.uf_destino, f.status, f.data_previsao_entrega, " +
            "GREATEST(CURRENT_DATE - f.data_previsao_entrega, 0) dias_atraso " +
            "FROM frete f " +
            "JOIN cliente d ON f.id_destinatario = d.id " +
            "WHERE f.status IN " + STATUS_FRETES_ATIVOS + " " +
            "AND f.data_previsao_entrega <= CURRENT_DATE " +
            "ORDER BY f.data_previsao_entrega ASC, f.id DESC LIMIT 8";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                MonitorFretesFreteCritico frete = new MonitorFretesFreteCritico();
                frete.setId(rs.getInt("id"));
                frete.setNumero(rs.getString("numero"));
                frete.setDestinatario(rs.getString("destinatario"));
                frete.setMunicipioDestino(rs.getString("municipio_destino"));
                frete.setUfDestino(rs.getString("uf_destino"));
                frete.setStatus(rs.getString("status"));
                Date previsao = rs.getDate("data_previsao_entrega");
                if (previsao != null) {
                    frete.setDataPrevisaoEntrega(previsao.toLocalDate());
                }
                frete.setDiasAtraso(rs.getInt("dias_atraso"));
                lista.add(frete);
            }
        }

        return lista;
    }

    private List<MonitorFretesRankingMotorista> listarRankingMotoristas(Connection conn) throws SQLException {
        List<MonitorFretesRankingMotorista> lista = new ArrayList<>();
        String sql =
            "SELECT m.nome, COUNT(*) entregas, COALESCE(SUM(f.valor_total), 0) valor_total " +
            "FROM frete f " +
            "JOIN motorista m ON f.id_motorista = m.id " +
            "WHERE f.status = 'ENTREGUE' " +
            "AND f.data_entrega >= date_trunc('month', CURRENT_DATE) " +
            "AND f.data_entrega < date_trunc('month', CURRENT_DATE) + INTERVAL '1 month' " +
            "GROUP BY m.nome " +
            "ORDER BY entregas DESC, valor_total DESC, m.nome LIMIT 3";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                MonitorFretesRankingMotorista item = new MonitorFretesRankingMotorista();
                item.setNome(rs.getString("nome"));
                item.setEntregas(rs.getInt("entregas"));
                item.setValorTotal(rs.getBigDecimal("valor_total"));
                lista.add(item);
            }
        }

        return lista;
    }
}
