package br.com.gw.frete;

import br.com.gw.cliente.ClienteDAO;
import br.com.gw.exception.FreteException;
import br.com.gw.exception.NegocioException;
import br.com.gw.motorista.Motorista;
import br.com.gw.motorista.MotoristaDAO;
import br.com.gw.util.ConnectionFactory;
import br.com.gw.veiculo.Veiculo;
import br.com.gw.veiculo.VeiculoDAO;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

public class FreteBO {
    private static final Logger logger = Logger.getLogger(FreteBO.class.getName());

    private final FreteDAO   freteDAO   = new FreteDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final MotoristaDAO motoristaDAO = new MotoristaDAO();
    private final VeiculoDAO veiculoDAO = new VeiculoDAO();

    public List<Frete> listar(String filtro, int pagina, int limite) throws NegocioException {
        if (pagina < 1) pagina = 1;
        if (limite < 1) limite = 10;
        return freteDAO.listar(filtro, pagina, limite);
    }

    public int contarPaginas(String filtro, int limite) throws NegocioException {
        return (int) Math.ceil((double) freteDAO.contarTotal(filtro) / limite);
    }

    public Frete buscarPorId(int id) throws NegocioException {
        Frete f = freteDAO.buscarPorId(id);
        if (f == null) throw new FreteException("Frete não encontrado.");
        f.setOcorrencias(freteDAO.listarOcorrencias(id));
        return f;
    }

    /**
     * Gera o próximo número no formato FRT-AAAA-NNNNN — regra de negócio no BO.
     */
    public String gerarNumeroFrete() throws NegocioException {
        int ano = LocalDate.now().getYear();
        String ultimo = freteDAO.buscarUltimoNumeroDoAno(ano);
        int sequencial = 1;
        if (ultimo != null) {
            String[] partes = ultimo.split("-");
            sequencial = Integer.parseInt(partes[2]) + 1;
        }
        return String.format("FRT-%d-%05d", ano, sequencial);
    }

