package br.com.gw.motorista;

import br.com.gw.exception.NegocioException;
import br.com.gw.util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

public class MotoristaDAO {
    private static final Logger logger = Logger.getLogger(MotoristaDAO.class.getName());

    public List<Motorista> listar(String filtro, int pagina, int limite) throws NegocioException {
        return listar(filtro, null, null, null, null, pagina, limite);
    }

    public List<Motorista> listar(String filtro, String cpf, String status,
                                  String tipoVinculo, String cnhCategoria,
                                  int pagina, int limite) throws NegocioException {
        List<Motorista> lista = new ArrayList<>();
        int offset = (pagina - 1) * limite;

        StringBuilder sql = new StringBuilder(
            "SELECT id, nome, cpf, data_nascimento, telefone, cnh_numero, " +
            "cnh_categoria, cnh_validade, tipo_vinculo, status " +
            "FROM motorista WHERE 1=1");
        List<String> parametros = new ArrayList<>();
        adicionarFiltros(sql, parametros, filtro, cpf, status, tipoVinculo, cnhCategoria);
        sql.append(" ORDER BY nome LIMIT ? OFFSET ?");

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int indice = preencherParametros(stmt, parametros);
            stmt.setInt(indice++, limite);
            stmt.setInt(indice, offset);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            logger.severe("Erro ao listar motoristas: " + e.getMessage());
            throw new NegocioException("Erro ao listar motoristas.", e);
        }
        return lista;
    }

    public int contarTotal(String filtro) throws NegocioException {
        return contarTotal(filtro, null, null, null, null);
    }

    public int contarTotal(String filtro, String cpf, String status,
                           String tipoVinculo, String cnhCategoria) throws NegocioException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM motorista WHERE 1=1");
        List<String> parametros = new ArrayList<>();
        adicionarFiltros(sql, parametros, filtro, cpf, status, tipoVinculo, cnhCategoria);

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            preencherParametros(stmt, parametros);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new NegocioException("Erro ao contar motoristas.", e);
        }
        return 0;
    }

    private void adicionarFiltros(StringBuilder sql, List<String> parametros, String filtro,
                                  String cpf, String status, String tipoVinculo,
                                  String cnhCategoria) {
        String filtroNome = normalizarTexto(filtro);
        if (filtroNome != null) {
            sql.append(" AND nome ILIKE ?");
            parametros.add("%" + filtroNome + "%");
        }

        String filtroCpf = somenteDigitos(cpf);
        if (filtroCpf != null && !filtroCpf.isEmpty()) {
            sql.append(" AND cpf LIKE ?");
            parametros.add("%" + filtroCpf + "%");
        }

        String filtroStatus = normalizarEnum(status);
        if (filtroStatus != null) {
            sql.append(" AND status = ?");
            parametros.add(filtroStatus);
        }

        String filtroTipoVinculo = normalizarEnum(tipoVinculo);
        if (filtroTipoVinculo != null) {
            sql.append(" AND tipo_vinculo = ?");
            parametros.add(filtroTipoVinculo);
        }

        String filtroCnhCategoria = normalizarEnum(cnhCategoria);
        if (filtroCnhCategoria != null) {
            sql.append(" AND cnh_categoria = ?");
            parametros.add(filtroCnhCategoria);
        }
    }

    private int preencherParametros(PreparedStatement stmt, List<String> parametros) throws SQLException {
        int indice = 1;
        for (String parametro : parametros) {
            stmt.setString(indice++, parametro);
        }
        return indice;
    }

    private String normalizarTexto(String valor) {
        if (valor == null) return null;
        String texto = valor.trim();
        return texto.isEmpty() ? null : texto;
    }

    private String normalizarEnum(String valor) {
        String texto = normalizarTexto(valor);
        return texto != null ? texto.toUpperCase(Locale.ROOT) : null;
    }

    private String somenteDigitos(String valor) {
        return valor != null ? valor.replaceAll("[^0-9]", "") : null;
    }

    public Motorista buscarPorId(int id) throws NegocioException {
        String sql = "SELECT id, nome, cpf, data_nascimento, telefone, cnh_numero, " +
                     "cnh_categoria, cnh_validade, tipo_vinculo, status " +
                     "FROM motorista WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            throw new NegocioException("Erro ao buscar motorista.", e);
        }
        return null;
    }

    public boolean existeCpf(String cpf, int idIgnorar) throws NegocioException {
        String sql = "SELECT COUNT(*) FROM motorista WHERE cpf = ? AND id <> ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            stmt.setInt(2, idIgnorar);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new NegocioException("Erro ao verificar CPF.", e);
        }
        return false;
    }

    public boolean existeCnhNumero(String cnhNumero, int idIgnorar) throws NegocioException {
        String sql = "SELECT COUNT(*) FROM motorista WHERE cnh_numero = ? AND id <> ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cnhNumero);
            stmt.setInt(2, idIgnorar);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new NegocioException("Erro ao verificar CNH.", e);
        }
        return false;
    }

    public boolean possuiFretesAtivos(int id) throws NegocioException {
        String sql = "SELECT COUNT(*) FROM frete WHERE id_motorista = ? " +
                     "AND status IN ('EMITIDO','SAIDA_CONFIRMADA','EM_TRANSITO')";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new NegocioException("Erro ao verificar fretes do motorista.", e);
        }
        return false;
    }

    public void salvar(Motorista m) throws NegocioException {
        String sql = "INSERT INTO motorista (nome, cpf, data_nascimento, telefone, cnh_numero, " +
                     "cnh_categoria, cnh_validade, tipo_vinculo, status) VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            preencherStatement(stmt, m);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Erro ao salvar motorista: " + e.getMessage());
            throw new NegocioException("Erro ao salvar motorista.", e);
        }
    }

    public void atualizar(Motorista m) throws NegocioException {
        String sql = "UPDATE motorista SET nome=?, cpf=?, data_nascimento=?, telefone=?, " +
                     "cnh_numero=?, cnh_categoria=?, cnh_validade=?, tipo_vinculo=?, status=? " +
                     "WHERE id=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            preencherStatement(stmt, m);
            stmt.setInt(10, m.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Erro ao atualizar motorista: " + e.getMessage());
            throw new NegocioException("Erro ao atualizar motorista.", e);
        }
    }

    public void excluir(int id) throws NegocioException {
        String sql = "DELETE FROM motorista WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Erro ao excluir motorista: " + e.getMessage());
            throw new NegocioException("Erro ao excluir motorista.", e);
        }
    }

    private void preencherStatement(PreparedStatement stmt, Motorista m) throws SQLException {
        stmt.setString(1, m.getNome());
        stmt.setString(2, m.getCpf());
        stmt.setDate(3, m.getDataNascimento() != null ? Date.valueOf(m.getDataNascimento()) : null);
        stmt.setString(4, m.getTelefone());
        stmt.setString(5, m.getCnhNumero());
        stmt.setString(6, m.getCnhCategoria() != null ? m.getCnhCategoria().name() : null);
        stmt.setDate(7, m.getCnhValidade() != null ? Date.valueOf(m.getCnhValidade()) : null);
        stmt.setString(8, m.getTipoVinculo() != null ? m.getTipoVinculo().name() : null);
        stmt.setString(9, m.getStatus() != null ? m.getStatus().name() : "ATIVO");
    }

    private Motorista mapear(ResultSet rs) throws SQLException {
        Motorista m = new Motorista();
        m.setId(rs.getInt("id"));
        m.setNome(rs.getString("nome"));
        m.setCpf(rs.getString("cpf"));
        Date dn = rs.getDate("data_nascimento");
        if (dn != null) m.setDataNascimento(dn.toLocalDate());
        m.setTelefone(rs.getString("telefone"));
        m.setCnhNumero(rs.getString("cnh_numero"));
        m.setCnhCategoria(Motorista.CnhCategoria.valueOf(rs.getString("cnh_categoria")));
        m.setCnhValidade(rs.getDate("cnh_validade").toLocalDate());
        m.setTipoVinculo(Motorista.TipoVinculo.valueOf(rs.getString("tipo_vinculo")));
        m.setStatus(Motorista.Status.valueOf(rs.getString("status")));
        return m;
    }
}
