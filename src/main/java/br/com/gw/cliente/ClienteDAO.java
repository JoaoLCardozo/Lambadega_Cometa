package br.com.gw.cliente;

import br.com.gw.exception.NegocioException;
import br.com.gw.util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ClienteDAO {
    private static final Logger logger = Logger.getLogger(ClienteDAO.class.getName());

    public List<Cliente> listar(String filtro, int pagina, int limite) throws NegocioException {
        List<Cliente> lista = new ArrayList<>();
        int offset = (pagina - 1) * limite;

        String sql = "SELECT id, tipo_pessoa, nome_razao_social, nome_fantasia, documento, inscricao_estadual, " +
                     "logradouro, numero, complemento, bairro, municipio, uf, cep, " +
                     "telefone, email, status " +
                     "FROM cliente " +
                     "WHERE nome_razao_social ILIKE ? " +
                     "ORDER BY nome_razao_social " +
                     "LIMIT ? OFFSET ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + (filtro != null ? filtro : "") + "%");
            stmt.setInt(2, limite);
            stmt.setInt(3, offset);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            logger.severe("Erro ao listar clientes: " + e.getMessage());
            throw new NegocioException("Erro ao listar clientes.", e);
        }
        return lista;
    }

    public int contarTotal(String filtro) throws NegocioException {
        String sql = "SELECT COUNT(*) FROM cliente WHERE nome_razao_social ILIKE ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + (filtro != null ? filtro : "") + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.severe("Erro ao contar clientes: " + e.getMessage());
            throw new NegocioException("Erro ao contar clientes.", e);
        }
        return 0;
    }

    public Cliente buscarPorId(int id) throws NegocioException {
        String sql = "SELECT id, tipo_pessoa, nome_razao_social, nome_fantasia, documento, inscricao_estadual, " +
                     "logradouro, numero, complemento, bairro, municipio, uf, cep, " +
                     "telefone, email, status FROM cliente WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            logger.severe("Erro ao buscar cliente por id: " + e.getMessage());
            throw new NegocioException("Erro ao buscar cliente.", e);
        }
        return null;
    }

    public boolean existeDocumento(String documento, int idIgnorar) throws NegocioException {
        String sql = "SELECT COUNT(*) FROM cliente WHERE documento = ? AND id <> ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, documento);
            stmt.setInt(2, idIgnorar);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new NegocioException("Erro ao verificar documento.", e);
        }
        return false;
    }

    public boolean possuiFretes(int idCliente) throws NegocioException {
        String sql = "SELECT COUNT(*) FROM frete " +
                     "WHERE id_remetente = ? OR id_destinatario = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCliente);
            stmt.setInt(2, idCliente);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new NegocioException("Erro ao verificar fretes do cliente.", e);
        }
        return false;
    }

    public void salvar(Cliente c) throws NegocioException {
        String sql = "INSERT INTO cliente (tipo_pessoa, nome_razao_social, nome_fantasia, documento, inscricao_estadual, " +
                     "logradouro, numero, complemento, bairro, municipio, uf, cep, " +
                     "telefone, email, status) " +
                     "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            preencherStatement(stmt, c);
            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.severe("Erro ao salvar cliente: " + e.getMessage());
            throw new NegocioException("Erro ao salvar cliente.", e);
        }
    }

    public void atualizar(Cliente c) throws NegocioException {
        String sql = "UPDATE cliente SET tipo_pessoa=?, nome_razao_social=?, nome_fantasia=?, documento=?, " +
                     "inscricao_estadual=?, logradouro=?, numero=?, complemento=?, " +
                     "bairro=?, municipio=?, uf=?, cep=?, telefone=?, email=?, status=? " +
                     "WHERE id=?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            preencherStatement(stmt, c);
            stmt.setInt(16, c.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.severe("Erro ao atualizar cliente: " + e.getMessage());
            throw new NegocioException("Erro ao atualizar cliente.", e);
        }
    }

    public void excluir(int id) throws NegocioException {
        String sql = "DELETE FROM cliente WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.severe("Erro ao excluir cliente: " + e.getMessage());
            throw new NegocioException("Erro ao excluir cliente.", e);
        }
    }

    private void preencherStatement(PreparedStatement stmt, Cliente c) throws SQLException {
        stmt.setString(1, c.getTipoPessoa() != null ? c.getTipoPessoa().name() : null);
        stmt.setString(2, c.getNomeRazaoSocial());
        stmt.setString(3, c.getNomeFantasia());
        stmt.setString(4, c.getDocumento());
        stmt.setString(5, c.getInscricaoEstadual());
        stmt.setString(6, c.getLogradouro());
        stmt.setString(7, c.getNumero());
        stmt.setString(8, c.getComplemento());
        stmt.setString(9, c.getBairro());
        stmt.setString(10, c.getMunicipio());
        stmt.setString(11, c.getUf());
        stmt.setString(12, c.getCep());
        stmt.setString(13, c.getTelefone());
        stmt.setString(14, c.getEmail());
        stmt.setString(15, c.getStatus() != null ? c.getStatus().name() : "ATIVO");
    }

    private Cliente mapear(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setId(rs.getInt("id"));
        String tipoPessoa = rs.getString("tipo_pessoa");
        if (tipoPessoa != null) {
            c.setTipoPessoa(Cliente.TipoPessoa.valueOf(tipoPessoa));
        }
        c.setNomeRazaoSocial(rs.getString("nome_razao_social"));
        c.setNomeFantasia(rs.getString("nome_fantasia"));
        c.setDocumento(rs.getString("documento"));
        c.setInscricaoEstadual(rs.getString("inscricao_estadual"));
        c.setLogradouro(rs.getString("logradouro"));
        c.setNumero(rs.getString("numero"));
        c.setComplemento(rs.getString("complemento"));
        c.setBairro(rs.getString("bairro"));
        c.setMunicipio(rs.getString("municipio"));
        c.setUf(rs.getString("uf"));
        c.setCep(rs.getString("cep"));
        c.setTelefone(rs.getString("telefone"));
        c.setEmail(rs.getString("email"));
        c.setStatus(Cliente.Status.valueOf(rs.getString("status")));
        return c;
    }
}