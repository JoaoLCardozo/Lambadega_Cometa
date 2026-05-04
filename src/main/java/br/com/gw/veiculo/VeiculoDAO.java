package br.com.gw.veiculo;

import br.com.gw.exception.NegocioException;
import br.com.gw.util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class VeiculoDAO {
    private static final Logger logger = Logger.getLogger(VeiculoDAO.class.getName());

    public List<Veiculo> listar(String filtro, int pagina, int limite) throws NegocioException {
        List<Veiculo> lista = new ArrayList<>();
        int offset = (pagina - 1) * limite;
        String sql = "SELECT id, placa, rntrc, ano_fabricacao, tipo, tara_kg, " +
                     "capacidade_kg, volume_m3, status FROM veiculo " +
                     "WHERE placa ILIKE ? ORDER BY placa LIMIT ? OFFSET ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + (filtro != null ? filtro : "") + "%");
            stmt.setInt(2, limite);
            stmt.setInt(3, offset);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            logger.severe("Erro ao listar veiculos: " + e.getMessage());
            throw new NegocioException("Erro ao listar veículos.", e);
        }
        return lista;
    }

    public int contarTotal(String filtro) throws NegocioException {
        String sql = "SELECT COUNT(*) FROM veiculo WHERE placa ILIKE ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + (filtro != null ? filtro : "") + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new NegocioException("Erro ao contar veículos.", e);
        }
        return 0;
    }

    public Veiculo buscarPorId(int id) throws NegocioException {
        String sql = "SELECT id, placa, rntrc, ano_fabricacao, tipo, tara_kg, " +
                     "capacidade_kg, volume_m3, status FROM veiculo WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            throw new NegocioException("Erro ao buscar veículo.", e);
        }
        return null;
    }

    public boolean existePlaca(String placa, int idIgnorar) throws NegocioException {
        String sql = "SELECT COUNT(*) FROM veiculo WHERE placa = ? AND id <> ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, placa);
            stmt.setInt(2, idIgnorar);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new NegocioException("Erro ao verificar placa.", e);
        }
        return false;
    }

    public boolean estaEmTransito(int id) throws NegocioException {
        String sql = "SELECT COUNT(*) FROM frete " +
                     "WHERE id_veiculo = ? AND status IN ('EMITIDO','SAIDA_CONFIRMADA','EM_TRANSITO')";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new NegocioException("Erro ao verificar status do veículo.", e);
        }
        return false;
    }

    public void salvar(Veiculo v) throws NegocioException {
        String sql = "INSERT INTO veiculo (placa, rntrc, ano_fabricacao, tipo, tara_kg, " +
                     "capacidade_kg, volume_m3, status) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            preencherStatement(stmt, v);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Erro ao salvar veiculo: " + e.getMessage());
            throw new NegocioException("Erro ao salvar veículo.", e);
        }
    }

    public void atualizar(Veiculo v) throws NegocioException {
        String sql = "UPDATE veiculo SET placa=?, rntrc=?, ano_fabricacao=?, tipo=?, " +
                     "tara_kg=?, capacidade_kg=?, volume_m3=?, status=? WHERE id=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            preencherStatement(stmt, v);
            stmt.setInt(9, v.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Erro ao atualizar veiculo: " + e.getMessage());
            throw new NegocioException("Erro ao atualizar veículo.", e);
        }
    }

    public void excluir(int id) throws NegocioException {
        String sql = "DELETE FROM veiculo WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Erro ao excluir veiculo: " + e.getMessage());
            throw new NegocioException("Erro ao excluir veículo.", e);
        }
    }

    private void preencherStatement(PreparedStatement stmt, Veiculo v) throws SQLException {
        stmt.setString(1, v.getPlaca());
        stmt.setString(2, v.getRntrc());
        stmt.setInt(3, v.getAnoFabricacao());
        stmt.setString(4, v.getTipo() != null ? v.getTipo().name() : null);
        stmt.setDouble(5, v.getTaraKg());
        stmt.setDouble(6, v.getCapacidadeKg());
        stmt.setDouble(7, v.getVolumeM3());
        stmt.setString(8, v.getStatus() != null ? v.getStatus().name() : "DISPONIVEL");
    }

    private Veiculo mapear(ResultSet rs) throws SQLException {
        Veiculo v = new Veiculo();
        v.setId(rs.getInt("id"));
        v.setPlaca(rs.getString("placa"));
        v.setRntrc(rs.getString("rntrc"));
        v.setAnoFabricacao(rs.getInt("ano_fabricacao"));
        v.setTipo(Veiculo.Tipo.valueOf(rs.getString("tipo")));
        v.setTaraKg(rs.getDouble("tara_kg"));
        v.setCapacidadeKg(rs.getDouble("capacidade_kg"));
        v.setVolumeM3(rs.getDouble("volume_m3"));
        v.setStatus(Veiculo.Status.valueOf(rs.getString("status")));
        return v;
    }
}