    public void emitir(Frete frete) throws NegocioException {
        validarEmissao(frete);

        frete.setNumero(gerarNumeroFrete());
        frete.setStatus(Frete.Status.EMITIDO);
        frete.setDataEmissao(LocalDateTime.now());

        // Calcula valor total
        if (frete.getValorFrete() != null && frete.getAliquotaIcms() != null) {
            BigDecimal icms = frete.getValorFrete()
                .multiply(frete.getAliquotaIcms())
                .divide(BigDecimal.valueOf(100));
            frete.setValorIcms(icms);
            frete.setValorTotal(frete.getValorFrete().add(icms));
        }

        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false);
            freteDAO.salvar(frete, conn);
            conn.commit();
            logger.info("Frete emitido: " + frete.getNumero());
        } catch (Exception e) {
            ConnectionFactory.rollback(conn);
            logger.severe("Erro ao emitir frete: " + e.getMessage());
            throw new FreteException("Erro ao emitir frete: " + e.getMessage(), e);
        } finally {
            ConnectionFactory.fechar(conn);
        }
    }

    public void confirmarSaida(int idFrete) throws NegocioException {
        Frete frete = buscarPorId(idFrete);

        if (frete.getStatus() != Frete.Status.EMITIDO) {
            throw new FreteException("Somente fretes com status EMITIDO podem ter saída confirmada.");
        }

        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false);

            freteDAO.atualizarStatus(idFrete, Frete.Status.SAIDA_CONFIRMADA,
                new java.sql.Timestamp(System.currentTimeMillis()), null, conn);
            freteDAO.atualizarStatusVeiculo(frete.getVeiculo().getId(), "EM_VIAGEM", conn);

            OcorrenciaFrete oc = new OcorrenciaFrete();
            oc.setIdFrete(idFrete);
            oc.setTipo(OcorrenciaFrete.Tipo.SAIDA_DO_PATIO);
            oc.setDataHora(LocalDateTime.now());
            oc.setMunicipio(frete.getMunicipioOrigem());
            oc.setUf(frete.getUfOrigem());
            oc.setDescricao("Saída do pátio confirmada.");
            freteDAO.salvarOcorrencia(oc, conn);

            conn.commit();
            logger.info("Saida confirmada: " + frete.getNumero());
        } catch (NegocioException e) {
            ConnectionFactory.rollback(conn);
            throw e;
        } catch (Exception e) {
            ConnectionFactory.rollback(conn);
            throw new FreteException("Erro ao confirmar saída: " + e.getMessage(), e);
        } finally {
            ConnectionFactory.fechar(conn);
        }
    }

    public void registrarEmTransito(int idFrete, OcorrenciaFrete ocorrencia) throws NegocioException {
        Frete frete = buscarPorId(idFrete);

        if (frete.getStatus() != Frete.Status.SAIDA_CONFIRMADA) {
            throw new FreteException("O frete precisa estar com SAÍDA CONFIRMADA para registrar em trânsito.");
        }
        validarOcorrencia(ocorrencia, frete);

        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false);

            freteDAO.atualizarStatus(idFrete, Frete.Status.EM_TRANSITO,
                new java.sql.Timestamp(System.currentTimeMillis()), null, conn);
            ocorrencia.setIdFrete(idFrete);
            ocorrencia.setTipo(OcorrenciaFrete.Tipo.EM_ROTA);
            freteDAO.salvarOcorrencia(ocorrencia, conn);

            conn.commit();
        } catch (NegocioException e) {
            ConnectionFactory.rollback(conn);
            throw e;
        } catch (Exception e) {
            ConnectionFactory.rollback(conn);
            throw new FreteException("Erro ao registrar em trânsito: " + e.getMessage(), e);
        } finally {
            ConnectionFactory.fechar(conn);
        }
    }

    public void registrarEntrega(int idFrete, OcorrenciaFrete ocorrencia) throws NegocioException {
        Frete frete = buscarPorId(idFrete);

        if (frete.getStatus() != Frete.Status.EM_TRANSITO) {
            throw new FreteException("Somente fretes EM TRÂNSITO podem ser entregues.");
        }
        if (ocorrencia.getNomeRecebedor() == null || ocorrencia.getNomeRecebedor().trim().isEmpty()) {
            throw new FreteException("O nome do recebedor é obrigatório para registrar entrega.");
        }
        if (ocorrencia.getDocumentoRecebedor() == null || ocorrencia.getDocumentoRecebedor().trim().isEmpty()) {
            throw new FreteException("O documento do recebedor é obrigatório para registrar entrega.");
        }
        validarOcorrencia(ocorrencia, frete);

        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false);

            java.sql.Timestamp agora = new java.sql.Timestamp(System.currentTimeMillis());
            freteDAO.atualizarStatus(idFrete, Frete.Status.ENTREGUE,
                new java.sql.Timestamp(frete.getDataSaida() != null
                    ? java.sql.Timestamp.valueOf(frete.getDataSaida()).getTime()
                    : System.currentTimeMillis()), agora, conn);
            freteDAO.atualizarStatusVeiculo(frete.getVeiculo().getId(), "DISPONIVEL", conn);

            ocorrencia.setIdFrete(idFrete);
            ocorrencia.setTipo(OcorrenciaFrete.Tipo.ENTREGA_REALIZADA);
            freteDAO.salvarOcorrencia(ocorrencia, conn);

            conn.commit();
            logger.info("Entrega registrada: " + frete.getNumero());
        } catch (NegocioException e) {
            ConnectionFactory.rollback(conn);
            throw e;
        } catch (Exception e) {
            ConnectionFactory.rollback(conn);
            throw new FreteException("Erro ao registrar entrega: " + e.getMessage(), e);
        } finally {
            ConnectionFactory.fechar(conn);
        }
    }

    public void registrarNaoEntrega(int idFrete, OcorrenciaFrete ocorrencia) throws NegocioException {
        Frete frete = buscarPorId(idFrete);

        if (frete.getStatus() != Frete.Status.EM_TRANSITO) {
            throw new FreteException("Somente fretes EM TRÂNSITO podem ter não entrega registrada.");
        }
        if (ocorrencia.getDescricao() == null || ocorrencia.getDescricao().trim().isEmpty()) {
            throw new FreteException("O motivo é obrigatório para registrar não entrega.");
        }
        validarOcorrencia(ocorrencia, frete);

        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false);

            freteDAO.atualizarStatus(idFrete, Frete.Status.NAO_ENTREGUE, null, null, conn);
            freteDAO.atualizarStatusVeiculo(frete.getVeiculo().getId(), "DISPONIVEL", conn);

            ocorrencia.setIdFrete(idFrete);
            ocorrencia.setTipo(OcorrenciaFrete.Tipo.TENTATIVA_ENTREGA);
            freteDAO.salvarOcorrencia(ocorrencia, conn);

            conn.commit();
        } catch (NegocioException e) {
            ConnectionFactory.rollback(conn);
            throw e;
        } catch (Exception e) {
            ConnectionFactory.rollback(conn);
            throw new FreteException("Erro ao registrar não entrega: " + e.getMessage(), e);
        } finally {
            ConnectionFactory.fechar(conn);
        }
    }

    public void cancelar(int idFrete) throws NegocioException {
        Frete frete = buscarPorId(idFrete);

        if (frete.getStatus() == Frete.Status.SAIDA_CONFIRMADA
                || frete.getStatus() == Frete.Status.EM_TRANSITO
                || frete.getStatus() == Frete.Status.ENTREGUE
                || frete.getStatus() == Frete.Status.NAO_ENTREGUE
                || frete.getStatus() == Frete.Status.CANCELADO) {
            throw new FreteException(
                "Não é possível cancelar um frete com status: " + frete.getStatus());
        }

        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false);
            freteDAO.atualizarStatus(idFrete, Frete.Status.CANCELADO, null, null, conn);
            conn.commit();
            logger.info("Frete cancelado: " + frete.getNumero());
        } catch (NegocioException e) {
            ConnectionFactory.rollback(conn);
            throw e;
        } catch (Exception e) {
            ConnectionFactory.rollback(conn);
            throw new FreteException("Erro ao cancelar frete: " + e.getMessage(), e);
        } finally {
            ConnectionFactory.fechar(conn);
        }
    }

    public void registrarOcorrencia(int idFrete, OcorrenciaFrete ocorrencia) throws NegocioException {
        Frete frete = buscarPorId(idFrete);

        if (!frete.podeReceberOcorrencia()) {
            throw new FreteException("Não é permitido registrar ocorrência em frete com status: "
                + frete.getStatus());
        }
        if (ocorrencia.getTipo() == OcorrenciaFrete.Tipo.ENTREGA_REALIZADA) {
            registrarEntrega(idFrete, ocorrencia);
            return;
        }
        validarOcorrencia(ocorrencia, frete);

        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false);
            ocorrencia.setIdFrete(idFrete);
            freteDAO.salvarOcorrencia(ocorrencia, conn);
            conn.commit();
        } catch (NegocioException e) {
            ConnectionFactory.rollback(conn);
            throw e;
        } catch (Exception e) {
            ConnectionFactory.rollback(conn);
            throw new FreteException("Erro ao registrar ocorrência: " + e.getMessage(), e);
        } finally {
            ConnectionFactory.fechar(conn);
        }
    }

    private void validarEmissao(Frete f) throws NegocioException {
        if (f.getRemetente() == null || f.getRemetente().getId() <= 0)
            throw new FreteException("Remetente é obrigatório.");
        if (f.getDestinatario() == null || f.getDestinatario().getId() <= 0)
            throw new FreteException("Destinatário é obrigatório.");
        if (f.getMotorista() == null || f.getMotorista().getId() <= 0)
            throw new FreteException("Motorista é obrigatório.");
        if (f.getVeiculo() == null || f.getVeiculo().getId() <= 0)
            throw new FreteException("Veículo é obrigatório.");
        if (f.getMunicipioOrigem() == null || f.getMunicipioOrigem().trim().isEmpty())
            throw new FreteException("Município de origem é obrigatório.");
        if (f.getUfOrigem() == null || f.getUfOrigem().trim().isEmpty())
            throw new FreteException("UF de origem é obrigatória.");
        if (f.getMunicipioDestino() == null || f.getMunicipioDestino().trim().isEmpty())
            throw new FreteException("Município de destino é obrigatório.");
        if (f.getUfDestino() == null || f.getUfDestino().trim().isEmpty())
            throw new FreteException("UF de destino é obrigatória.");
        if (f.getDataPrevisaoEntrega() == null)
            throw new FreteException("Data prevista de entrega é obrigatória.");
        if (!f.getDataPrevisaoEntrega().isAfter(LocalDate.now()))
            throw new FreteException("A data prevista de entrega deve ser posterior à data de hoje.");

        // Validar veículo disponível
        Veiculo veiculo = veiculoDAO.buscarPorId(f.getVeiculo().getId());
        if (veiculo.getStatus() != Veiculo.Status.DISPONIVEL)
            throw new FreteException("O veículo selecionado não está disponível (status: " + veiculo.getStatus() + ").");

        // Validar capacidade
        if (f.getPesoKg() != null && f.getPesoKg().doubleValue() > veiculo.getCapacidadeKg())
            throw new FreteException("O peso da carga (" + f.getPesoKg() + " kg) excede a capacidade do veículo ("
                + veiculo.getCapacidadeKg() + " kg).");

        // Validar motorista ativo e sem frete em andamento
        Motorista motorista = motoristaDAO.buscarPorId(f.getMotorista().getId());
        if (motorista.getStatus() != Motorista.Status.ATIVO)
            throw new FreteException("O motorista selecionado não está ativo.");
        if (motorista.isCnhVencida())
            throw new FreteException("A CNH do motorista está vencida.");
    }

    private void validarOcorrencia(OcorrenciaFrete oc, Frete frete) throws NegocioException {
        if (oc.getDataHora() == null)
            throw new FreteException("Data/hora da ocorrência é obrigatória.");

        LocalDateTime ultimaOcorrencia = freteDAO.buscarDataHoraUltimaOcorrencia(frete.getId());
        if (ultimaOcorrencia != null && !oc.getDataHora().isAfter(ultimaOcorrencia))
            throw new FreteException(
                "A data/hora da ocorrência deve ser posterior à última ocorrência registrada.");

        if (oc.getTipo() == OcorrenciaFrete.Tipo.AVARIA
                || oc.getTipo() == OcorrenciaFrete.Tipo.EXTRAVIO
                || oc.getTipo() == OcorrenciaFrete.Tipo.OUTROS) {
            if (oc.getDescricao() == null || oc.getDescricao().trim().isEmpty())
                throw new FreteException("Descrição é obrigatória para o tipo: " + oc.getTipo());
        }
    }
}