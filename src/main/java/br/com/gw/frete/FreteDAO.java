package br.com.gw.frete;

import br.com.gw.cliente.Cliente;
import br.com.gw.exception.NegocioException;
import br.com.gw.motorista.Motorista;
import br.com.gw.util.ConnectionFactory;
import br.com.gw.veiculo.Veiculo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.util.logging.Logger;

public class FreteDAO {
    private static final Logger logger = Logger.getLogger(FreteDAO.class.getName());

    public List<Frete> listar(String filtro, int pagina, int limite) throws NegocioException {
        List<Frete> lista = new ArrayList<>();
        int offset = (pagina - 1) * limite;
        String sql =
            "SELECT f.id, f.numero, f.municipio_origem, f.uf_origem, f.municipio_destino, " +
            "f.uf_destino, f.descricao_carga, f.peso_kg, f.volumes, f.valor_frete, " +
            "f.aliquota_icms, f.valor_icms, f.valor_total, f.status, f.data_emissao, " +
            "f.data_previsao_entrega, f.data_saida, f.data_entrega, " +
            "r.id r_id, r.nome_razao_social r_razao, " +
            "d.id d_id, d.nome_razao_social d_razao, " +
            "m.id m_id, m.nome m_nome, " +
            "v.id v_id, v.placa v_placa " +
            "FROM frete f " +
            "JOIN cliente r ON f.id_remetente    = r.id " +
            "JOIN cliente d ON f.id_destinatario = d.id " +
            "JOIN motorista m ON f.id_motorista  = m.id " +
            "JOIN veiculo v   ON f.id_veiculo    = v.id " +
            "WHERE f.numero ILIKE ? OR r.nome_razao_social ILIKE ? OR d.nome_razao_social ILIKE ? " +
            "ORDER BY f.id DESC LIMIT ? OFFSET ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String f = "%" + (filtro != null ? filtro : "") + "%";
            stmt.setString(1, f);
            stmt.setString(2, f);
            stmt.setString(3, f);
            stmt.setInt(4, limite);
            stmt.setInt(5, offset);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapearResumido(rs));
            }
        } catch (SQLException e) {
            logger.severe("Erro ao listar fretes: " + e.getMessage());
            throw new NegocioException("Erro ao listar fretes.", e);
        }
        return lista;
    }

    public int contarTotal(String filtro) throws NegocioException {
        String sql =
            "SELECT COUNT(*) FROM frete f " +
            "JOIN cliente r ON f.id_remetente    = r.id " +
            "JOIN cliente d ON f.id_destinatario = d.id " +
            "WHERE f.numero ILIKE ? OR r.nome_razao_social ILIKE ? OR d.nome_razao_social ILIKE ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String f = "%" + (filtro != null ? filtro : "") + "%";
            stmt.setString(1, f); stmt.setString(2, f); stmt.setString(3, f);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new NegocioException("Erro ao contar fretes.", e);
        }
        return 0;
    }

    public Frete buscarPorId(int id) throws NegocioException {
        String sql =
            "SELECT f.id, f.numero, f.municipio_origem, f.uf_origem, f.municipio_destino, " +
            "f.uf_destino, f.descricao_carga, f.peso_kg, f.volumes, f.valor_frete, " +
            "f.aliquota_icms, f.valor_icms, f.valor_total, f.status, f.data_emissao, " +
            "f.data_previsao_entrega, f.data_saida, f.data_entrega, " +
            "r.id r_id, r.nome_razao_social r_razao, r.documento r_cnpj, " +
            "d.id d_id, d.nome_razao_social d_razao, d.documento d_cnpj, " +
            "m.id m_id, m.nome m_nome, m.cpf m_cpf, " +
            "v.id v_id, v.placa v_placa, v.tipo v_tipo, v.capacidade_kg v_cap " +
            "FROM frete f " +
            "JOIN cliente r ON f.id_remetente    = r.id " +
            "JOIN cliente d ON f.id_destinatario = d.id " +
            "JOIN motorista m ON f.id_motorista  = m.id " +
            "JOIN veiculo v   ON f.id_veiculo    = v.id " +
            "WHERE f.id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapearCompleto(rs);
            }
        } catch (SQLException e) {
            throw new NegocioException("Erro ao buscar frete.", e);
        }
        return null;
    }

    public String buscarUltimoNumeroDoAno(int ano) throws NegocioException {
        String sql = "SELECT numero FROM frete WHERE numero LIKE ? ORDER BY id DESC LIMIT 1";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "FRT-" + ano + "-%");
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getString("numero");
            }
        } catch (SQLException e) {
            throw new NegocioException("Erro ao buscar último número de frete.", e);
        }
        return null;
    }

    public void salvar(Frete frete, Connection conn) throws NegocioException {
        String sql =
            "INSERT INTO frete (numero, id_remetente, id_destinatario, id_motorista, id_veiculo, " +
            "municipio_origem, uf_origem, municipio_destino, uf_destino, descricao_carga, " +
            "peso_kg, volumes, valor_frete, aliquota_icms, valor_icms, valor_total, " +
            "status, data_emissao, data_previsao_entrega) " +
            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, frete.getNumero());
            stmt.setInt(2, frete.getRemetente().getId());
            stmt.setInt(3, frete.getDestinatario().getId());
            stmt.setInt(4, frete.getMotorista().getId());
            stmt.setInt(5, frete.getVeiculo().getId());
            stmt.setString(6, frete.getMunicipioOrigem());
            stmt.setString(7, frete.getUfOrigem());
            stmt.setString(8, frete.getMunicipioDestino());
            stmt.setString(9, frete.getUfDestino());
            stmt.setString(10, frete.getDescricaoCarga());
            stmt.setBigDecimal(11, frete.getPesoKg());
            stmt.setInt(12, frete.getVolumes());
            stmt.setBigDecimal(13, frete.getValorFrete());
            stmt.setBigDecimal(14, frete.getAliquotaIcms());
            stmt.setBigDecimal(15, frete.getValorIcms());
            stmt.setBigDecimal(16, frete.getValorTotal());
            stmt.setString(17, Frete.Status.EMITIDO.name());
            stmt.setTimestamp(18, Timestamp.valueOf(frete.getDataEmissao()));
            stmt.setDate(19, Date.valueOf(frete.getDataPrevisaoEntrega()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Erro ao salvar frete: " + e.getMessage());
            throw new NegocioException("Erro ao salvar frete.", e);
        }
    }

    public void atualizarStatus(int idFrete, Frete.Status status,
                                 Timestamp dataSaida, Timestamp dataEntrega,
                                 Connection conn) throws NegocioException {
        String sql = "UPDATE frete SET status=?, data_saida=?, data_entrega=? WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setTimestamp(2, dataSaida);
            stmt.setTimestamp(3, dataEntrega);
            stmt.setInt(4, idFrete);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Erro ao atualizar status do frete: " + e.getMessage());
            throw new NegocioException("Erro ao atualizar status do frete.", e);
        }
    }

    public void atualizarStatusVeiculo(int idVeiculo, br.com.gw.veiculo.Veiculo.Status status,
                                        Connection conn) throws NegocioException {
        String sql = "UPDATE veiculo SET status=? WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setInt(2, idVeiculo);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new NegocioException("Erro ao atualizar status do veículo.", e);
        }
    }

    public void salvarOcorrencia(OcorrenciaFrete oc, Connection conn) throws NegocioException {
        String sql =
            "INSERT INTO ocorrencia_frete (id_frete, tipo, data_hora, municipio, uf, " +
            "descricao, nome_recebedor, documento_recebedor) VALUES (?,?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, oc.getIdFrete());
            stmt.setString(2, oc.getTipo().name());
            stmt.setTimestamp(3, Timestamp.valueOf(oc.getDataHora()));
            stmt.setString(4, oc.getMunicipio());
            stmt.setString(5, oc.getUf());
            stmt.setString(6, oc.getDescricao());
            stmt.setString(7, oc.getNomeRecebedor());
            stmt.setString(8, oc.getDocumentoRecebedor());
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Erro ao salvar ocorrencia: " + e.getMessage());
            throw new NegocioException("Erro ao salvar ocorrência.", e);
        }
    }

    public List<OcorrenciaFrete> listarOcorrencias(int idFrete) throws NegocioException {
        List<OcorrenciaFrete> lista = new ArrayList<>();
        String sql = "SELECT id, id_frete, tipo, data_hora, municipio, uf, descricao, " +
                     "nome_recebedor, documento_recebedor FROM ocorrencia_frete " +
                     "WHERE id_frete = ? ORDER BY data_hora";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idFrete);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapearOcorrencia(rs));
            }
        } catch (SQLException e) {
            throw new NegocioException("Erro ao listar ocorrências.", e);
        }
        return lista;
    }

    public LocalDateTime buscarDataHoraUltimaOcorrencia(int idFrete) throws NegocioException {
        String sql = "SELECT MAX(data_hora) FROM ocorrencia_frete WHERE id_frete = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idFrete);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && rs.getTimestamp(1) != null) {
                    return rs.getTimestamp(1).toLocalDateTime();
                }
            }
        } catch (SQLException e) {
            throw new NegocioException("Erro ao buscar última ocorrência.", e);
        }
        return null;
    }

    private Frete mapearResumido(ResultSet rs) throws SQLException {
        Frete f = new Frete();
        f.setId(rs.getInt("id"));
        f.setNumero(rs.getString("numero"));
        f.setMunicipioOrigem(rs.getString("municipio_origem"));
        f.setUfOrigem(rs.getString("uf_origem"));
        f.setMunicipioDestino(rs.getString("municipio_destino"));
        f.setUfDestino(rs.getString("uf_destino"));
        f.setStatus(Frete.Status.valueOf(rs.getString("status")));
        Timestamp de = rs.getTimestamp("data_emissao");
        if (de != null) f.setDataEmissao(de.toLocalDateTime());
        Date dprev = rs.getDate("data_previsao_entrega");
        if (dprev != null) f.setDataPrevisaoEntrega(dprev.toLocalDate());

        Cliente rem = new Cliente(); rem.setId(rs.getInt("r_id")); rem.setNomeRazaoSocial(rs.getString("r_razao"));
        Cliente dest = new Cliente(); dest.setId(rs.getInt("d_id")); dest.setNomeRazaoSocial(rs.getString("d_razao"));
        Motorista mot = new Motorista(); mot.setId(rs.getInt("m_id")); mot.setNome(rs.getString("m_nome"));
        Veiculo vei = new Veiculo(); vei.setId(rs.getInt("v_id")); vei.setPlaca(rs.getString("v_placa"));

        f.setRemetente(rem); f.setDestinatario(dest);
        f.setMotorista(mot); f.setVeiculo(vei);
        return f;
    }

    private Frete mapearCompleto(ResultSet rs) throws SQLException {
        Frete f = mapearResumido(rs);
        f.setDescricaoCarga(rs.getString("descricao_carga"));
        f.setPesoKg(rs.getBigDecimal("peso_kg"));
        f.setVolumes(rs.getInt("volumes"));
        f.setValorFrete(rs.getBigDecimal("valor_frete"));
        f.setAliquotaIcms(rs.getBigDecimal("aliquota_icms"));
        f.setValorIcms(rs.getBigDecimal("valor_icms"));
        f.setValorTotal(rs.getBigDecimal("valor_total"));
        Timestamp ds = rs.getTimestamp("data_saida");
        if (ds != null) f.setDataSaida(ds.toLocalDateTime());
        Timestamp dent = rs.getTimestamp("data_entrega");
        if (dent != null) f.setDataEntrega(dent.toLocalDateTime());

        // complementar objetos com campos extras
        f.getRemetente().setDocumento(rs.getString("r_cnpj"));
        f.getDestinatario().setDocumento(rs.getString("d_cnpj"));
        f.getMotorista().setCpf(rs.getString("m_cpf"));
        f.getVeiculo().setTipo(Veiculo.Tipo.valueOf(rs.getString("v_tipo")));
        f.getVeiculo().setCapacidadeKg(rs.getDouble("v_cap"));
        return f;
    }

    private OcorrenciaFrete mapearOcorrencia(ResultSet rs) throws SQLException {
        OcorrenciaFrete oc = new OcorrenciaFrete();
        oc.setId(rs.getInt("id"));
        oc.setIdFrete(rs.getInt("id_frete"));
        oc.setTipo(OcorrenciaFrete.Tipo.valueOf(rs.getString("tipo")));
        oc.setDataHora(rs.getTimestamp("data_hora").toLocalDateTime());
        oc.setMunicipio(rs.getString("municipio"));
        oc.setUf(rs.getString("uf"));
        oc.setDescricao(rs.getString("descricao"));
        oc.setNomeRecebedor(rs.getString("nome_recebedor"));
        oc.setDocumentoRecebedor(rs.getString("documento_recebedor"));
        return oc;
    }
}