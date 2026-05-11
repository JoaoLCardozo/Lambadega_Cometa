package br.com.gw.usuario;

import br.com.gw.util.ConnectionFactory;
import br.com.gw.exception.DAOException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe de Acesso a Dados (DAO) para a entidade Usuario.
 * Responsável por todas as operações de banco de dados.
 */
public class UsuarioDAO {

    /**
     * Busca um usuário ativo pelo login para autenticação.
     * @param usuario nome do usuário
     * @return objeto Usuario ativo se encontrado, null caso contrário
     * @throws DAOException em caso de erro na operação
     */
    public Usuario autenticar(String usuario) throws DAOException {
        String sql = "SELECT id, nome, email, usuario, senha, ativo, data_criacao, data_atualizacao " +
                     "FROM usuario WHERE usuario = ? AND ativo = true";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, usuario);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extrairUsuario(rs);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Erro ao autenticar usuário: " + usuario, e);
        }
        
        return null;
    }

    /**
     * Busca um usuário pelo ID.
     * @param id ID do usuário
     * @return objeto Usuario se encontrado, null caso contrário
     * @throws DAOException em caso de erro na operação
     */
    public Usuario buscarPorId(Integer id) throws DAOException {
        String sql = "SELECT id, nome, email, usuario, senha, ativo, data_criacao, data_atualizacao " +
                     "FROM usuario WHERE id = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extrairUsuario(rs);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Erro ao buscar usuário com ID: " + id, e);
        }
        
        return null;
    }

    /**
     * Busca um usuário pelo nome de usuário.
     * @param usuario nome do usuário
     * @return objeto Usuario se encontrado, null caso contrário
     * @throws DAOException em caso de erro na operação
     */
    public Usuario buscarPorUsuario(String usuario) throws DAOException {
        String sql = "SELECT id, nome, email, usuario, senha, ativo, data_criacao, data_atualizacao " +
                     "FROM usuario WHERE usuario = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, usuario);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extrairUsuario(rs);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Erro ao buscar usuário: " + usuario, e);
        }
        
        return null;
    }

    public Usuario buscarPorEmailAtivo(String email) throws DAOException {
        String sql = "SELECT id, nome, email, usuario, senha, ativo, data_criacao, data_atualizacao " +
                     "FROM usuario WHERE LOWER(email) = LOWER(?) AND ativo = true";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extrairUsuario(rs);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Erro ao buscar usuário por e-mail", e);
        }

        return null;
    }

    /**
     * Lista todos os usuários.
     * @return lista de usuários
     * @throws DAOException em caso de erro na operação
     */
    public List<Usuario> listarTodos() throws DAOException {
        String sql = "SELECT id, nome, email, usuario, senha, ativo, data_criacao, data_atualizacao " +
                     "FROM usuario ORDER BY nome";
        
        List<Usuario> usuarios = new ArrayList<>();
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                usuarios.add(extrairUsuario(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Erro ao listar usuários", e);
        }
        
        return usuarios;
    }

    /**
     * Insere um novo usuário no banco.
     * @param usuario objeto Usuario a ser inserido
     * @throws DAOException em caso de erro na operação
     */
    public void inserir(Usuario usuario) throws DAOException {
        String sql = "INSERT INTO usuario (nome, email, usuario, senha, ativo) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getEmail());
            ps.setString(3, usuario.getUsuario());
            ps.setString(4, usuario.getSenha());
            ps.setBoolean(5, usuario.getAtivo() != null ? usuario.getAtivo() : true);
            
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erro ao inserir usuário: " + usuario.getUsuario(), e);
        }
    }

    /**
     * Atualiza um usuário existente.
     * @param usuario objeto Usuario a ser atualizado
     * @throws DAOException em caso de erro na operação
     */
    public void atualizar(Usuario usuario) throws DAOException {
        String sql = "UPDATE usuario SET nome = ?, email = ?, senha = ?, ativo = ? " +
                     "WHERE id = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getEmail());
            ps.setString(3, usuario.getSenha());
            ps.setBoolean(4, usuario.getAtivo() != null ? usuario.getAtivo() : true);
            ps.setInt(5, usuario.getId());
            
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erro ao atualizar usuário: " + usuario.getId(), e);
        }
    }

    public void atualizarSenha(Integer id, String senha) throws DAOException {
        String sql = "UPDATE usuario SET senha = ?, data_atualizacao = NOW() WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, senha);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erro ao atualizar senha do usuário: " + id, e);
        }
    }

    public void salvarCodigoRecuperacao(Integer idUsuario, String codigoHash,
                                        LocalDateTime dataExpiracao) throws DAOException {
        String invalidarSql = "UPDATE recuperacao_senha SET usado = true " +
                              "WHERE id_usuario = ? AND usado = false";
        String inserirSql = "INSERT INTO recuperacao_senha " +
                            "(id_usuario, codigo_hash, data_expiracao) VALUES (?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection()) {
            try (PreparedStatement invalidar = conn.prepareStatement(invalidarSql)) {
                invalidar.setInt(1, idUsuario);
                invalidar.executeUpdate();
            }

            try (PreparedStatement inserir = conn.prepareStatement(inserirSql)) {
                inserir.setInt(1, idUsuario);
                inserir.setString(2, codigoHash);
                inserir.setTimestamp(3, Timestamp.valueOf(dataExpiracao));
                inserir.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DAOException("Erro ao salvar código de recuperação", e);
        }
    }

    public Integer buscarUsuarioPorCodigoRecuperacao(String email, String codigoHash)
            throws DAOException {
        String sql = "SELECT r.id_usuario " +
                     "FROM recuperacao_senha r " +
                     "JOIN usuario u ON u.id = r.id_usuario " +
                     "WHERE LOWER(u.email) = LOWER(?) " +
                     "AND r.codigo_hash = ? " +
                     "AND r.usado = false " +
                     "AND r.data_expiracao >= NOW() " +
                     "AND u.ativo = true " +
                     "ORDER BY r.data_criacao DESC LIMIT 1";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, codigoHash);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_usuario");
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Erro ao validar código de recuperação", e);
        }

        return null;
    }

    public void marcarCodigosRecuperacaoComoUsados(Integer idUsuario) throws DAOException {
        String sql = "UPDATE recuperacao_senha SET usado = true " +
                     "WHERE id_usuario = ? AND usado = false";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erro ao invalidar códigos de recuperação", e);
        }
    }

    /**
     * Deleta um usuário do banco.
     * @param id ID do usuário a ser deletado
     * @throws DAOException em caso de erro na operação
     */
    public void deletar(Integer id) throws DAOException {
        String sql = "DELETE FROM usuario WHERE id = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erro ao deletar usuário com ID: " + id, e);
        }
    }

    /**
     * Método auxiliar para extrair dados do ResultSet e criar um objeto Usuario.
     */
    private Usuario extrairUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("id"));
        usuario.setNome(rs.getString("nome"));
        usuario.setEmail(rs.getString("email"));
        usuario.setUsuario(rs.getString("usuario"));
        usuario.setSenha(rs.getString("senha"));
        usuario.setAtivo(rs.getBoolean("ativo"));
        
        Timestamp dataCriacao = rs.getTimestamp("data_criacao");
        if (dataCriacao != null) {
            usuario.setDataCriacao(dataCriacao.toLocalDateTime());
        }
        
        Timestamp dataAtualizacao = rs.getTimestamp("data_atualizacao");
        if (dataAtualizacao != null) {
            usuario.setDataAtualizacao(dataAtualizacao.toLocalDateTime());
        }
        
        return usuario;
    }
}
